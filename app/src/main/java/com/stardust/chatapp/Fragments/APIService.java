package com.stardust.chatapp.Fragments;

import com.stardust.chatapp.Notifications.MyResponse;
import com.stardust.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAACOJ-prY:APA91bEliD4vwnQNLQ-WO0b6BEy0KRKymxavK-8H-xF7vFlnaqo6hUZIm_xvjuPCOoBqWJB78lROBD30exBWUjiXMcZD1bU71sFFmNLzDqJRCE3CnrkTp1cSj9ge9uv-Kc0qgnxOWHbQ"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
