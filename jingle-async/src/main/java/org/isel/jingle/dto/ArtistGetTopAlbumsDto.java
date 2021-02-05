package org.isel.jingle.dto;

public class ArtistGetTopAlbumsDto {

    TopAlbumsDto topalbums;

    public ArtistGetTopAlbumsDto(TopAlbumsDto topalbums) {
        this.topalbums = topalbums;
    }

    public TopAlbumsDto getTopalbums() {
        return topalbums;
    }
}
