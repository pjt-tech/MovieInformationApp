package com.kye.movieinformationapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
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
    AlertDialog.Builder builder;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        movieList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        //처음 실행하면 최신영화정보를 띄움
        String search_url = "https://api.themoviedb.org/3/movie/upcoming?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7&language=ko-KR&page=1";
        String[] strings = {search_url};

        Mytask mytask = new Mytask();
        mytask.execute(strings);

        //리싸이클러뷰의 그리드를 사용하여 출력
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));

        builder = new AlertDialog.Builder(this);
    }


    @Override
    public void onBackPressed() {

        builder.setTitle("프로그램 종료").setMessage("정말 종료하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Snackbar.make(recyclerView,"종료가 취소되었습니다.",Snackbar.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //툴바의 SearchView 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        //SearchView 를 통하여 사용자의 입력과 영화의 제목이 맞는게 있다면 AsyncTask 를 통하여 다시 정보를 가져와 띄움

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setQueryHint("영화제목을 입력하세요.");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String search_url = "https://api.themoviedb.org/3/search/movie?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7&query="+query+"&language=ko-KR&page=1";
                String[] strings = {search_url};

                //OkHttp 쓰레드
                Mytask mytask = new Mytask();
                mytask.execute(strings);
                Toast.makeText(getApplicationContext(),query+"에 대한 영화를 검색했습니다.",Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }


    //툴바의 메뉴 아이템 클릭관련 설정
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_search:
                return true;

            case R.id.action_back:
                String search_url = "https://api.themoviedb.org/3/movie/upcoming?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7&language=ko-KR&page=1";
                String[] strings = {search_url};
                Mytask mytask = new Mytask();
                mytask.execute(strings);

                return true;

            case R.id.action_settings:
                Snackbar.make(recyclerView,"준비중..",Snackbar.LENGTH_LONG).show();
                return true;

                default:
                Toast.makeText(getApplicationContext(),"default..",Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }

    public class Mytask extends AsyncTask<String, Void, Movie[]>{

        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("\t로딩중...");
            dialog.show();

            movieList.clear();
        }

        @Override
        protected Movie[] doInBackground(String... strings) {

            //OkHttp , Gson 등을 이용하여 Json데이터를 gson으로 변환하여 자바객체(Movie)에 저장
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
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
                }
            //결과가 없을경우
            if(movies.length==0) {
                Snackbar.make(recyclerView,"검색결과가 없습니다.",Snackbar.LENGTH_LONG).show();
            }
                adapter = new MyAdapter(MainActivity.this,movieList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

        }
    }
}

