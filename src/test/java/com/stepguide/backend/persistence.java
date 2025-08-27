package com.stepguide.backend;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.fail;



public class persistence {

    // Properties 파일에서 읽기


    @BeforeAll
    public static void setup(){
        try{
            Properties props = new Properties();
            props.load(persistence.class.getResourceAsStream("/application.properties"));
            // 값 설정
            String driver = props.getProperty("spring.datasource.driver-class-name");
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("JDBC 드라이버 연결이 된다.")
    public void testConnection(){
        try {
            // Properties 파일에서 값 읽기
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/application.properties"));

            String url = props.getProperty("spring.datasource.url");
            String username = props.getProperty("spring.datasource.username");
            String password = props.getProperty("spring.datasource.password");

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
//                log.info(conn);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}


