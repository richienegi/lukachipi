package com.negi.ritika.myimages.Model;

public class All_Images {
    String id;
    String url;
    String likes;
    String downloads;
    String time;
    String category;

    public All_Images(String id, String url, String likes, String downloads, String time, String category) {
        this.id = id;
        this.url = url;
        this.likes = likes;
        this.downloads = downloads;
        this.time = time;
        this.category = category;
    }

    public All_Images() {
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getLikes() {
        return likes;
    }

    public String getDownloads() {
        return downloads;
    }

    public String getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }
}
