package com.vpplab.io.datahub.global.exception;

import com.vpplab.io.datahub.global.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Response response;

    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class,
            DataIntegrityViolationException.class})
    protected ResponseEntity<?> handleDataException() {
        log.error("handleDataException throw Exception : {}", ErrorCode.DUPLICATE_RESOURCE);
        return response.fail(ErrorCode.DUPLICATE_RESOURCE);
    }

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<?> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return response.fail(e.getErrorCode());
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<?> handleCustomException(Exception e) {
        log.error("handleException throw Exception : {}", e);
        return response.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }

}
