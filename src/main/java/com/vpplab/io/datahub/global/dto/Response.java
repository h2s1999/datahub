package com.vpplab.io.datahub.global.dto;

import com.vpplab.io.datahub.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Response {

    @Getter
    @Builder
    public static class Body {

        private final LocalDateTime timestamp = LocalDateTime.now();
        private final int status;
        private final String error;
        private final String code;
        private final String message;
        private HttpStatus httpStatus;
    }

    public ResponseEntity<?> fail(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(Body
                        .builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(errorCode.getMessage())
                        .build()
                );

    }


    public ResponseEntity<?> fail(Body body) {
        return ResponseEntity
                .status(body.getHttpStatus())
                .body(body);
    }

}