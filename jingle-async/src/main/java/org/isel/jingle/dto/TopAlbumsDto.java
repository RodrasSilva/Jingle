package org.isel.jingle.dto;

public class TopAlbumsDto {

    AlbumDto[] album;

    public TopAlbumsDto(AlbumDto[] album) {
        this.album = album;
    }

    public AlbumDto[] getAlbum() {
        return album;
    }
}
