package com.negi.wallpapers.myimages.Model;

public class User_Images {
    String id;
    String imageUrl;
    String status;
    String name;
    String uid;
    String thumb;

    public User_Images() {
    }

    public User_Images(String id, String imageUrl, String status, String name, String uid, String thumb) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.status = status;
        this.name = name;
        this.uid = uid;
        this.thumb = thumb;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getThumb() {
        return thumb;
    }
}
