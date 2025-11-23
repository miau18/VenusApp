package com.example.vnus.ui.auth.notificacao;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type: application/json; charset=utf-8",
            "Authorization: Basic os_v2_app_2i55irevkzb2dozxsf4ownapbagpje6fjrse4buo5kodubnxnqexc5dbasb4q7vf72f23bsw2fj76piemeech2d4go47dkcm64eazgi"
    })
    @POST("notifications")
    Call<Object> sendNotification(@Body OneSignalNotification notification);
}