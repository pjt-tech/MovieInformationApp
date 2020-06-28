package com.kye.movieinformationapp.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kye.movieinformationapp.R;


public class ListItemView extends LinearLayout {

    //LinearLayout 상속을 받기 때문에 객체 자체가 레이아웃임

    Context context;
    TextView title,date;
    ImageView imageView;

    public ListItemView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    //인플레이션과정
    public void init(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item2,this,true);
        title = view.findViewById(R.id.fv_title);
        date = view.findViewById(R.id.fv_date);
        imageView = view.findViewById(R.id.imageView);
    }


    public void setImageView(String photoUrl) {
        Glide.with(context).load(photoUrl).centerCrop().crossFade().into(imageView);
    }

    public void setTitle(String title) {
        this.title.setText(title);

    }

    public void setDate(String date) {
        this.date.setText(date);
    }
}
