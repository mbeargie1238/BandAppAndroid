package com.mbeargie.bandapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties

public class Register {

    public String bandname;
    public String city;


    public Register() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Register(String bandname, String bandcity) {
        this.bandname = bandname;
        this.city = city;

    }

}
