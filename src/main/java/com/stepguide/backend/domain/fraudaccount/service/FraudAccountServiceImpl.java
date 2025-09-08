package com.stepguide.backend.domain.fraudaccount.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepguide.backend.global.response.BaseException;
import com.stepguide.backend.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FraudAccountServiceImpl implements FraudAccountService {
    private final RestTemplate restTemplate;

    @Override
    public String checkFraudAccount(String accountNumber) {

        String url="https://cybercop.cyber.go.kr/countFraud.do" +
                "?fieldType=A&keyword=" + accountNumber + "&accessType=1";

        try{
            ResponseEntity<String> response=restTemplate.postForEntity(url, null, String.class);

            String body=response.getBody();

            //data(...) 제거
            body=body.replaceAll("^data\\((.*)\\)$", "$1");

            //ObjectMapper로 파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map=mapper.readValue(body, Map.class);

            boolean success=(Boolean)map.get("success");
            String message=(String)map.get("message");

            if(!success){
                if("Invalid number!".equals(message)){
                    throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
                }
                throw new BaseException(BaseResponseStatus.ACCOUNT_VERICATION_FAILED);
            }
            //html 제거
            message = message.replaceAll("<.*?>", "");

            return message;

        }
        catch(Exception e){
            throw new RuntimeException("사기계좌 조회 중 오류 발생: " + e.getMessage(), e);
        }





    }
}
