package com.example.nfcmuseum;

import android.media.Image;
import java.io.Serializable;

public class MuseumExhibit implements Serializable {

    private String title;
    private String year;
    private String img;
    private String artistID;
    private String exhibitID;
    private String exhibitDesc;
    private String exhibitHistory;
    private String exhibitSimilar;

    public MuseumExhibit(String exhibitID, String title, String year, String artistID, String  img, String exhibitDesc, String exhibitHistory, String exhibitSimilar) {
        this.title = title;
        this.year = year;
        this.img = img;
        this.artistID = artistID;
        this.exhibitID = exhibitID;
        this.exhibitDesc = exhibitDesc;
        this.exhibitHistory = exhibitHistory;
        this.exhibitSimilar = exhibitSimilar;
    }

    public String getExhibitDesc() {
        return exhibitDesc;
    }

    public String getExhibitHistory() {
        return exhibitHistory;
    }

    public String getExhibitSimilar() {
        return exhibitSimilar;
    }

    public void setExhibitDesc(String exhibitDesc) {
        this.exhibitDesc = exhibitDesc;
    }

    public void setExhibitHistory(String exhibitHistory) {
        this.exhibitHistory = exhibitHistory;
    }

    public void setExhibitSimilar(String exhibitSimilar) {
        this.exhibitSimilar = exhibitSimilar;
    }

    public String getExhibitID() {
        return exhibitID;
    }

    public void setExhibitID(String exhibitID) {
        this.exhibitID = exhibitID;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public String getArtistID() {
        return artistID;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }
}
