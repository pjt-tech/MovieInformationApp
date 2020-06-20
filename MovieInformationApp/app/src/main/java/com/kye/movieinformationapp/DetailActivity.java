package com.kye.movieinformationapp;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kye.movieinformationapp.data.Youtube;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends YouTubeBaseActivity {

    TextView txt_title,txt_original_title,txt_release_date,txt_overview,txt_average,txt_popularity,txt_favorite;
    ImageView poster;
    //인텐트로 넘어온 영화&드라마 정보데이터 값
    String title,original_title,release_date,overview,img_poster,name,first_air_date,original_name,vote_average,popularity;
    String check = null;

    private ArrayList<Youtube> youtubelist;
    private boolean isSelector = false;

    private YouTubePlayerView youTubePlayerView;
    private String trail;   //youtube 키 값을 담을 변수
    private String id; // trailer 구분하기위한 Id 를 담을 변수
    boolean selector = true; // 드라마인지 영화인지 구분하기 위한 boolean 변수

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
        txt_average = findViewById(R.id.txt_average);
        txt_popularity = findViewById(R.id.txt_popularity);
        txt_favorite = findViewById(R.id.txt_favorite);
        poster = findViewById(R.id.poster);
        Button btn_search = findViewById(R.id.btn_search);
        Button btn_nation = findViewById(R.id.btn_nation);


        //Intent로 넘겨받은 데이터를 저장하고
        final Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        original_title = intent.getStringExtra("original_title");
        release_date = intent.getStringExtra("release_date");
        overview = intent.getStringExtra("overview");
        name = intent.getStringExtra("name");
        first_air_date = intent.getStringExtra("first_air_date");
        original_name = intent.getStringExtra("original_name");
        vote_average = intent.getStringExtra("vote_average");
        popularity = intent.getStringExtra("popularity");
        check = intent.getStringExtra("check");
        //poster
        img_poster = "https://image.tmdb.org/t/p/w500"+intent.getStringExtra("poster");
        Glide.with(this).load(img_poster).centerCrop().crossFade().into(poster);


        //저장한 데이터를 textView 설정 , 드라마 정보
        if(title==null||original_title==null||release_date==null||overview==null){
        txt_title.setText(name);
        txt_release_date.setText(first_air_date);
        txt_original_title.setText(original_name);
        txt_overview.setText(overview);
        txt_average.setText(vote_average);
        txt_popularity.setText(popularity+"명");
        selector = false;
        //영화 정보
        }else{
            txt_title.setText(title);
            txt_original_title.setText(original_title);
            txt_release_date.setText(release_date);
            txt_overview.setText(overview);
            txt_average.setText(vote_average);
            txt_popularity.setText(popularity+"명");
        }

        if(check==null){
            txt_favorite.setVisibility(View.GONE);
        }else {
            txt_favorite.setVisibility(View.VISIBLE);
        }

        txt_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isSelector!=true){
                    txt_favorite.setBackgroundResource(R.drawable.ic_action_favorite3);
                    Snackbar.make(poster,"즐겨찾기가 등록되었습니다.", BaseTransientBottomBar.LENGTH_SHORT).show();
                    isSelector = true;
                }else {
                    txt_favorite.setBackgroundResource(R.drawable.ic_action_favorite2);
                    Snackbar.make(poster,"즐겨찾기가 해제되었습니다.", BaseTransientBottomBar.LENGTH_SHORT).show();
                    isSelector = false;
                }
            }
        });

        //OkHttp
        YoutubeAsynctask asynctask = new YoutubeAsynctask();
        //id 값으로 구분하여 예고편을 틀어주기 위함 doInBackground 파라미터변수로 던져줌
        asynctask.execute(id);

        //구글 토렌트 검색
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String torrent = original_title + " " +title + " 토렌트";
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY,torrent);
                startActivity(intent);
            }
        });

        //구글 자막 검색
        btn_nation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nation = original_title + " " +title + " 자막";
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY,nation);
                startActivity(intent);
            }
        });

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

        //youtube trailer 영상 키 값을 가져오기위해 request요청을 해야함
        @Override
        protected Youtube[] doInBackground(String... strings) {
            Request request;
            String id = strings[0];
            //id에 해당하는 Json 데이터로 접근
            OkHttpClient client = new OkHttpClient();
            if(selector==true){
                request = new Request.Builder()
                        .url("https://api.themoviedb.org/3/movie/"+id+"/videos?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7")
                        .build();
            }else
                request = new Request.Builder()
                        .url("https://api.themoviedb.org/3/tv/"+id+"/videos?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7")
                        .build();
            try{
                //받아온 Json 데이터를 Gson 으로 변환 저장
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

            //arraylist 더해준뒤 playVideo() 메소드에 키 값 전달
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
