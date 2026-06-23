package com.chaimm.ai;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chaimm.ai.entity.Parameter;
import com.chaimm.ai.entity.Result;
import com.chaimm.ai.exception.CommonExp;
import com.chaimm.ai.utils.HttpRequest;
import com.chaimm.ai.utils.ImageTool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static com.chaimm.ai.entity.Parameter.*;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/31 오전 11:37
 * @description
 */
@RestController
public class Controller {

//    @GetMapping("/")
    public String wxVerify(String signature, String timestamp, String nonce, String echostr){
        return echostr;
    }

    @GetMapping("getJSTicket")
    public String getJSTicket(){
        getAccessToken();
        getTicket();
        return Parameter.Ticket_Parameter;
    }

    private void getTicket() {
        //ticket 가져오기
        String param = "access_token="+ Parameter.AccessToken_Parameters+"&type=jsapi";
        String ticket_result = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket", param);
        String ticket = "";
        try {
            ticket = JSONObject.parseObject(ticket_result).getString("ticket");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Parameter.Ticket_Parameter = ticket;
        System.out.println("가져온 ticket="+ticket);
//                System.out.println("Parameter의 ticket="+Parameter.Ticket_Parameter);
    }

//    @GetMapping("getAccessToken")
    public String getAccessToken(){
        //access_token 가져오기
        String param = "grant_type=client_credential&appid="+APPID+"&secret="+SECRET;
        String access_token_result = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token", param);
        String access_token = "";
        try {
            access_token = JSONObject.parseObject(access_token_result).getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Parameter.AccessToken_Parameters = access_token;
        System.out.println("access_token="+access_token);
        return Parameter.AccessToken_Parameters;
    }


    @GetMapping("recognizeFace")
    public Result recognizeFace(String picId, String userToken){

//        String accessToken = getAccessToken();

        String fileName = HttpRequest.downloadByGet("http://file.api.weixin.qq.com/cgi-bin/media/get", "access_token="+Parameter.AccessToken_Parameters+"&media_id="+picId);
        System.out.println(fileName);

        //이미지 다운로드 실패 시: 1.관리자에게 알림, 2.pic_id를 DB에 저장해 관리자가 수동으로 다운로드하도록 함
        if(fileName==null){
            return Result.newFailResult("파일 업로드에 실패했습니다!");
        }

        try {
            // 이미지 인식
            JSONObject jsonObject = ImageTool.recognizeFace(URL_PATH+fileName);

            // 그리기
            String paintedFileName = ImageTool.paintImage(jsonObject, new File(Parameter.ABS_PATH+fileName));

            // 기존 사용자는 결과를 바로 반환
//            System.out.println("userToken="+userToken);
//            String resultURL = Parameter.userResultMap.get(userToken);
//            System.out.println("resultURL="+resultURL);
//            if (resultURL != null && !resultURL.equals("")) {
//                return buildResult(jsonObject, paintedFileName, resultURL);
//            }

            // TODO 분석 결과 생성
            String resultURL = createResultURL(userToken, jsonObject);

            // 반환 결과 구성
            return buildResult(jsonObject, paintedFileName, resultURL);
        } catch (CommonExp e) {
            e.printStackTrace();
            System.out.println(Result.newFailResult(e.getMessage()).toString());
            return Result.newFailResult(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Result.newFailResult("서버가 과부하 상태입니다! 잠시 후 다시 시도해 주세요").toString());
            return Result.newFailResult("서버가 과부하 상태입니다! 잠시 후 다시 시도해 주세요");
        }


    }

    private String createResultURL(String userToken, JSONObject jsonObject) throws IOException {

        String dirName = buildDirName(jsonObject);
        List<String> fileNameList_nengli = Parameter.resultMap_nengli.get(dirName);
        List<String> fileNameList_yanzhi = Parameter.resultMap_yanzhi.get(dirName);

        String fileName_yanzhi = fileNameList_yanzhi.get(new Random().nextInt(fileNameList_yanzhi.size()));
        String fileName_nengli = fileNameList_nengli.get(new Random().nextInt(fileNameList_nengli.size()));

        // TODO dirName을 인코딩해야 함
        String resultURL = Parameter.URL_PATH+"result/yanzhi/"+dirName+"/"+fileName_yanzhi + "," +
                Parameter.URL_PATH+"result/nengli/"+dirName+"/"+fileName_nengli;

//        Parameter.userResultMap.put(userToken, resultURL);
//        BufferedWriter buf_w = new BufferedWriter(new FileWriter(new File(Parameter.userFilePath),true));
//        buf_w.write(userToken+"#"+resultURL);
//        buf_w.newLine();
//        buf_w.close();

        return resultURL;
    }

    private String buildDirName(JSONObject jsonObject) {
        StringBuilder dirName = new StringBuilder();

        Integer gender = (Integer) jsonObject.getJSONArray("gender").get(0);
        Integer age = (Integer) jsonObject.getJSONArray("age").get(0);

        if (gender == 0) {
            dirName.append("nv_");
        } else {
            dirName.append("na_");
        }

        if (age<=15) {
            dirName.append("0_15");
        } else if (age>15 && age<=20) {
            dirName.append("15_20");
        } else if (age>20 && age<=30) {
            dirName.append("20_30");
        } else if (age>30 && age<=40) {
            dirName.append("30_40");
        } else if (age>40) {
            dirName.append("40_");
        }

        return dirName.toString();
    }

    private Result buildResult(JSONObject jsonObject, String paintedFileName, String resultURL) {
        Integer gender = (Integer) jsonObject.getJSONArray("gender").get(0);
        Integer age = (Integer) jsonObject.getJSONArray("age").get(0);
        Integer expression = (Integer) jsonObject.getJSONArray("expression").get(0);
        Integer glass = (Integer) jsonObject.getJSONArray("glass").get(0);

        Result result = new Result();
        result.setSuccess(true);

        if (gender.intValue() == 0) {
            result.setGender("여성");
        } else {
            result.setGender("남성");
        }

        if (glass.intValue() == 0) {
            result.setGlass("안경 미착용");
        } else {
            result.setGlass("안경 착용");
        }

        result.setAge(age.intValue());

        if (expression.intValue() == 1) {
            result.setExpression("미소");
        } else {
            result.setExpression("무표정");
        }

        result.setFaceUrl(Parameter.URL_PATH +"ai/"+ paintedFileName);
        result.setResultUrl(resultURL);
        System.out.println(result.toString());
        return result;
    }

    public static void main(String[] args) {
        try {
            // 이미지 인식
            JSONObject jsonObject = ImageTool.recognizeFace("http://www.chaimm.com:8080/upload/1515062968");

            // 그리기
            File file = new File("/Users/chibozhou/Downloads/WechatIMG4.jpeg");
            String paintedFileName = ImageTool.paintImage(jsonObject, file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
