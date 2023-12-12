package com.example.nfcmuseum;

import java.io.Serializable;
import java.util.List;

public class ExhibitInfo implements Serializable {
    private String description;
    private String history;
    private List<String> similar;

    public ExhibitInfo(String description, String history, List<String> similar) {
        this.description = description;
        this.history = history;
        this.similar = similar;
    }

    public List<String> getSimilar() {
        return similar;
    }

    public String getDescriptionInfoCard() {return description;}

    public String getHistoryInfoCard() {return history;}

}