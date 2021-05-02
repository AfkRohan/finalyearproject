package com.example.chatapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.R;
import com.kwabenaberko.newsapilib.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<Article> articleArrayList;
    private Context context;

    public NewsAdapter(List<Article> articleArrayList) {
        this.articleArrayList = articleArrayList;
    }


    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemviewheadline, viewGroup, false);
        return new NewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Article articleModel = articleArrayList.get(position);
        viewHolder.titleText.setText(articleModel.getTitle());
        String url = articleModel.getUrlToImage();
        Picasso.get().load(url).into(viewHolder.imageView);
    }


    @Override
    public int getItemCount() {
        return articleArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView titleText;
        public ImageView imageView;
        public CardView artilceAdapterParentLinear;

        ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.NewsTitle);
            imageView = view.findViewById(R.id.headlineImage);
            artilceAdapterParentLinear = view.findViewById(R.id.newsCard);
        }

    }


}