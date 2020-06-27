package com.kye.movieinformationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.kye.movieinformationapp.data.ListItem;
import com.kye.movieinformationapp.data.ListItemView;
import com.kye.movieinformationapp.data.MyDbHelper;

import java.util.ArrayList;
import java.util.Collections;

public class FavoriteActivity extends AppCompatActivity {

    SQLiteDatabase db;
    MyDbHelper helper;
    Cursor cursor;
    ListView listView;
    String title,date,subMail,photoUrl;
    ArrayList<ListItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //즐겨찾기 화면 구현

        helper = new MyDbHelper(this,"favorite",null,DetailActivity.version);
        db = helper.getWritableDatabase();
        items = new ArrayList<>();

        String select = "select * from favorite";

        listView = findViewById(R.id.listView);
        MyListAdapter adapter = new MyListAdapter(items);
        listView.setAdapter(adapter);

        cursor = db.rawQuery(select,null);
        for(int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            subMail = cursor.getString(1);
            title = cursor.getString(2);
            date = cursor.getString(3);
            photoUrl = cursor.getString(4);
            if(subMail.equals(MainActivity.mail)){
                items.add(new ListItem(title,date,photoUrl));
            }
        }
        Collections.reverse(items);
    }

    public class MyListAdapter extends BaseAdapter{

        ArrayList<ListItem> items;

        public MyListAdapter(ArrayList<ListItem> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItemView view = null;
            if(convertView==null){
                view = new ListItemView(getApplicationContext());
            }else {
                view = (ListItemView)convertView;
            }
            ListItem item = items.get(position);
            view.setTitle("제목 : "+item.getTitle());
            view.setDate("개봉일 : " + item.getDate());
            view.setImageView(item.getPhotoUrl());
            return view;
        }
    }
}
