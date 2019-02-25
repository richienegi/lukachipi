package com.negi.wallpapers.myimages.Model;

public class All_Images {
    String id;
    String uid;
    String owner;
    String downloads;
    String date;
    String imageUrl;
    String thumb;

    public All_Images() {
    }

    public All_Images(String id, String uid, String owner, String downloads, String date, String imageUrl, String thumb) {
        this.id = id;
        this.uid = uid;
        this.owner = owner;
        this.downloads = downloads;
        this.date = date;
        this.imageUrl = imageUrl;
        this.thumb = thumb;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getOwner() {
        return owner;
    }

    public String getDownloads() {
        return downloads;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getThumb() {
        return thumb;
    }
}