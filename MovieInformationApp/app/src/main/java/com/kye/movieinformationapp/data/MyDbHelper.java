package com.kye.movieinformationapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.kye.movieinformationapp.MainActivity;

public class MyDbHelper extends SQLiteOpenHelper {

    private Context context;
    private String tb_name = "favorite";


    public MyDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if(MainActivity.mail!=null){
            db.execSQL("drop table if exists " + tb_name);
            db.execSQL("create table if not exists " + tb_name + "(_id integer Primary key autoincrement," +
                    "mail text," +
                    "title text," +
                    "date text)");
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Toast.makeText(context,tb_name+" table Open.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
