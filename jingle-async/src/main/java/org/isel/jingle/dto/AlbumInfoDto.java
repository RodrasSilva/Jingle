package org.isel.jingle.dto;

public class AlbumInfoDto {

    private AlbumDto album;


    public AlbumInfoDto(AlbumDto album) {
        this.album = album;
    }

    public AlbumDto getAlbum() {
        return album;
    }
}
