package com.chaimm.ai.utils;

import com.chaimm.ai.entity.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


public class HttpRequest {
    /**
     * 지정한 URL로 GET 방식 요청을 보낸다
     *
     * @param url
     *            요청을 보낼 URL
     * @param param
     *            요청 파라미터. name1=value1&name2=value2 형식이어야 한다.
     * @return URL이 가리키는 원격 리소스의 응답 결과
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // URL과의 연결을 연다
            URLConnection connection = realUrl.openConnection();
            // 공통 요청 속성을 설정한다
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 실제 연결을 맺는다
            connection.connect();
            // 모든 응답 헤더 필드를 가져온다
            Map<String, List<String>> map = connection.getHeaderFields();
            // 모든 응답 헤더 필드를 순회한다
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // URL 응답을 읽기 위해 BufferedReader 입력 스트림을 정의한다
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("GET 요청 전송 중 예외 발생!" + e);
            e.printStackTrace();
        }
        // finally 블록에서 입력 스트림을 닫는다
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 지정한 URL로 POST 방식 요청을 보낸다
     *
     * @param url
     *            요청을 보낼 URL
     * @param param
     *            요청 파라미터. name1=value1&name2=value2 형식이어야 한다.
     * @return 원격 리소스의 응답 결과
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // URL과의 연결을 연다
            URLConnection conn = realUrl.openConnection();
            // 공통 요청 속성을 설정한다
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // POST 요청을 보내려면 아래 두 줄을 반드시 설정해야 한다
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // URLConnection 객체에 대응하는 출력 스트림을 가져온다
            out = new PrintWriter(conn.getOutputStream());
            // 요청 파라미터를 전송한다
            out.print(param);
            // 출력 스트림 버퍼를 flush 한다
            out.flush();
            // URL 응답을 읽기 위해 BufferedReader 입력 스트림을 정의한다
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("POST 요청 전송 중 예외 발생!"+e);
            e.printStackTrace();
        }
        //finally 블록에서 출력 스트림과 입력 스트림을 닫는다
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 이미지를 다운로드한다
     * @return 파일명
     */
    public static String downloadByGet(String url, String param){
        // 업로드 파일 디렉터리를 설정한다
        String uploadPath = Parameter.ABS_PATH;
//        String uploadPath = ServletActionContext.getServletContext().getRealPath("/upload");
        File uploadPathDir  = new File(uploadPath);
        if(!uploadPathDir.exists()){
            uploadPathDir.mkdirs();
        }
        // 대상 파일을 설정한다
        String fileName = System.currentTimeMillis()/1000+"";
        File toFile = new File(uploadPath, fileName);
        if(!toFile.exists()){
            try {
                toFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        //출력 스트림을 생성한다
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(toFile);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }

        String result = "";
        InputStream in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // URL과의 연결을 연다
            URLConnection connection = realUrl.openConnection();
            // 공통 요청 속성을 설정한다
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 실제 연결을 맺는다
            connection.connect();
            // 모든 응답 헤더 필드를 가져온다
            Map<String, List<String>> map = connection.getHeaderFields();
            // 모든 응답 헤더 필드를 순회한다
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // URL 응답을 읽기 위해 BufferedReader 입력 스트림을 정의한다
            //버퍼를 설정한다
            byte[] buffer = new byte[1024];
            int length = 0;
            in = connection.getInputStream();
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (Exception e) {
            System.out.println("GET 요청 전송 중 예외 발생!" + e);
            e.printStackTrace();
            return null;
        }
        // finally 블록에서 입력 스트림을 닫는다
        finally {
            try {
                if (in != null) {
                    in.close();
                    out.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }

        return fileName;
    }
}
