package com.chaimm.ai.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaimm.ai.entity.Parameter;
import com.chaimm.ai.exception.CommonExp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/30 오후 10:31
 * @description
 */
public class ImageTool {

    public static void main(String[] args) throws Exception {
//        //이미지 파일을 읽어 BufferedImage 객체를 얻는다
//        BufferedImage bimg= ImageIO.read(new FileInputStream("/Users/chibozhou/Downloads/WechatIMG4.jpeg"));
//        //Graphics2D 객체를 얻는다
//        Graphics2D g2d=(Graphics2D)bimg.getGraphics();
//        //색상과 펜 굵기를 설정한다
//        g2d.setColor(Color.RED);
//        //도형이나 문자를 그린다
//        g2d.drawString("텍스트", 0, 0);
//        g2d.drawRect(215, 420, 330, 510);
//        //새 이미지를 저장한다
//        ImageIO.write(bimg, "JPG",new FileOutputStream("/Users/chibozhou/Downloads/aaa.jpg.jpeg"));

        ImageTool.recognizeFace("http://101.132.66.131:8080/upload/aaa.jpg.jpeg");
    }

    /**
     * 얼굴 이미지를 분석한다
     * @param url 얼굴 이미지 URL
     * @return 분석 결과
     */
    public static JSONObject recognizeFace(String url) throws Exception {
        // 얼굴 인식 인터페이스 호출
        String result = AESDecode.sendPost("http://dtplus-cn-shanghai.data.aliyuncs.com/face/attribute","{\"type\":0,\"image_url\":\""+url+"\"}");

        // 결과 파싱
        JSONObject json = JSONObject.parseObject(result);

//        System.out.println(json.toJSONString());
        // 얼굴이 존재하는지 판단
        Integer face_num = json.getInteger("face_num");
        if (face_num == 0) {
            throw new CommonExp("얼굴이 감지되지 않았습니다");
        }


        // 결과 반환
        return json;
    }

    /**
     * 이미지 위에 점을 그린다
     * @param json
     * @param file
     * @return 파일명
     */
    public static String paintImage(JSONObject json, File file) throws IOException {
        JSONArray faceRectArray = json.getJSONArray("face_rect");
        JSONArray landmarkArray = json.getJSONArray("landmark");

        // 이미지 파일을 읽어 BufferedImage 객체를 얻는다
        BufferedImage bimg= ImageIO.read(new FileInputStream(file));
        //Graphics2D 객체를 얻는다
        Graphics2D g2d=(Graphics2D)bimg.getGraphics();
        //색상과 펜 굵기를 설정한다
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke( 2.0f ));

        //얼굴 박스 그리기
        Integer x = (Integer) faceRectArray.get(0);
        Integer y = (Integer) faceRectArray.get(1);
        Integer width = (Integer) faceRectArray.get(2);
        Integer height = (Integer) faceRectArray.get(3);
        g2d.drawRect(x.intValue(), y.intValue(), width.intValue(), height.intValue());

        // 특징점 그리기
        for (int i=0; i<105; i++) {
            BigDecimal pointX = (BigDecimal) landmarkArray.get(i*2);
            BigDecimal pointY = (BigDecimal) landmarkArray.get(i*2+1);
            g2d.drawString(".", pointX.intValue(), pointY.intValue());
            g2d.drawRect(pointX.intValue(), pointY.intValue(), 3,3);
        }

        //새 이미지를 저장한다
        File resultFile = new File(Parameter.ABS_PATH+"ai/"+System.currentTimeMillis()/1000);
        ImageIO.write(bimg, "JPG",new FileOutputStream(resultFile));
        return resultFile.getName();
    }
}
