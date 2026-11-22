package com.capgemini.test.code.clients;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailNotificationRequest {
    private String email;
    private String message;
}