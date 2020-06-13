package com.kye.movieinformationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kye.movieinformationapp.data.Movie;
import com.kye.movieinformationapp.data.MyAdapter;
import com.kye.movieinformationapp.login.LoginActivity;
import com.kye.movieinformationapp.login.UserSettingActivity;

import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Movie> movieList;
    private MyAdapter adapter;
    private String startMode = "mv";
    private String photourl = null;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private AlertDialog.Builder builder;
    private NavigationView navigationView;
    private TextView nav_name,nav_mail;
    private ImageView nav_img;
    private Button btn_login;
    private FirebaseAuth auth;
    private Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);
        recyclerView = findViewById(R.id.recycler);
        drawerLayout = findViewById(R.id.drawerlayout);
        movieList = new ArrayList<>();
        builder = new AlertDialog.Builder(this);

        //처음 실행하면 최신영화정보를 띄움
        String search_url = "https://api.themoviedb.org/3/movie/upcoming?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7&language=ko-KR&page=1";
        String[] strings = {search_url};
        Mytask mytask = new Mytask();
        mytask.execute(strings);

        //리싸이클러뷰의 그리드를 사용하여 출력
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));

        //툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //서랍 꺼내고 닫기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger_menu); //서랍 햄버거 버튼설정

        //header 부분
        View header_View = navigationView.getHeaderView(0);
        nav_name = header_View.findViewById(R.id.nav_name);
        nav_mail = header_View.findViewById(R.id.nav_mail);
        nav_img = header_View.findViewById(R.id.nav_img);

        //header 로그인 클릭 이벤트
        btn_login = header_View.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_login.getText().equals("로그아웃")){
                    nav_img.setImageResource(R.drawable.profile);
                    nav_name.setText("비회원");
                    nav_mail.setText("이메일");
                    btn_login.setText("로그인");
                    auth.signOut();
                    Snackbar.make(recyclerView,"로그아웃 되었습니다.",BaseTransientBottomBar.LENGTH_SHORT).show();
                }else{
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent,1000);
                }
            }
        });

        //navigationView 바디부분 메뉴버튼 클릭 이벤트
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.nav_list :
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Snackbar.make(recyclerView,"Home.",BaseTransientBottomBar.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_review :
                        drawerLayout.closeDrawer(GravityCompat.START);
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/"));
                        startActivity(intent);
                        break;
                    case R.id.nav_book :
                        drawerLayout.closeDrawer(GravityCompat.START);
                        if(btn_login.getText().equals("로그인")){
                            Snackbar.make(recyclerView, "먼저 로그인을 해주세요.", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }else {
                            Snackbar.make(recyclerView,"즐겨찾기 구현중..",BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_settings :
                        if(btn_login.getText().equals("로그인")) {
                            Snackbar.make(recyclerView, "먼저 로그인을 해주세요.", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }else {
                            intent = new Intent(MainActivity.this, UserSettingActivity.class);
                            if(photourl!=null){
                                intent.putExtra("url",photourl);
                            }
                            startActivityForResult(intent,2000);
                        }
                        break;
                }
                return true;
            }
        });

    }

    //툴바의 SearchView 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        //SearchView 를 통하여 사용자의 입력과 영화 & 드라마 제목이 맞는게 있다면 AsyncTask 를 통하여 다시 정보를 가져와 띄움

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)searchItem.getActionView();

        searchView.setQueryHint("제목을 입력하세요.");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String search_url;

                if(startMode.equals("mv")){
                    search_url = "https://api.themoviedb.org/3/search/movie?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7&query="+query+"&language=ko-KR&page=1";
                    Toast.makeText(getApplicationContext(),query+"에 대한 영화를 검색했습니다.",Toast.LENGTH_SHORT).show();
                }else{
                    search_url = "https://api.themoviedb.org/3/search/tv?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7&query="+query+"&language=kR&page=1";
                    Toast.makeText(getApplicationContext(),query+"에 대한 드라마를 검색했습니다.",Toast.LENGTH_SHORT).show();
                }
                String[] strings = {search_url};

                //OkHttp 쓰레드
                Mytask mytask = new Mytask();
                mytask.execute(strings);

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

            case R.id.action_movie:
                //처음 최신영화 추천정보를 다시 받아옴
                String search_url = "https://api.themoviedb.org/3/movie/upcoming?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7&language=ko-KR&page=1";
                String[] strings = {search_url};
                Mytask mytask = new Mytask();
                mytask.execute(strings);
                Snackbar.make(recyclerView,"Movie transition", BaseTransientBottomBar.LENGTH_SHORT).show();
                startMode = "mv";
                return true;

            case R.id.action_tv:
                String search_url1 = "https://api.themoviedb.org/3/tv/popular?api_key=c8f97a0e5dbd6fd29d035cdfe7a8f4b7&language=ko-KR&page=1";
                String[] strings1 = {search_url1};
                Mytask mytask1 = new Mytask();
                mytask1.execute(strings1);
                Snackbar.make(recyclerView,"TV transition", BaseTransientBottomBar.LENGTH_SHORT).show();
                startMode = "tv";
                return true;

                default:
                    if (toggle.onOptionsItemSelected(item)) {
                        return true;
                    }
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            //뒤로가기버튼 클릭 시 이벤트 처리

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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000 && resultCode==RESULT_OK){
            String mail = data.getStringExtra("mail");
            photourl = data.getStringExtra("photo");
            if(photourl!=null){
                Glide.with(this).load(photourl).into(nav_img); //프로필이 있으면 가져옴
            }
            nav_name.setText("TMDB 회원입니다.");
            nav_mail.setText(mail+"으로 로그인하였습니다.");
            btn_login.setText("로그아웃");
        }else if(resultCode==2){
            nav_img.setImageResource(R.drawable.profile);
            nav_name.setText("비회원");
            nav_mail.setText("이메일");
            btn_login.setText("로그인");
            auth.signOut();
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
                //리싸이클러뷰의 변경된 정보를 적용
                adapter.notifyDataSetChanged();

        }
    }
}

