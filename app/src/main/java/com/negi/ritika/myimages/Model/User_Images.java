package com.negi.ritika.myimages.Model;


public class User_Images {
    String id;
    String url;
    String status;
    String category;
    String name;

    public User_Images() {
    }

    public User_Images(String id, String url, String status, String category, String name) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.category = category;
        this.name = name;
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

    public String getName() {
        return name;
    }
}
