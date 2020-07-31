package org.jeecg.modules.aigx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 签名
 *
 * @author liwen
 */
public class SignUtil {
    private static final Logger logger = LoggerFactory.getLogger(SignUtil.class);

    /**
     * 获取云宝宝签名信息串
     *
     * @param data         待签名相关Param参数
     * @param clientSecret
     * @return
     */
    public static String generateYbbSignInfo(Map<String, String> data, String clientSecret) {
        String info = sortMapToStr(data);
        String target = String.format("%s&key=%s", info, clientSecret);
        Mac sha256HMAC;
        try {
            sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(clientSecret.getBytes("UTF-8"), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] array = sha256HMAC.doFinal(target.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param map
     * @return
     * @Description 将map数据按照key字典字母顺序排序转换成字符串输出，且将value为空值的键值对排除
     * @author rbw
     * @date 2017年8月8日上午10:57:08
     * @Title sortMapToStr
     */
    public static String sortMapToStr(Map<String, String> map) {
        if (null != map) {
            List<String> keys = new ArrayList<String>(map.keySet());
            // key排序
            Collections.sort(keys);

            StringBuilder authInfo = new StringBuilder();
            for (int i = 0; i < keys.size() - 1; i++) {
                String key = keys.get(i);
                String value = map.get(key);
                if ("sign".equals(key)) {
                    continue;
                }
                // 参数值为空，则不参与签名
                if (map.get(key).trim().length() > 0) {
                    authInfo.append(buildKeyValue(key, value, false));
                    authInfo.append("&");
                }
            }
            String tailKey = keys.get(keys.size() - 1);
            String tailValue = map.get(tailKey);
            authInfo.append(buildKeyValue(tailKey, tailValue, false));
            return authInfo.toString();
        }
        return "";
    }

    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }
}
