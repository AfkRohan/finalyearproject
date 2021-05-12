package com.example.chatapplication.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Adapters.NewsAdapter;
import com.example.chatapplication.Models.Headlines;
import com.example.chatapplication.Models.NewsAPIClient;
import com.example.chatapplication.Models.NewsAPIInterface;
import com.example.chatapplication.Models.OnRecyclerViewItemClickListener;
import com.example.chatapplication.R;
import com.kwabenaberko.newsapilib.models.Article;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StatusFragment extends Fragment implements OnRecyclerViewItemClickListener {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_status, container, false);
        recyclerView = view.findViewById(R.id.newsRecycleView);
        final RecyclerView mainRecycler = recyclerView.findViewById(R.id.newsRecycleView); // correct this line
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mainRecycler.setLayoutManager(linearLayoutManager);
        articleList = new ArrayList<>();
        final NewsAPIInterface apiService = NewsAPIClient.getClient().create(NewsAPIInterface.class);
        Call<Headlines> call = apiService.getLatestNews("in",ApiKey);
        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines>call, Response<Headlines> response) {
                if(response.body().getStatus().equals("ok")) {
                    articleList = response.body().getArticles();
                    if(articleList.size()>0) {
                        final  NewsAdapter mainArticleAdapter = new NewsAdapter(articleList);
                        mainArticleAdapter.setOnRecyclerViewItemClickListener(StatusFragment.this);
                        mainRecycler.setAdapter(mainArticleAdapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<Headlines>call, Throwable t) {
                Log.e("out", t.toString());
            }
        });
        return view;
    }

    @Override
    public void onItemClick(int position, View view) {
        String url = articleList.get(position).getUrl();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
     }
    }
