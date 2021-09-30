package com.example.whatsmytask.models;

public class User {

    private String id;
    private String email;
    private String userName;
    private String imageProfile;
    private long timestamp;

    //necesita un constructor vacio
    public User(){

    }

    // CONSTRUCTOR

    public User(String id, String email, String userName, String imageProfile, long timestamp) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.imageProfile = imageProfile;
        this.timestamp = timestamp;
    }


    // FIN CONSTRUCTOR

    // GETTER SETTER

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }
}
