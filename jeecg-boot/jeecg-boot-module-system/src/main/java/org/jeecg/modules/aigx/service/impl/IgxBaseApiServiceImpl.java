package org.jeecg.modules.aigx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.sf.saxon.expr.Component;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.aigx.entity.IgxUserInfo;
import org.jeecg.modules.aigx.mapper.IgxBaseApiMapper;
import org.jeecg.modules.aigx.service.IIgxBaseApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import static org.jeecg.modules.aigx.util.SignUtilTest.genSign;

/**
 * @auth Created by Administrator on 2020/7/15.
 */
@Service
public class IgxBaseApiServiceImpl extends ServiceImpl<IgxBaseApiMapper,IgxUserInfo> implements IIgxBaseApiService {

    @Autowired
    private RedisUtil redisUtil;

    @Value(value="${igx.igxUrl}")
    private String igxUrl;
    @Value(value="${igx.ClientId}")
    private String clientId;
    @Value(value ="${igx.ClientSecret}")
    private String clientSecret;

    private static final String igxToken = "igxToken";

    /**
     * 获取token
     * @param code
     * @return
     */
    @Override
    public Result<?> getIgxToken(String code) {
        log.debug("getIgxToken 入参：" + code);
        String res = "";
        String token = "";
        if(redisUtil.hasKey(igxToken)) {
            token = redisUtil.get(igxToken).toString();
            return Result.ok(token);
        }else {
            try {
                String url = "";
                CloseableHttpClient httpclient = createHttpClientWithNoSsl();
                if(oConvertUtils.isEmpty(code)) {
                    //3.2 通过client_credentials获取access_token
                    url = igxUrl + "/oauth/v2/token?client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=client_credentials&scope=openApi";
                }else{
                    //3.1 通过code获取access_token
                    url = igxUrl + "/oauth/v2/token?client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code + "&grant_type=authorization_code";
                }
                HttpPost httppost = new HttpPost(url);
//                httppost.setURI(URI.create(url));
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                //失败则循环三次
                for (int i = 0; i < 3; i++) {
                    HttpResponse response = httpclient.execute(httppost);
                    res = readResponse(response);
                    log.trace("getIgxToken  url: " + url + " 请求结果:" + res);
                    System.out.println("getIgxToken  url: " + url + " 请求结果:" + res);
                    if (oConvertUtils.isNotEmpty(res) && res.contains("\"code\":10000")) {
                        token = JSONObject.parseObject(res).getString("access_token");
                        String expiresIn = JSONObject.parseObject(res).getString("expires_in");
                        redisUtil.set(igxToken,token,Long.parseLong(expiresIn));
                        return Result.ok(token);
                    }
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Result.error(400,res);
    }
    /**
     * 根据clientId和client_secret获取access_token
     */
    public Result<?> getIgxToken() {
        String res = "";
        String token = "";
        if(redisUtil.hasKey(igxToken)) {
            token = redisUtil.get(igxToken).toString();
            return Result.ok(token);
        }else {
            try {
                String url = "";
                url = igxUrl + "/oauth/v2/token?client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=client_credentials&scope=openApi";
                CloseableHttpClient httpclient = createHttpClientWithNoSsl();
                HttpPost httppost = new HttpPost(url);
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                //失败则循环三次
                for (int i = 0; i < 3; i++) {
                    HttpResponse response = httpclient.execute(httppost);
                    res = readResponse(response);
                    log.debug("getIgxToken  url: " + url + " 请求结果:" + res);
                    if (oConvertUtils.isNotEmpty(res) && res.contains("\"code\":10000")) {
                        token = JSONObject.parseObject(res).getString("access_token");
                        String expiresIn = JSONObject.parseObject(res).getString("expires_in");
                        redisUtil.set(igxToken,token,Long.parseLong(expiresIn));
                        return Result.ok(token);
                    }
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Result.error(400,res);
    }

    /**
     * 根据accessToken查询用户基本信息
     * @return
     */
    @Override
    public Result<?> getIgxUserInfo(){
        Result getToken = getIgxToken("");
        String res = "";
        IgxUserInfo igxUserIn = new IgxUserInfo();
        if(getToken.isSuccess()) {
            String token = getToken.getMessage();

            //签名
            Map<String, String> queryParamMap = new HashMap<>();
            queryParamMap.put("access_token", "1234");
            String signStr = genSign(queryParamMap, clientId, clientSecret);
            log.debug("获取爱广西用户信息签名结果：" + signStr);
            try{
                String url = igxUrl + "/openapi/uc/user/v2/query?access_token=" + token;
                CloseableHttpClient httpclient = createHttpClientWithNoSsl();
                HttpGet httpget = new HttpGet(url);
                httpget.setHeader("authtication", signStr);
                HttpResponse response = httpclient.execute(httpget);
                res = readResponse(response);
                log.debug("getIgxToken  url: " + url + " 请求结果:" + res);
                if (oConvertUtils.isNotEmpty(res) && res.contains("\"code\":10000")) {
                    igxUserIn = JSON.parseObject(res, IgxUserInfo.class);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Result.ok(igxUserIn);
        }else {
            return Result.error(400,getToken.getMessage());
        }
    }

    /**
     * 读取 response body 内容为字符串
     *
     * @param response
     * @return
     * @throws IOException
     */
    private static String readResponse(HttpResponse response) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String result = new String();
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
        return result;
    }

    /**
     * 创建模拟客户端（针对 https 客户端禁用 SSL 验证）
     *
     * @param
     * @return
     * @throws Exception
     */
    private static CloseableHttpClient createHttpClientWithNoSsl() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // don't check
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // don't check
                    }
                }
        };

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustAllCerts, null);
        LayeredConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(ctx);
        return HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
    }
}
