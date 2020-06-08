package com.kye.movieinformationapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<Movie> movieList;
    private LayoutInflater inflater;
    private Context context;

    public MyAdapter(Context context,ArrayList<Movie> list){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.movieList = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String url = "https://image.tmdb.org/t/p/w500" + movieList.get(position).getPoster_path();
        Glide.with(context).load(url).centerCrop().crossFade().into(holder.imageView);

        //리싸이클러뷰 각 아이템 터치 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.putExtra("title",movieList.get(position).getTitle());
                intent.putExtra("original_title",movieList.get(position).getOriginal_title());
                intent.putExtra("poster",movieList.get(position).getPoster_path());
                intent.putExtra("overview",movieList.get(position).getOverview());
                intent.putExtra("release_date",movieList.get(position).getRelease_date());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
