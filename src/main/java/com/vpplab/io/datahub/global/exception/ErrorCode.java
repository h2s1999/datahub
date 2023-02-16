package com.vpplab.io.datahub.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 400 : BAD_REQUEST (잘못된 요청)
     */
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 유효하지 않습니다"),

    /**
     * 401 : UNAUTHORIZED (인증되지 않은 요청)
     */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "권한이 없는 사용자 입니다."),
    SESSION_TIMED_OUT_USER(HttpStatus.UNAUTHORIZED, "세션 시간이 만료되었습니다."),

    /**
     * 404 : NOT_FOUND(데이터나 페이지를 찾을 수 없음)
     */
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),

    /**
     * 409 : CONFLICT (요청이 서버의 상태와 충돌 될 경우)
     */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다"),

    /**
     * 500 : INTERNAL_SERVER_ERROR
     */
    FAILED_MLONE_DATA_ISSUE(HttpStatus.INTERNAL_SERVER_ERROR, "ML1 DATA 요청에 실패하였습니다."),
    FAILED_MLONE_TOKEN_ISSUE(HttpStatus.INTERNAL_SERVER_ERROR, "ML1 토큰 발급에 실패하였습니다."),
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패했습니다."),
    DATA_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JDBC 요청을 수행 중 에러가 발생하였습니다."),
    FAILED_SEND_SLACK(HttpStatus.INTERNAL_SERVER_ERROR, "슬랙 알람 전송에 실패였습니다."),
    FAILED_KPX_TOKEN_ISSUE(HttpStatus.INTERNAL_SERVER_ERROR, "KPX 토큰 발급에 실패하였습니다."),
    CREATE_EXCEL_FILE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "엑셀 파일 생성에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 오류")

    ;

    private final HttpStatus httpStatus;
    private final String message;

}
