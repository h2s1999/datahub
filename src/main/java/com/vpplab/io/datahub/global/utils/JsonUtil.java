package com.vpplab.io.datahub.global.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

public class JsonUtil {

    // 생성자
    public JsonUtil()
    {
        // 생성자 Code
    }


    /**
     * FuncName : JsonToMap()
     * FuncDesc : Json String -> Map 형태 변환
     * Param    : param : Json String
     * Return   : Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> JsonToMap(String param)
    {
        Gson gson = new Gson(); // 구글 JSON Parsing 라이브러리
        return gson.fromJson(param, new HashMap<String,Object>().getClass());
    }


    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> jsonToList(String param)
    {
        Gson gson = new Gson(); // 구글 JSON Parsing 라이브러리
        return gson.fromJson(param, new ArrayList<Map<String, String>>().getClass());
    }


    /**
     * FuncName : ListToJson()
     * FuncDesc : List -> Json String 변환
     * Param    : res : Json String
     * Return   : Json String
     */
    public static String ListToJson(List<Map<String, Object>> res)
    {
        Gson gson = new GsonBuilder().serializeNulls().create(); // 구글 JSON Parsing 라이브러리
        return gson.toJson(res);
    }



    /**
     * FuncName : JsonToLinkedHashMap()
     * FuncDesc : Json String -> LinkedHashMap 형태 변환(들어온 순서대로)
     * Param    : param : Json String
     * Return   : Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Object> JsonToLinkedHashMap(String param)
    {
        Gson gson = new Gson(); // 구글 JSON Parsing 라이브러리

        return gson.fromJson(param, new LinkedHashMap<String,Object>().getClass());
    }


    /**
     * FuncName : OneStringToJson()
     * FuncDesc : Json String 변환
     * Param    : sData : String
     * Return   : String
     */
    public static String OneStringToJson(String sData)
    {
        Gson gson = new Gson(); // 구글 JSON Parsing 라이브러리
        return gson.toJson(sData);
    }


    /**
     * FuncName : HashMapToJson()
     * FuncDesc : Json String 변환
     * Param    : sData : String
     * Return   : String
     */
    public static String HashMapToJson(HashMap<String, Object> map)
    {
        Gson gson = new Gson(); // 구글 JSON Parsing 라이브러리
        return gson.toJson(map);
    }


    /**
     * FuncName : MapToJson()
     * FuncDesc : Json String 변환
     * Param    : sData : String
     * Return   : String
     */
    public static String MapToJson(Map<String, Object> map)
    {
        Gson gson = new Gson(); // 구글 JSON Parsing 라이브러리
        return gson.toJson(map);
    }


    /**
     * FuncName : ObjectToJson()
     * FuncDesc : Object -> Json String 변환
     * Param    : res : Json String
     * Return   : Json String
     */
    public static String ObjectToJson(Object obj)
    {
        Gson gson = new GsonBuilder().serializeNulls().create(); // 구글 JSON Parsing 라이브러리

        return gson.toJson(obj);
    }
}
