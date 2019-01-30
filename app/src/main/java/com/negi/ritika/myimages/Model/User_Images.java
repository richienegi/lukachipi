package com.negi.ritika.myimages.Model;

public class User_Images {
    String id;
    String url;
    String status;
    String category;

    public User_Images(String id, String url, String status, String category) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.category = category;
    }

    public User_Images() {
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }
}
