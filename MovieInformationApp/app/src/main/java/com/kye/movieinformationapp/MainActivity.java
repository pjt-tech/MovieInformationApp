package com.kye.movieinformationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    ArrayList<Movie> movieList;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        movieList = new ArrayList<>();

        //OkHttp 쓰레드
        Mytask mytask = new Mytask();
        mytask.execute();

        //리싸이클러뷰의 그리드를 사용하여 출력
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
    }

    public class Mytask extends AsyncTask<String, Void, Movie[]>{

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
            //OkHttp , Gson 등을 이용하여 Json데이터를 gson으로 변환하여 자바객체(Movie)에 저장
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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);
            dialog.dismiss();

            //arraylist에 gson으로 변환된 객체를 하나씩 넣어주고 arraylist로 adapter를 설정하여 화면에 출력해준다.
            if(movies.length > 0){
                for(Movie p : movies){
                    movieList.add(p);
                }
                adapter = new MyAdapter(MainActivity.this,movieList);
                recyclerView.setAdapter(adapter);
            }
        }
    }
}

