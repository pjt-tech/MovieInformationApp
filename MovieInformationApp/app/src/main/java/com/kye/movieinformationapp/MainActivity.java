package com.kye.movieinformationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Movie> movieList;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        movieList = new ArrayList<>();

        MySynctask mytask = new MySynctask();
        mytask.execute();

        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));

    }

   class MySynctask extends AsyncTask<String,Void,Movie[]>{

        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("\t로딩중...");

            dialog.show();
        }

        @Override
        protected Movie[] doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/upcoming?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7&language=ko-KR&page=1")
                    .build();

            try{
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream()).getAsJsonObject().get("results");
                Movie[] posts = gson.fromJson(rootObject, Movie[].class);
                return posts;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        dialog.dismiss();

        if(movies.length > 0){
            for(Movie p : movies){
                movieList.add(p);
            }
        }
        adapter = new MyAdapter(MainActivity.this,movieList);
        recyclerView.setAdapter(adapter);
    }
}

}
