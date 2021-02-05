package org.isel.jingle.dto;

public class AlbumGetInfoDto {

    AlbumDto album;

    public AlbumGetInfoDto(AlbumDto album) {
        this.album = album;
    }

    public AlbumDto getAlbum() {
        return album;
    }
}
