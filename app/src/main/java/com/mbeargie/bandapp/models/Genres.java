package com.mbeargie.bandapp.models;


import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class Genres {

    public String genre1;
    public String genre2;
    public String genre3;
    public String genre4;
    public String genre5;
    public String genre6;
    public String genre7;
    public String genre8;
    public String genre9;
    public String genre10;

    public Genres() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Genres(String genre1,String genre2,String genre3,String genre4,String genre5,String genre6,String genre7,String genre8,String genre9,String genre10) {
        // Default constructor required for calls to DataSnapshot.getValue(Genres.class)
        this.genre1 = genre1;
        this.genre2 = genre2;
        this.genre3 = genre3;
        this.genre4 = genre4;
        this.genre5 = genre5;
        this.genre6 = genre6;
        this.genre7 = genre7;
        this.genre8 = genre8;
        this.genre9 = genre9;
        this.genre10 = genre10;

    }

}
