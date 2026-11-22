package com.capgemini.test.code.clients;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmsNotificationRequest {
    private String phone;
    private String message;
}