package com.kye.movieinformationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    TextView txt_title,txt_original_title,txt_release_date,txt_overview;
    ImageView poster;
    String title,original_title,release_date,overview,img_poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        poster = findViewById(R.id.poster);
        txt_title = findViewById(R.id.txt_title);
        txt_original_title = findViewById(R.id.txt_original_title);
        txt_release_date = findViewById(R.id.txt_release_date);
        txt_overview = findViewById(R.id.txt_overview);
        poster = findViewById(R.id.poster);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        original_title = intent.getStringExtra("original_title");
        release_date = intent.getStringExtra("release_date");
        overview = intent.getStringExtra("overview");
        img_poster = "https://image.tmdb.org/t/p/w500"+intent.getStringExtra("poster");

        Glide.with(this).load(img_poster).centerCrop().crossFade().into(poster);

        txt_title.setText(title);
        txt_original_title.setText(original_title);
        txt_release_date.setText(release_date);
        txt_overview.setText(overview);

    }
}
