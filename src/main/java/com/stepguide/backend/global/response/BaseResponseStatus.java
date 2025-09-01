package com.stepguide.backend.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum BaseResponseStatus {
    /**
     * 성공 코드 2xx
     * 코드의 원활한 이해을 위해 code는 숫자가 아닌 아래 형태로 입력해주세요.
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),


    // 공통 에러
    DATABASE_CONSTRAINT_ERROR(false, HttpStatus.BAD_REQUEST.value(), "데이터 제약조건 위반입니다."),
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 오류가 발생했습니다."),
    DATABASE_CONNECTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패했습니다."),
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."),
    FAIL_IMAGE_CONVERT(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Multipart 파일 전환에 실패했습니다."),

    // 400류 (요청 검증)
    BAD_REQUEST(false, HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
    BAD_REQUEST_INVALID_PARAM(false, HttpStatus.BAD_REQUEST.value(), "요청 파라미터가 유효하지 않습니다."),
    AUTH_AUTHORIZATION_CODE_MISSING(false, HttpStatus.BAD_REQUEST.value(), "인가코드가 없습니다."),
    AUTH_KAKAO_ACCESS_TOKEN_MISSING(false, HttpStatus.BAD_REQUEST.value(), "카카오 액세스 토큰이 없습니다."),
    AUTH_REFRESH_TOKEN_REUSED(false, 401, "리프레시 토큰이 이미 사용되었습니다."),

    //인증관련 에러
    AUTH_REFRESH_TOKEN_MISSING(false, HttpStatus.UNAUTHORIZED.value(), "리프레시 토큰이 없습니다."),
    AUTH_REFRESH_TOKEN_INVALID(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 리프레시 토큰입니다."),
    AUTH_REFRESH_TOKEN_EXPIRED(false, HttpStatus.UNAUTHORIZED.value(), "리프레시 토큰이 만료되었습니다."),
    AUTH_REFRESH_TOKEN_REVOKED(false, HttpStatus.UNAUTHORIZED.value(), "리프레시 토큰이 폐기되었습니다."),
    AUTH_ACCESS_TOKEN_INVALID(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 액세스 토큰입니다."),
    AUTH_ACCESS_TOKEN_EXPIRED(false, HttpStatus.UNAUTHORIZED.value(), "액세스 토큰이 만료되었습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    /**
     * isSuccess : 요청의 성공 또는 실패
     * code : Http Status Code
     * message : 설명
     */

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
