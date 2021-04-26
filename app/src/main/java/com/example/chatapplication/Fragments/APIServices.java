package com.example.chatapplication.Fragments;

import com.example.chatapplication.Notfications.MyResponse;
import com.example.chatapplication.Notfications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServices {
    @Headers(
        {
            "Content-Type:application/json",
                //"Authorization:key=AAAAyAOANFE:APA91bE7B_fRBJhLbOgeorEIwBu-7xgH-8Nc4LJgRqILPVm9BCCQH9L-DqvEdgux7Ml9bcI1SmhyR1IHmOjtOxRXBANc4tHanCbi8F647surDNNtMmdOylN9EEdfplX8gzrq-jRi4U4W"
                    "Authorization:key=AAAAyAOANFE:APA91bE7B_fRBJhLbOgeorEIwBu-7xgH-8Nc4LJgRqILPVm9BCCQH9L-DqvEdgux7Ml9bcI1SmhyR1IHmOjtOxRXBANc4tHanCbi8F647surDNNtMmdOylN9EEdfplX8gzrq-jRi4U4W"
        }
        )

    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body Sender body);

}
