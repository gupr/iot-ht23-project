package com.example.nfcmuseum;

import android.media.Image;

public class ExhibitInfo {
    private String title; // Artist name etc.
    private String description;
    private Image img; // artist image or other related image

    public ExhibitInfo(String title, String description, Image img) {
        this.title = title;
        this.description = description;
        this.img = img;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImg(Image img) {
        this.img = img;
    }
}
