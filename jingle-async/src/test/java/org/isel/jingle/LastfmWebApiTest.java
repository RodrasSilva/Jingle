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

import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.isel.jingle.dto.*;
import org.isel.jingle.util.req.HttpRequest;
import org.isel.jingle.util.req.Request;
import org.isel.jingle.util.req.TestRequest;
import org.junit.Test;

import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;


public class LastfmWebApiTest {


    @Test
    public void getTopTracksForPortugal() {
        Request req = new HttpRequest();
        LastfmWebApi api = new LastfmWebApi(req);
        CompletableFuture<TrackRankDto[]> topTracks = api.getTopTracks("portugal",1);
        Single<Long> count = JingleService.toObservable(topTracks)
                .flatMap(arr -> Observable.fromArray(arr)).count();
        assertEquals(50,count.blockingGet().intValue());
    }

    @Test
    public void getAlbumInfoUnitaryTest() throws ExecutionException, InterruptedException {
        Request req = new HttpRequest();
        LastfmWebApi api = new LastfmWebApi(req);
        CompletableFuture<TrackDto[]> albumInfo = api
                .getAlbumInfo("6e0b8985-ab0a-401d-af61-fefa738b950a");
        Single<Long> count = JingleService.toObservable(albumInfo)
                .flatMap(arr -> Observable.fromArray(arr)).count();
        assertEquals(0, count.blockingGet().intValue());
    }

    @Test
    public void searchForArtistsNamedDavid() {
        Request req = new HttpRequest();
        LastfmWebApi api = new LastfmWebApi(req);
        CompletableFuture<ArtistDto[]> artists = api.searchArtist("david", 1);
        Observable<ArtistDto> artistDtoObservable = JingleService.toObservable(artists)
                .flatMap(arr -> Observable.fromArray(arr));
        String name = artistDtoObservable.blockingFirst().getName();
        assertEquals("David Bowie", name);
    }

    @Test
    public void searchForArtisNameTheWeeknd() {
        Request req = new HttpRequest();
        LastfmWebApi api = new LastfmWebApi(req);
        CompletableFuture<ArtistDto[]> artists = api.searchArtist("The Weeknd", 1);
        Observable<ArtistDto> artistDtoObservable = JingleService.toObservable(artists)
                .flatMap(arr -> Observable.fromArray(arr));
        String name = artistDtoObservable.blockingFirst().getName();
        assertEquals("The Weeknd", name);
    }

    @Test
    public void getTopAlbumsFromMuse() {
        Request req = new HttpRequest();
        LastfmWebApi api = new LastfmWebApi(req);
        CompletableFuture<ArtistDto[]> artists = api.searchArtist("muse", 1);
        Observable<ArtistDto> artistDtoObservable = JingleService.toObservable(artists)
                .flatMap(arr -> Observable.fromArray(arr));
        String mbid = artistDtoObservable.blockingFirst().getMbid();
        CompletableFuture<AlbumDto[]> albums = api.getAlbums(mbid, 1);
        Observable<AlbumDto> albumDtoObservable = JingleService.toObservable(albums)
                .flatMap(arr -> Observable.fromArray(arr));
        assertEquals("Black Holes and Revelations",albumDtoObservable.blockingFirst().getName() );
    }

    @Test
    public void getTopAlbumsFromTheWeeknd() {
        Request req = new HttpRequest();
        LastfmWebApi api = new LastfmWebApi(req);
        CompletableFuture<ArtistDto[]> artists = api.searchArtist("the weeknd", 1);
        Observable<ArtistDto> artistDtoObservable = JingleService.toObservable(artists)
                .flatMap(arr -> Observable.fromArray(arr));
        String mbid = artistDtoObservable.blockingFirst().getMbid();
        CompletableFuture<AlbumDto[]> albums = api.getAlbums(mbid, 1);
        Observable<AlbumDto> albumDtoObservable = JingleService.toObservable(albums)
                .flatMap(arr -> Observable.fromArray(arr));
        assertEquals("Beauty Behind the Madness", albumDtoObservable.blockingFirst().getName());
    }

    @Test
    public void getStarlightFromBlackHolesAlbumOfMuse() {
        Request req = new HttpRequest();
        LastfmWebApi api = new LastfmWebApi(req);
        CompletableFuture<ArtistDto[]> artists = api.searchArtist("muse", 1);
        Observable<ArtistDto> artistDtoObservable = JingleService.toObservable(artists)
                .flatMap(Observable::fromArray);
        String mbid = artistDtoObservable.blockingFirst().getMbid();
        CompletableFuture<AlbumDto[]> album = api.getAlbums(mbid, 1);
        Observable<AlbumDto> albumDtoObservable = JingleService.toObservable(album).flatMap(
                Observable::fromArray);
        CompletableFuture<TrackDto[]> albumInfo = api
                .getAlbumInfo(albumDtoObservable.blockingFirst().getMbid());
        Observable<TrackDto> tracks = JingleService.toObservable(albumInfo).flatMap(Observable::fromArray);
        TrackDto track = tracks.skip(1).blockingFirst();
        assertEquals("Starlight", track.getName());
    }


}
