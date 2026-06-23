package com.chaimm.ai.utils;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/31 오전 11:22
 * @description
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;
import javax.crypto.Mac;


@SuppressWarnings("restriction")
public class AESDecode {

    private static final String ak_id = "LTAIeoZJzkulyq53";
    private static final String ak_secret = "UwLQoJ5f0twzqFsar2SjKrvgbQMueT";

    /*
     * MD5+BASE64 계산
     */
    public static String MD5Base64(String s) {
        if (s == null) {
            return null;
        }

        String encodeStr = "";
        byte[] utfBytes = s.getBytes();
        MessageDigest mdTemp;
        try {
            mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(utfBytes);
            byte[] md5Bytes = mdTemp.digest();
            BASE64Encoder b64Encoder = new BASE64Encoder();
            encodeStr = b64Encoder.encode(md5Bytes);
        } catch (Exception e) {
            throw new Error("Failed to generate MD5 : " + e.getMessage());
        }
        return encodeStr;
    }


    /*
     * HMAC-SHA1 계산
     */
    public static String HMACSha1(String data, String key) {
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = (new BASE64Encoder()).encode(rawHmac);
        } catch (Exception e) {
            throw new Error("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }


    /*
     * JavaScript의 new Date().toUTCString()과 동일하다
     */
    public static String toGMTString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.UK);
        df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }


    /*
     * POST 요청 전송
     */
    public static String sendPost(String url, String body) throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        int statusCode = 200;
        try {
            URL realUrl = new URL(url);
            /*
             * http header 파라미터
             */
            String method = "POST";
            String accept = "application/json";
            String content_type = "application/json";
            String path = realUrl.getFile();
            String date = toGMTString(new Date());
            // 1.body를 MD5+BASE64로 암호화
            String bodyMd5 = MD5Base64(body);
            String stringToSign = method + "\n" + accept + "\n" + bodyMd5 + "\n" + content_type + "\n" + date + "\n"
                    + path;
            // 2.HMAC-SHA1 계산
            String signature = HMACSha1(stringToSign, ak_secret);
            // 3.authorization header 생성
            String authHeader = "Dataplus " + ak_id + ":" + signature;
            // URL과의 연결을 연다
            URLConnection conn = realUrl.openConnection();
            // 공통 요청 속성을 설정한다
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("content-type", content_type);
            conn.setRequestProperty("date", date);
            conn.setRequestProperty("Authorization", authHeader);
            // POST 요청을 보내려면 아래 두 줄을 반드시 설정해야 한다
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // URLConnection 객체에 대응하는 출력 스트림을 가져온다
            out = new PrintWriter(conn.getOutputStream());
            // 요청 파라미터를 전송한다
            out.print(body);
            // 출력 스트림 버퍼를 flush 한다
            out.flush();
            // URL 응답을 읽기 위해 BufferedReader 입력 스트림을 정의한다
            statusCode = ((HttpURLConnection)conn).getResponseCode();
            if(statusCode != 200) {
                in = new BufferedReader(new InputStreamReader(((HttpURLConnection)conn).getErrorStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (statusCode != 200) {
            throw new IOException("\nHttp StatusCode: "+ statusCode + "\nErrorMessage: " + result);
        }
        return result;
    }


    /*
     * GET 요청
     */
    public static String sendGet(String url) throws Exception {
        String result = "";
        BufferedReader in = null;
        int statusCode = 200;
        try {
            URL realUrl = new URL(url);
            /*
             * http header 파라미터
             */
            String method = "GET";
            String accept = "application/json";
            String content_type = "application/json";
            String path = realUrl.getFile();
            String date = toGMTString(new Date());
            // 1.body를 MD5+BASE64로 암호화
            // String bodyMd5 = MD5Base64(body);
            String stringToSign = method + "\n" + accept + "\n" + "" + "\n" + content_type + "\n" + date + "\n" + path;
            // 2.HMAC-SHA1 계산
            String signature = HMACSha1(stringToSign, ak_secret);
            // 3.authorization header 생성
            String authHeader = "Dataplus " + ak_id + ":" + signature;
            // URL과의 연결을 연다
            URLConnection connection = realUrl.openConnection();
            // 공통 요청 속성을 설정한다
            connection.setRequestProperty("accept", accept);
            connection.setRequestProperty("content-type", content_type);
            connection.setRequestProperty("date", date);
            connection.setRequestProperty("Authorization", authHeader);
            connection.setRequestProperty("Connection", "keep-alive");
            // 실제 연결을 맺는다
            connection.connect();
            // URL 응답을 읽기 위해 BufferedReader 입력 스트림을 정의한다
            statusCode = ((HttpURLConnection)connection).getResponseCode();
            if(statusCode != 200) {
                in = new BufferedReader(new InputStreamReader(((HttpURLConnection)connection).getErrorStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (statusCode != 200) {
            throw new IOException("\nHttp StatusCode: "+ statusCode + "\nErrorMessage: " + result);
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        // POST 요청 전송 예시
//        String ak_id1 = "NMV.............5jv"; //사용자 ak
//        String ak_secret1 = "Fgs...............3zu"; // 사용자 ak_secret
//        String url = "https://shujuapi.aliyun.com/org_code/service_code/api_name";
//        String body = "{\"param1\": \"xxx\", \"param2\":\"xxx\"}";
//        System.out.println("response body:" + sendPost(url, body, ak_id, ak_secret));
//        // GET 요청 전송
//        String ak_id1 = "NMV.............5jv"; //사용자 ak
//        String ak_secret1 = "Fgs...............3zu"; // 사용자 ak_secret
//        String url1 = "https://shujuapi.aliyun.com/org_code/service_code/api_name?param1=xxx&param2=xxx";
//        System.out.println("response body:" + sendGet(url1, ak_id1, ak_secret1));
    }
}
