package com.vpplab.io.datahub.domain.test;

import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class TestController {

    @Test
    public void test() throws ParseException {


        // ISO8601 -> date  변환
        /*String str = "2023-02-13T15:46:46+09:00";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date convDate = df.parse(str);
        SimpleDateFormat recvSimpleFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        SimpleDateFormat tranSimpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date reConvDate = recvSimpleFormat.parse(convDate.toString());
        String returDate = tranSimpleFormat.format(reConvDate);

        System.out.println("val convDate ->"+convDate);
        System.out.println("val returDate ->"+returDate);*/
        // 날짜 변환  unix timestamp -> date
        /*String dateStr = getTimestampToDate("1675872000");
        System.out.println(dateStr);*/

        // 날짜 변환 date -> ISO8601 : 결과 2023-02-09T15:03:05.335+09:00
        /*Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);// 내일

        Date td = calendar.getTime();
        String pt = "yyyy-MM-dd'T'HH:mm:ssXXX";
        String pts = "yyyy-MM-dd'T'"+"00:00:00"+"XXX";
        String pte = "yyyy-MM-dd'T'"+"00:00:00"+"XXX";
        Date tom = calendar.getTime();
        SimpleDateFormat sdftd = new SimpleDateFormat(pt);
        SimpleDateFormat sdftom = new SimpleDateFormat(pt);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String today = timezone("yyyy-MM-dd'T'HH:mm:ssXXX","");
        String tomorrowStart = timezone("yyyy-MM-dd'T'"+"00:00:00"+"XXX","tommorw");
        String tomorrowEnd = timezone("yyyy-MM-dd'T'"+"23:00:00"+"XXX","tommorw");
        System.out.println("1->"+today);
        System.out.println("2->"+tomorrowStart);
        System.out.println("3->"+tomorrowEnd);
        */

    }

    //private final TestService testService;

    /*@RequestMapping("/dashboard")
    public Map<String, Object> getDashboard(@RequestBody HashMap<String,Object> paramMap, HttpServletRequest request) {
        return testService.getDashboard(paramMap,request);
    }*/



    private String timezone(String pt, String tom){
        Calendar calendar = Calendar.getInstance();
        if (tom.equals("tommorw")) calendar.add(Calendar.DATE,1);

        Date tody = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(pt);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateformat = sdf.format(tody).toString();
        //System.out.println("1->"+today);
        return dateformat;
    }
    private static String getTimestampToDate(String timestampStr){
        long timestamp = Long.parseLong(timestampStr);
        Date date = new java.util.Date(timestamp*1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
