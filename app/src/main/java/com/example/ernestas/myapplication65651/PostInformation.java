package com.example.ernestas.myapplication65651;

import android.net.Uri;

import java.util.Date;

/**
 * Created by Ernestas on 11/24/2016.
 */

public class PostInformation{
    public String postID;
    public String userID;
    public String postText;
    public String postDate;
    public String bit64;
    public String ratings;

    public PostInformation() {

    }

    public PostInformation(String postID, String userID, String postText, String postDate, String bit64, String ratings) {
        this.postID = postID;
        this.userID = userID;
        this.postText = postText;
        this.postDate = postDate;
        this.bit64 = bit64;
        this.ratings = ratings;

    }


    public String getUserID() {
        return userID;
    }

    public String getPostText() {
        return postText;
    }

    public String getBit64() {
        return bit64;
    }

    public String getPostID() { return postID; }

    public String getRatings() { return ratings; }

    public String getPostDate() { return postDate; }


    public void setPostText(String postText) {
        this.postText = postText;
    }




}
