package com.kye.movieinformationapp.data;

public class ListItem {

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
