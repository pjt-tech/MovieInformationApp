package com.kye.movieinformationapp.data;

public class ListItem {

    //ListItemView layout 에 들어갈 정보들

    String title,date,photoUrl;

    public ListItem(String title,String date,String photoUrl){
        this.title = title;
        this.date = date;
        this.photoUrl = photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
