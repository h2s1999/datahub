package com.vpplab.io.datahub.global.utils;

import lombok.experimental.UtilityClass;

/**
 * @author archmagece
 * @since 2017-08-30
 */
@UtilityClass

public class PagingUtil {


    /**
     * 페이징설정
     */
    public static int pageCnt(int totCnt , int rowCnt) {
        int totPageCnt = 1;
        totPageCnt = totCnt / rowCnt;
        if((totCnt % rowCnt) != 0 ){
            totPageCnt =  totPageCnt + 1;
        }

        return totPageCnt;
    }
    /**
     * 페이징설정
     * 조회갯수 rowCnt
     * 현재페이지 pageNum
     * return schPageNo 조회된페이지
     * LIMIT 조회된페이지 ,조회갯수
     */


    public static int schPageNo(int pageNo , int rowCnt) {

        int schPageNo =  ((pageNo - 1) * rowCnt);
        return schPageNo;
    }
    /**
     * @param args
     */
    public static void main(String[] args) throws  Exception {


    }



}
