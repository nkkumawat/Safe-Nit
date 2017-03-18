package com.example.sonu.safenit1;

import android.content.Context;

import javax.sql.StatementEvent;

/**
 * Created by sonu on 6/3/17.
 */

public class dataRetreve {
    private String title;
    private String disc;
    private String imageurl;
    private  String username;
    private  String profilepic;
    private  String uid;
    public dataRetreve() {

    }
    public dataRetreve(String title, String disc, String imageurl, String uid , String username , String profilepic ) {
        this.title = title;
        this.disc = disc;
        this.imageurl = imageurl;
        this.username = username;
        this.profilepic = profilepic;
        this.uid = uid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl( String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String tilte) {
        this.title = tilte;
    }

    public String getUid() {
            return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisc() {
        return disc;
    }


    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }
}
