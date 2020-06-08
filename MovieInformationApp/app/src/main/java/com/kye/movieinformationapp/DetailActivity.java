package com.kye.movieinformationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends YouTubeBaseActivity {

    TextView txt_title,txt_original_title,txt_release_date,txt_overview;
    ImageView poster;
    String title,original_title,release_date,overview,img_poster;

    ArrayList<Youtube> youtubelist;

    private YouTubePlayerView youTubePlayerView;
    private String trail;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        youtubelist = new ArrayList<>();

        poster = findViewById(R.id.poster);
        txt_title = findViewById(R.id.txt_title);
        txt_original_title = findViewById(R.id.txt_original_title);
        txt_release_date = findViewById(R.id.txt_release_date);
        txt_overview = findViewById(R.id.txt_overview);
        poster = findViewById(R.id.poster);

        //Intent로 넘겨받은 데이터를 저장하고
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        original_title = intent.getStringExtra("original_title");
        release_date = intent.getStringExtra("release_date");
        overview = intent.getStringExtra("overview");
        img_poster = "https://image.tmdb.org/t/p/w500"+intent.getStringExtra("poster");
        Glide.with(this).load(img_poster).centerCrop().crossFade().into(poster);


        //저장한 데이터를 textView 설정
        txt_title.setText(title);
        txt_original_title.setText(original_title);
        txt_release_date.setText(release_date);
        txt_overview.setText(overview);

        YoutubeAsynctask asynctask = new YoutubeAsynctask();
        asynctask.execute(id);

    }

    public void playVideo(final String videoId, YouTubePlayerView youtubePlayerView) {
            youtubePlayerView.initialize("AIzaSyDW5Htlck7yFEBWsZWYByZv6l81pMYkBwc", new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.cueVideo(videoId);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
    }

    public class YoutubeAsynctask extends AsyncTask<String, Void, Youtube[]>{

        @Override
        protected Youtube[] doInBackground(String... strings) {
            String id = strings[0];

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/"+id+"/videos?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7")
                    .build();

            try{
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream()).getAsJsonObject().get("results");
                Youtube[] posts = gson.fromJson(rootObject, Youtube[].class);
                return posts;

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Youtube[] youtubes) {
            super.onPostExecute(youtubes);

            if(youtubes.length>0){
                for(Youtube p : youtubes){
                    youtubelist.add(p);
                }
                trail = youtubelist.get(0).getKey();
                youTubePlayerView = findViewById(R.id.youtube_view);
                playVideo(trail,youTubePlayerView);
            }
        }
    }
}
