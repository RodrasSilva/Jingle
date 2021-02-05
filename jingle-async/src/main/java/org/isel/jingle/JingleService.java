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
import io.reactivex.internal.functions.Functions;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.isel.jingle.dto.AlbumDto;
import org.isel.jingle.dto.ArtistDto;
import org.isel.jingle.dto.TrackDto;
import org.isel.jingle.dto.TrackRankDto;
import org.isel.jingle.model.Album;
import org.isel.jingle.model.Artist;
import org.isel.jingle.model.Track;
import org.isel.jingle.model.TrackRank;
import org.isel.jingle.util.req.HttpRequest;

import java.util.stream.Stream;

public class JingleService {

    private static final int TRACKS_PER_PAGE = 50 ;
    final LastfmWebApi api;

    public JingleService(LastfmWebApi api) {
        this.api = api;
    }

    public JingleService() {
        this(new LastfmWebApi(new HttpRequest()));
    }

    public Observable<TrackRank> getTracksRank(String artistMbId, String country){
        Observable<TrackRank> top100 = getTopTracks(country).take(100);
        Observable<Track> artistTracks = getTracks(artistMbId);

        BiPredicate<Track,TrackRank> trackBiPredicate = (t1,t2)-> t1.getName().equals(t2.getName()) && t1.getUrl().equals(t2.getUrl());
        BiFunction<Track,TrackRank,TrackRank> trackFunction = (t1,t2)-> new TrackRank(t1.getName(),t1.getUrl(),t1.getDuration(),t2.getRank());

        return ObservableUtils.merge(artistTracks,top100,trackBiPredicate,trackFunction, new TrackRank("","",0,0));
    }

    public Observable<Artist> searchArtist(String name) {
        Stream<CompletableFuture<ArtistDto[]>> completableFutureStream = Stream
                .iterate(1, n -> n + 1)
                .map(number -> api.searchArtist(name, number));

        return Observable.fromIterable(completableFutureStream::iterator)
                .concatMap(JingleService::toObservable)
                .takeWhile(ArtistDto -> ArtistDto.length!=0)
                .concatMap(Observable::fromArray)
                .map(this::createArtist);
    }

    public Observable<Album> getAlbums(String artistMbid) {
            Stream<CompletableFuture<AlbumDto[]>> completableFutureStream = Stream
                    .iterate(1, n -> n + 1)
                    .map(number -> api.getAlbums(artistMbid, number));

            return Observable.fromIterable(() -> completableFutureStream.iterator())
                    .concatMap(JingleService::toObservable)
                    .takeWhile(artistDto -> artistDto.length != 0)
                    .concatMap(Observable::fromArray)
                    .map(this::createAlbum);
    }

    public Observable<TrackRank> getTopTracks(String country){
        int[] currPage = {0};
        Stream<CompletableFuture<TrackRankDto[]>> completableFutureStream = Stream
                .iterate(1, n -> n + 1)
                .map(number -> {
                    currPage[0] = number;
                    return api.getTopTracks(country, number);
                });

        return Observable.fromIterable(completableFutureStream::iterator)
                .concatMap(JingleService::toObservable)
                .takeWhile(trackDto-> trackDto.length!=0)
                .concatMap(Observable::fromArray)
                .map(trackDto -> createTrackRank(trackDto,currPage[0]));
    }

    public Observable<Track> getAlbumTracks(String albumMbid) {
        Stream<String> stream = Stream.of(albumMbid);
        return Observable.fromIterable(stream::iterator)
                .map(api::getAlbumInfo)
                .concatMap(JingleService::toObservable)
                .concatMap(Observable::fromArray)
                .map(this::createTrack);
    }

    public Observable<Track> getTracks(String artistMbid) {
        return getAlbums(artistMbid)
                .filter(album -> album.getMbid() != null)
                .concatMap(album -> getAlbumTracks(album.getMbid()));
    }

    private Artist createArtist(ArtistDto artistDto) {
        return new Artist(
                artistDto.getName(),
                artistDto.getListeners(),
                artistDto.getMbid(),
                artistDto.getUrl(),
                artistDto.getImage()[3].getText(),
                getAlbums(artistDto.getMbid()),
                getTracks(artistDto.getMbid()),
                (country)->getTracksRank(artistDto.getMbid(),country));
    }


    private Track createTrack(TrackDto trackDto) {
        return new Track(trackDto.getName(), trackDto.getUrl(), trackDto.getDuration());
    }

    private TrackRank createTrackRank(TrackRankDto trackRank, int page) {
        return new TrackRank(
                trackRank.getName(),
                trackRank.getUrl(),
                trackRank.getDuration(),
                trackRank.getAttr().getRank() + ((page-1) * TRACKS_PER_PAGE) +1) ;
    }

    private Album createAlbum(AlbumDto albumDto) {
        return new Album(
                albumDto.getName(),
                albumDto.getPlaycount(),
                albumDto.getMbid(),
                albumDto.getUrl(),
                albumDto.getImage()[3].getText(),
                getAlbumTracks(albumDto.getMbid()));
    }

    public static <T> Observable<T> toObservable(CompletableFuture<T> future) {
        return Observable.create(subscriber ->
                future.whenComplete((result, error) -> {
                    if (error != null) {
                        subscriber.onError(error);
                    } else {
                        subscriber.onNext(result);
                        subscriber.onComplete();
                    }
                }));
    }
}
