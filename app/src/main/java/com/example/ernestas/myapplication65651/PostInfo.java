package com.example.ernestas.myapplication65651;

/**
 * Created by Divecoke on 1/12/2017.
 */

public class PostInfo {
    public String postID;
    public String userID;
    public String postText;
    public String postDate;
    public String bit64;
    public String ratings;
    public String username;
    public String profilePicture;

    public PostInfo() {

    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public PostInfo(String postID, String userID, String postText, String postDate, String bit64, String ratings, String username, String profilePicture) {
        this.postID = postID;
        this.userID = userID;
        this.postText = postText;
        this.postDate = postDate;
        this.bit64 = bit64;
        this.ratings = ratings;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getUsername() { return username; }

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

    public void setUsername(String username) {
        this.username = username;
    }

}
