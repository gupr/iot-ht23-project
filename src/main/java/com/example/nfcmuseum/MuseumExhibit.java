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
    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImg() {
        return img;
    }

    public String getArtistID() {
        return artistID;
    }
}
