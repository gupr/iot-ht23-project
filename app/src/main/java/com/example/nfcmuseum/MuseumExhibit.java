package com.example.nfcmuseum;

import android.media.Image;

public class MuseumExhibit {

    private String title;
    private int year;
    private Image img;
    private int artistID;

    public MuseumExhibit(String title, int year, Image img, int artistID) {
        this.title = title;
        this.year = year;
        img = null;
        this.artistID = artistID;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public Image getImg() {
        return img;
    }

    public int getArtistID() {
        return artistID;
    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }
}
