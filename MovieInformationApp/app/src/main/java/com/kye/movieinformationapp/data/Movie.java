package com.kye.movieinformationapp.data;

//movie,tv 정보를 담고 있는 객체
public class Movie {
    private String title;
    private String original_title;
    private String original_name;
    private String poster_path;
    private String overview;
    private String backdrop_path;
    private String release_date;
    private String id;
    private String name;
    private String first_air_date;
    private String vote_average;
    private String popularity;

    public Movie(String id ,String title,String original_title,String poster_path,String overview,String popularity,
                 String backdrop_path,String release_date,String name,String first_air_date,String original_name,String vote_average){
        this.id = id;
        this.title = title;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.backdrop_path = backdrop_path;
        this.release_date = release_date;
        this.name = name;
        this.first_air_date = first_air_date;
        this.original_name = original_name;
        this.vote_average = vote_average;
        this.popularity = popularity;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getName() {
        return name;
    }

    public String getFirst_air_date() {
        return first_air_date;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getPopularity() {
        return popularity;
    }
}
