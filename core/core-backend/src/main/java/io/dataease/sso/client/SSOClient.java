package io.dataease.sso.client;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.dataease.sso.exception.SSOException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Component
public class SSOClient {
    private static final Logger log = LoggerFactory.getLogger(SSOClient.class);

    @Value("${sso.appKey}")
    private String appKey;

    @Value("${sso.appToken}")
    private String appToken;

    @Value("${sso.api.getTicket}")
    private String getTicketUrl;

    @Value("${sso.api.verifyTicket}")
    private String verifyTicketUrl;

    @Value("${sso.api.logout}")
    private String logoutUrl;

    /**
     * 1. 换取正式登录态票据（/api/getTicket）
     * @param ssoServiceTicket 前端传来的临时票据
     * @param request 原始请求（用于获取IP、指纹、Header等）
     * @return 正式票据（REQ_DATA）
     */
    public String getTicket(String ssoServiceTicket, HttpServletRequest request) {
        System.out.println("222");
        long timestamp = System.currentTimeMillis();
        // 签名参数：token + timestamp + sso_service_ticket值
        String sign = generateSign(appToken, timestamp, ssoServiceTicket);

        // 准备请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("app", appKey);
        System.out.println("app:"+appKey);
        params.put("fp", getFpFromCookie(request));          // 从Cookie取指纹
        System.out.println("fp:"+getFpFromCookie(request));
        params.put("ip", getClientIp(request));
        System.out.println("ip:"+getClientIp(request));// 真实客户端IP
        params.put("url", request.getRequestURL().toString());
        System.out.println("url:"+request.getRequestURL().toString());
        params.put("time", String.valueOf(timestamp));
        System.out.println("time:"+String.valueOf(timestamp));
        params.put("sign", sign);
        System.out.println("sign:"+sign);
        params.put("sso_service_ticket", ssoServiceTicket);
        System.out.println("sso_service_ticket:"+ssoServiceTicket);// 注意参数名

        // 准备需要透传的Header
        Map<String, String> headers = buildHeaders(request);
//        System.out.println(headers);

//        System.out.println("URL:"+getTicketUrl);
        // 发起POST请求
//        cn.hutool.http.HttpRequest.closeCookie();
//        HttpRequest request1 = cn.hutool.http.HttpRequest.post(getTicketUrl)
//                .form(params)
//                .addHeaders(headers);
//        System.out.println("===== Hutool 请求详情 =====");
//        System.out.println("URL: " + request1.getUrl());
//        System.out.println("Method: " + request1.getMethod());
//        System.out.println("Headers: " + request1.headers()); // 打印所有请求头（Map）
//        System.out.println("Form Params: " + request1.form());
        String responseBody = HttpRequest.post(getTicketUrl)
                .form(params)
                .addHeaders(headers)
                .execute()
                .body();
//        HttpResponse response = request1.execute();
//        int status = response.getStatus();
//        String body = response.body();
//        System.out.println("Response Status: " + status);
//        System.out.println("Response Body: " + body);
        JSONObject respJson = JSON.parseObject(responseBody);

        int code = respJson.getIntValue("REQ_CODE");
        System.out.println("REQ_CODE"+code);
        if (code == 1) {
            System.out.println("赢！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
            return respJson.getString("REQ_DATA");  // 正式票据
        } else {
            log.error("getTicket failed, code: {}, msg: {}", code, respJson.getString("REQ_MSG"));
            throw new SSOException("获取正式票据失败: " + respJson.getString("REQ_MSG"));
        }
    }

    /**
     * 2. 验证登录态票据（/api/verifyTicket）
     * @param ticket 当前请求携带的正式票据（可从Cookie中获取）
     * @param request 原始请求
     * @return 用户信息（包含username）
     */
    public JSONObject verifyTicket(String ticket, HttpServletRequest request) {
        long timestamp = System.currentTimeMillis();
        String sign = generateSign(appToken, timestamp, ticket);

        Map<String, Object> params = new HashMap<>();
        params.put("app", appKey);
        params.put("fp", getFpFromCookie(request));
        params.put("ip", getClientIp(request));
        params.put("url", request.getRequestURL().toString());
        params.put("time", String.valueOf(timestamp));
        params.put("sign", sign);
        params.put("ticket", ticket);  // 参数名是 ticket

        Map<String, String> headers = buildHeaders(request);

        String responseBody = HttpRequest.post(verifyTicketUrl)
                .form(params)
                .addHeaders(headers)
                .execute()
                .body();
        JSONObject respJson = JSON.parseObject(responseBody);

        int code = respJson.getIntValue("REQ_CODE");
        if (code == 1) {
            return respJson.getJSONObject("REQ_DATA");
        } else {
            log.error("verifyTicket failed, code: {}, msg: {}", code, respJson.getString("REQ_MSG"));
            throw new SSOException("验证票据失败: " + respJson.getString("REQ_MSG"));
        }
    }

    /**
     * 3. 登出销毁票据（/api/logout）
     */
    public void logout(String ticket, HttpServletRequest request) {
        long timestamp = System.currentTimeMillis();
        String sign = generateSign(appToken, timestamp, ticket);

        Map<String, Object> params = new HashMap<>();
        params.put("app", appKey);
        params.put("fp", getFpFromCookie(request));
        params.put("ip", getClientIp(request));
        params.put("url", request.getRequestURL().toString());
        params.put("time", String.valueOf(timestamp));
        params.put("sign", sign);
        params.put("ticket", ticket);

        Map<String, String> headers = buildHeaders(request);

        String responseBody = HttpRequest.post(logoutUrl)
                .form(params)
                .addHeaders(headers)
                .execute()
                .body();
        JSONObject respJson = JSON.parseObject(responseBody);

        int code = respJson.getIntValue("REQ_CODE");
        if (code != 1) {
            log.error("logout failed, code: {}, msg: {}", code, respJson.getString("REQ_MSG"));
            throw new SSOException("登出失败: " + respJson.getString("REQ_MSG"));
        }
    }

    // ---------- 辅助方法 ----------
    private String generateSign(String token, long timestamp, String... params) {
        return getSign(token, timestamp, params);
    }

    private static String getSign(String token, Long timestamp, String... params) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(token);
        buffer.append(timestamp);
        if (params != null && params.length > 0) {
            for (String param : params) {
                buffer.append(param);
            }
        }
        return getMd5(buffer.toString());
    }

    private static String getMd5(String s) {
        return getMd5(s, "UTF-8");
    }

    private static String getMd5(String s, String charset) {
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes(charset);
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte per_byte = md[i];
                str[k++] = hexDigits[per_byte >>> 4 & 0xf];
                str[k++] = hexDigits[per_byte & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error("getMd5 err: {}", s, e);  // 使用类中的 log
            return "";
        }
    }

    private String getFpFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jdfp".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "";  // 若无指纹，传空字符串
    }

    private String getClientIp(HttpServletRequest request) {
        // 优先取 X-Forwarded-For，参考文档要求
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private Map<String, String> buildHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        // 必须设置的Header
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        // 透传客户端原始Header
        headers.put("User-Agent", request.getHeader("User-Agent"));
        headers.put("Origin", request.getHeader("Origin"));
        //headers.put("Host", request.getHeader("Host"));
        headers.put("Referer", request.getHeader("Referer"));
        headers.put("X-Forwarded-For", request.getHeader("X-Forwarded-For"));
        headers.put("J-Forwarded-For", request.getHeader("J-Forwarded-For"));
        // 如果还有其他要求，继续添加
        return headers;
    }
}
