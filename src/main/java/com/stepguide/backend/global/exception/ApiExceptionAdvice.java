package com.stepguide.backend.global.exception;



import com.stepguide.backend.global.response.BaseException;
import com.stepguide.backend.global.response.BaseResponse;
import com.stepguide.backend.global.response.BaseResponseStatus;
import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
@Order(2)
public class ApiExceptionAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException e) {
        return ResponseEntity
                .status(e.getStatus().getCode())
                .body(new BaseResponse<>(e.getStatus()));
    }

    // MyBatis + SQL 관련 예외 통합 처리
    @ExceptionHandler({MyBatisSystemException.class, PersistenceException.class, SQLException.class})
    public ResponseEntity<BaseResponse<Void>> handleDatabaseException(Exception e) {
        // 제약조건 위반인지 확인
        if (e.getMessage().contains("constraint") || e.getMessage().contains("Duplicate")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, 400, "데이터 제약조건 위반"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, 500, "데이터베이스 오류"));
    }

    // 파라미터 관련
    @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<BaseResponse<Void>> handleBadRequest(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, 400, e.getMessage()));
    }

    // 최종 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }
}
