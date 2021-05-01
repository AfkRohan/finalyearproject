package com.example.chatapplication.Models;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsAPIClient {
    private static NewsAPIClient newsAPIClient;
    private static final String BASE_URL = "https://newsapi.org/v2/";  //API url
    private static Retrofit retrofit;

    private NewsAPIClient(){
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized NewsAPIClient getInstance(){
        if(newsAPIClient==null){
            newsAPIClient = new NewsAPIClient();
            return newsAPIClient;
        }
        return newsAPIClient;
    }

    public NewsAPIInterface getApi(){
        return retrofit.create(NewsAPIInterface.class);
    }
}
