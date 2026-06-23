package com.chaimm.ai.init;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chaimm.ai.entity.Parameter;
import com.chaimm.ai.utils.AESDecode;
import com.chaimm.ai.utils.HttpRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

import static com.chaimm.ai.entity.Parameter.APPID;
import static com.chaimm.ai.entity.Parameter.SECRET;
import static com.chaimm.ai.entity.Parameter.userResultMap;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/31 오후 1:49
 * @description
 */
@Component
public class InitToken implements CommandLineRunner {

    @Override
    public void run(String... strings) throws Exception {
        //정시 스레드1: 1.5시간마다 access_token을 한 번씩 가져온다 5400000
//        getAccess_token(300000);

        //정시 스레드1: 1.5시간마다 ticket을 한 번씩 가져온다
//        getTicket(5400000);

        // 사용자 정보 로드
//        loadUser();

        // 분석 결과 로드
        loadResults();
    }

    private void loadResults() {
        // "외모(yanzhi)" 분석 결과 로드
        loadYanzhiResults();
        // "능력(nengli)" 분석 결과 로드
        loadNengliResults();

        System.out.println(JSONObject.toJSON(Parameter.resultMap_yanzhi).toString());
        System.out.println(JSONObject.toJSON(Parameter.resultMap_nengli).toString());
    }

    private void loadNengliResults() {
        Map<String, List<String>> results = loadFiles(Parameter.resultPath_nengli);
        Parameter.resultMap_nengli.putAll(results);
    }

    private void loadYanzhiResults() {
        Map<String, List<String>> results = loadFiles(Parameter.resultPath_yanzhi);
        Parameter.resultMap_yanzhi.putAll(results);
    }

    private Map<String, List<String>> loadFiles(String resultMap_yanzhi) {
        Map<String, List<String>> results = new HashMap<>();

        File dir = new File(resultMap_yanzhi);
        File[] fileDirs = dir.listFiles();
        if (fileDirs!=null && fileDirs.length>0) {
            for (File fileDir : fileDirs) {
                String[] fileNames = fileDir.list();
                if (fileNames!=null && fileNames.length>0) {
                    List<String> fileNameList = Arrays.asList(fileNames);
                    results.put(fileDir.getName(), fileNameList);
                }
            }
        }
        return results;
    }

    private void loadUser() throws IOException {
        BufferedReader buf_r = new BufferedReader(new FileReader(new File(Parameter.userFilePath)));
        String line = null;
        userResultMap.clear();
        while ((line=buf_r.readLine()) != null) {
            String[] results = line.split("#");
            Parameter.userResultMap.put(results[0],results[1]);
        }
        buf_r.close();

        System.out.println(JSONObject.toJSON(Parameter.userResultMap).toString());
    }


    /**
     * 정시 스레드: access_token 가져오기
     */
    private boolean getAccess_token(long time){
        if(time<=0) {
            return false;
        }

        Timer timer = new Timer();
        TimerTask task =new TimerTask(){
            public void run(){
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
                System.out.println("가져온 access_token="+access_token);
//                System.out.println("Parameter의 access_token="+Parameter.AccessToken_Parameters);

                getTicket();
            }
        };
        timer.scheduleAtFixedRate(task, new Date(),time);//현재 시각부터 시작해 매 n초 간격으로 다시 실행
        return true;
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


    /**
     * 정시 스레드: ticket 가져오기
     */
    private boolean getTicket(long time){
        if(time<=0) {
            return false;
        }

        Timer timer = new Timer();
        TimerTask task =new TimerTask(){
            public void run(){
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
        };
        timer.scheduleAtFixedRate(task, 10000,time);//현재 시각부터 시작해 매 n초 간격으로 다시 실행
        return true;
    }

}
