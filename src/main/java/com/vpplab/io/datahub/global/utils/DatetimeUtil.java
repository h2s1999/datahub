package com.vpplab.io.datahub.global.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DatetimeUtil {
    /**
     * unix timestamp -> Date 변환
     * @param timestampStr
     * @return
     */
    public static String getTimestampToDate(String timestampStr){
        long timestamp = Long.parseLong(timestampStr);
        Date date = new java.util.Date(timestamp*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /**
     * ISO8601 -> Date 변환
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String getIso8601ToDate(String dateStr) throws ParseException {
        String str = dateStr;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date convDate = df.parse(str);
        SimpleDateFormat recSimpleFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        SimpleDateFormat tranSimpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date reConvDate = recSimpleFormat.parse(convDate.toString());
        String returnDate = tranSimpleFormat.format(reConvDate);

        return returnDate;
    }

    /**
     * 오늘 날짜 기준으로 ISO8601 형태 날짜 조회 (Meteo 용)
     */
    public static String[] getDefaultForecastDatesBy8601(){
        DateTimeFormatter ymdFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currDateTime = LocalDateTime.now();

        String today = currDateTime.format(ymdFormat).toString()+":00:00";
        String today8601 = dateTo8601WithOffset("yyyy-MM-dd'T'HH:mm:ssXXX","");
        String tomorrowStart = dateTo8601WithOffset("yyyy-MM-dd'T'"+"00:00:00"+"XXX","tommorw");
        String tomorrowEnd = dateTo8601WithOffset("yyyy-MM-dd'T'"+"23:00:00"+"XXX","tommorw");
        String[] defaultDate = new String[]{today8601, tomorrowStart, tomorrowEnd, today};
        return defaultDate;
    }

    /**
     * Date -> ISO8601 변환
     * @param pt
     * @param tom
     * @return
     */
    private static String dateTo8601WithOffset(String pt, String tom){
        Calendar calendar = Calendar.getInstance();
        if (tom.equals("tommorw")) calendar.add(Calendar.DATE,1);

        Date day = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(pt);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateformat = sdf.format(day).toString();

        return dateformat;
    }
}
