package com.example.chatapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Adapters.NewsAdapter;
import com.example.chatapplication.R;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class StatusFragment extends Fragment {

    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    List<Article> articleList = new ArrayList<>();
    final String ApiKey = "f0100bee5e5244338721203aa8fc9c91 ";
    private View view;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void retrieveJSON(String country,String apikey) {
        NewsApiClient newsApiClient = new NewsApiClient(apikey);
       newsApiClient.getTopHeadlines(new TopHeadlinesRequest.Builder().country("us").language("en").build(), new NewsApiClient.ArticlesResponseCallback() {
           @Override
           public void onSuccess(ArticleResponse articleResponse) {
               articleList.clear();
               articleList = articleResponse.getArticles();
               newsAdapter = new NewsAdapter(getContext(),articleList);
               recyclerView.setAdapter(newsAdapter);
               //Toast.makeText(getContext(),articleList.get(1).getTitle(),Toast.LENGTH_LONG).show();
           }

           @Override
           public void onFailure(Throwable throwable) {
              Toast.makeText(getContext(),"Can't load Articles" + throwable.getMessage(),Toast.LENGTH_LONG).show();
           }
       });
    }

    public String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.toString().toLowerCase();
        return country;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view   =  inflater.inflate(R.layout.fragment_status, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.newsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String country = getCountry();
        retrieveJSON(country,ApiKey);

        return inflater.inflate(R.layout.fragment_status, container, false);
    }
}