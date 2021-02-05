/*
 * GNU General Public License v3.0
 *
 * Copyright (c) 2019, Miguel Gamboa (gamboa.pt)
 *
 *   All rights granted under this License are granted for the term of
 * copyright on the Program, and are irrevocable provided the stated
 * conditions are met.  This License explicitly affirms your unlimited
 * permission to run the unmodified Program.  The output from running a
 * covered work is covered by this License only if the output, given its
 * content, constitutes a covered work.  This License acknowledges your
 * rights of fair use or other equivalent, as provided by copyright law.
 *
 *   You may make, run and propagate covered works that you do not
 * convey, without conditions so long as your license otherwise remains
 * in force.  You may convey covered works to others for the sole purpose
 * of having them make modifications exclusively for you, or provide you
 * with facilities for running those works, provided that you comply with
 * the terms of this License in conveying all material for which you do
 * not control copyright.  Those thus making or running the covered works
 * for you must do so exclusively on your behalf, under your direction
 * and control, on terms that prohibit them from making any copies of
 * your copyrighted material outside their relationship with you.
 *
 *   Conveying under any other circumstances is permitted solely under
 * the conditions stated below.  Sublicensing is not allowed; section 10
 * makes it unnecessary.
 *
 */

package org.isel.jingle;

import com.google.gson.Gson;
import io.reactivex.Observable;
import java.util.concurrent.CompletableFuture;
import org.isel.jingle.dto.*;
import org.isel.jingle.util.req.Request;

import java.util.stream.Collectors;


public class LastfmWebApi {

    private static final String LASTFM_API_KEY = "1791bbb536e0d3647cf1502e007b45ce";
    private static final String LASTFM_HOST = "http://ws.audioscrobbler.com/2.0/";
    private static final String LASTFM_SEARCH = LASTFM_HOST
            + "?method=artist.search&format=json&artist=%s&page=%d&api_key="
            + LASTFM_API_KEY;

    private static final String LASTFM_GET_ALBUMS = LASTFM_HOST
            + "?method=artist.gettopalbums&format=json&mbid=%s&page=%d&api_key="
            + LASTFM_API_KEY;

    private static final String LASTFM_GET_ALBUM_INFO = LASTFM_HOST
            + "?method=album.getinfo&format=json&mbid=%s&api_key="
            + LASTFM_API_KEY;
    private static final String LASTFM_GET_TOP_TRACKS = LASTFM_HOST
            + "?method=geo.gettoptracks&country=%s&page=%d&format=json&api_key="
            + LASTFM_API_KEY;


    protected final Gson gson;
    private final Request request;

    public LastfmWebApi(Request request) {
        this(request, new Gson());
    }

    public LastfmWebApi(Request request, Gson gson) {
        this.request = request;
        this.gson = gson;
    }

    public CompletableFuture<ArtistDto[]> searchArtist(String name, int page) {
        String url = String.format(LASTFM_SEARCH, name, page);
        return request.getLines(url).thenApply(s -> {
            ArtistSearchDto artistSearch = gson.fromJson(s, ArtistSearchDto.class);
            return artistSearch.getResults().getArtistmatches().getArtist();
        });
    }

    public  CompletableFuture<AlbumDto[]> getAlbums(String artistMbid, int page) {
        if(artistMbid==null){
            return CompletableFuture.completedFuture(new AlbumDto[0]);
        }
        String url = String.format(LASTFM_GET_ALBUMS, artistMbid, page);
        return request.getLines(url).thenApply(s -> {
            ArtistGetTopAlbumsDto getTopAlbums = gson.fromJson(s, ArtistGetTopAlbumsDto.class);
            return getTopAlbums.getTopalbums().getAlbum();
        });
    }

    public CompletableFuture<TrackDto[]> getAlbumInfo(String albumMbid) {
        if(albumMbid==null)
            return CompletableFuture.completedFuture(new TrackDto[0]);

        String url = String.format(LASTFM_GET_ALBUM_INFO, albumMbid);
        return request.getLines(url).thenApply(s->{
            AlbumGetInfoDto albumInfo = gson.fromJson(s, AlbumGetInfoDto.class);
            return albumInfo.getAlbum().getTracks().getTracks();
        });
    }

    public CompletableFuture<TrackRankDto[]> getTopTracks(String country, int page){
        if(country==null)
            return CompletableFuture.completedFuture(new TrackRankDto[0]);

        String url = String.format(LASTFM_GET_TOP_TRACKS,country,page);
        return request.getLines(url).thenApply(s->{
            TopTracksDto tracksDto = gson.fromJson(s,TopTracksDto.class);
            return tracksDto.getTracks().getTrack();
        });
    }
}
