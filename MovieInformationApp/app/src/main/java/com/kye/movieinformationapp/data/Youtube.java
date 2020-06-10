package com.kye.movieinformationapp.data;

public class Youtube {

    //영화 아이디를 받아서 다시 리퀘스트를 해야함 이때 데이터를 담을 클래스

    private String id;
    private String key;
    private String name;
    private String size;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }
}

