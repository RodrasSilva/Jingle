package org.isel.jingle.dto;

public class TopTracksInfoDto {
    private TrackRankDto[] track;

    public TopTracksInfoDto(TrackRankDto[] track) {
        this.track = track;
    }

    public TrackRankDto[] getTrack() {
        return track;
    }
}
