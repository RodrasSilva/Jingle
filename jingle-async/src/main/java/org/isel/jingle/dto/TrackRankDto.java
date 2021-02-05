package org.isel.jingle.dto;

import com.google.gson.annotations.SerializedName;

public class TrackRankDto {
    private String name;
    private String url;
    private int duration;

    @SerializedName("@attr")
    private AttrDto attr;

    public TrackRankDto(String name, String url, int duration, AttrDto attr) {
        this.name = name;
        this.url = url;
        this.duration = duration;
        this.attr = attr;
    }


    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getDuration() {
        return duration;
    }

    public AttrDto getAttr() {
        return attr;
    }
}
