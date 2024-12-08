package com.example.fittarget.APIResponses;

import com.example.fittarget.objects.User;

public class SignInResponse {
    private String message;
    public User user;

    // Getter for the message
    public String getMessage() {
        return message;
    }
    public User getUser() {
        return user;
    }
}

