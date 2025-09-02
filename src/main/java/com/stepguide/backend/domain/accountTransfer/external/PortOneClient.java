package com.stepguide.backend.domain.accountTransfer.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PortOneClient {

    private final RestTemplate restTemplate = new RestTemplate();

    // application.properties에서 가져오기
    @Value("${portone.imp_key}")
    private String impKey="4044533133324330";

    @Value("${portone.imp_secret}")
    private String impSecret="l5PfJkt3pDUO2SGC6y1GYEPK4zqfWiwvHbVTEYtpJSlaSIVJsOJ8z3lDy69nfbK4pBKN7VLbMrS4IRYg";

    // 1) 토큰 발급
    public String getAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";

        Map<String, String> body = new HashMap<>();
        body.put("imp_key", impKey);
        body.put("imp_secret", impSecret);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || !responseBody.containsKey("response")) {
            throw new IllegalStateException("PortOne 토큰 발급 실패");
        }

        Map<String, Object> resp = (Map<String, Object>) responseBody.get("response");
        return (String) resp.get("access_token");
    }

    // 2) 계좌 예금주 조회
    public String getAccountHolderName(String bankCode, String accountNumber) {
        String accessToken = getAccessToken();  // 토큰 발급

        String url = String.format(
                "https://api.iamport.kr/vbanks/holder?bank_code=%s&bank_num=%s",
                bankCode, accountNumber
        );
        System.out.println("요청  url = " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        // 응답 전체 로그
        System.out.println("PortOne 응답 전체: " + response);

        Map<String, Object> responseBody = response.getBody();
        System.out.println("PortOne 응답 바디: " + responseBody);

        if (responseBody == null || !responseBody.containsKey("response")) {
            throw new IllegalArgumentException("계좌 조회 실패");
        }

        Map<String, Object> resp = (Map<String, Object>) responseBody.get("response");

        System.out.println("계좌 예금주 정보: " + resp);

        return (String) resp.get("bank_holder");  // 예금주명 반환

    }

}
