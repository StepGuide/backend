package com.stepguide.backend.domain.FireBase.service;
import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class PushSender {

    public String sendToToken(String fcmToken, String title, String body, String clickUrl) throws Exception {
        WebpushNotification notification = WebpushNotification.builder()
                .setTitle(title)
                .setBody(body)
                .setIcon("/favicon.ico")
                .build();

        WebpushFcmOptions fcmOptions = WebpushFcmOptions.withLink(clickUrl);

        WebpushConfig webpush = WebpushConfig.builder()
                .setNotification(notification)
                .putHeader("TTL", "86400") // 1일
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setWebpushConfig(webpush)
                .putData("url", clickUrl)
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }
}