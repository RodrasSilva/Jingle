package org.isel.jingle.dto;

public class TopTracksDto {
    private TopTracksInfoDto tracks;

    public TopTracksDto(TopTracksInfoDto tracks) {
        this.tracks = tracks;
    }

    public TopTracksInfoDto getTracks() {
        return tracks;
    }
}
