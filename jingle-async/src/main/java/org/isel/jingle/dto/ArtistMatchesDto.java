package org.isel.jingle.dto;

public class ArtistMatchesDto {

    private ArtistDto[] artist;

    public ArtistMatchesDto(ArtistDto[] artist) {
        this.artist = artist;
    }

    public ArtistDto[] getArtist() {
        return artist;
    }
}
