package com.negi.ritika.myimages.Model;

public class User_Images {
    String uid;
    String id;
    String url;
    String status;
    String category;

    public User_Images() {
    }

    public User_Images(String uid, String id, String url, String status, String category) {
        this.uid = uid;
        this.id = id;
        this.url = url;
        this.status = status;
        this.category = category;
    }

    public String getUid() {
        return uid;
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
