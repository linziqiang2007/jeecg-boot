package org.jeecg.modules.aigx.util;


import java.util.HashMap;
import java.util.Map;

/**
 * @author fangwh
 * @date 2019/7/25 10:37
 */
public class SignUtilTest {


    /**
     * 生成签名
     * @param queryParamMap url参数
     * @param clientId
     * @param clientSecret
     * @return
     */
    public static String genSign(Map<String,String> queryParamMap,String clientId,String clientSecret) {

        String queryParam = SignUtil.sortMapToStr(queryParamMap);

        // 处理头部签名
        Map<String, String> signMap = new HashMap<String, String>();
        signMap.put("client_id", clientId);
        String now = String.valueOf(System.currentTimeMillis());
        signMap.put("time", now);
        signMap.put("queryParam", queryParam);
        String sign = SignUtil.generateYbbSignInfo(signMap, clientSecret);
        String authtication = String.format("time=%s,client_id=%s,sign=%s", now,clientId, sign);
        return authtication;
    }

    public static void main(String[] args) {
        Map<String, String> queryParamMap = new HashMap<>();
        queryParamMap.put("access_token", "2f41ed273975f4797105485424a8f40c");
        String clientId="lhC8ikdp0BKDugdzDGob";
        String clientSecret = "4xXnsun7iCfbLCQopBQI";
        String s = genSign(queryParamMap, clientId, clientSecret);
        System.out.println("签名串："+ s);
    }
}
