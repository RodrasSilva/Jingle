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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import junit.framework.Assert;
import org.isel.jingle.model.Album;
import org.isel.jingle.model.Artist;
import org.isel.jingle.model.Track;
import org.isel.jingle.model.TrackRank;
import org.isel.jingle.util.req.HttpRequest;
import org.isel.jingle.util.req.TestRequest;
import org.junit.Test;

public class JingleServiceTest {

    @Test
    public void artistGetTracksRankForMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Observable<Artist> artist = service.searchArtist("Capitao Fausto");
        assertEquals(0, httpGet.count);
        Artist muse = artist.blockingFirst();
        assertEquals(1, httpGet.count);
        Observable<TrackRank> museTracksRankInPortugal = muse.getTracksRank("Portugal");
        Observable<TrackRank> tracks = service.getTracksRank(muse.getMbid(),"Portugal");
        Long expected = museTracksRankInPortugal.count().blockingGet();
        Long actual = tracks.count().blockingGet();
        Assert.assertEquals(expected, actual);
    }
    @Test
    public void artistGetTracksRankForDisturbed() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
            Observable<Artist> artist = service.searchArtist("Disturbed");
        assertEquals(0, httpGet.count);
        Artist disturbed = artist.blockingFirst();
        assertEquals(1, httpGet.count);
        Observable<TrackRank> museTracksRankInPortugal = disturbed.getTracksRank("Portugal");
        Observable<TrackRank> tracks = service.getTracksRank(disturbed.getMbid(),"Portugal");
        Assert.assertEquals(museTracksRankInPortugal.count().blockingGet(),tracks.count().blockingGet());
    }

    @Test
    public void getTracksRank() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Observable<TrackRank> tracks = service.getTracksRank("ada7a83c-e3e1-40f1-93f9-3e73dbc9298a", "Portugal");
        assertEquals(0, httpGet.count);
        assertTrue(tracks.count().blockingGet().intValue() > 0);
        assertTrue(httpGet.count > 1);
    }


    @Test
    public void unitaryTestToGetTopTracksForPortugal() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Observable<TrackRank> topTracks = service.getTopTracks("Portugal").take(200);
        assertEquals(0, httpGet.count);
        assertEquals(200,topTracks.count().blockingGet().intValue());
        assertEquals(4, httpGet.count);
    }

    @Test
    public void unitaryTestToGetAlbumTracks() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Observable<Track> albumTracks = service.getAlbumTracks("6e0b8985-ab0a-401d-af61-fefa738b950a");
        assertEquals(0, httpGet.count);
        assertEquals(0,albumTracks.count().blockingGet().intValue());
        albumTracks = service.getAlbumTracks("aefcf53b-5980-463b-b03d-a6c8da6a9432");
        assertEquals(11, albumTracks.count().blockingGet().intValue());
        assertEquals(2, httpGet.count);
    }

    @Test
    public void TestGetAlbums() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Observable<Track> tracks = service.getAlbums("fd857293-5ab8-40de-b29e-55a69d4e4d0f")
                .filter(a -> a.getMbid()!=null)
                .map(Album::getMbid)
                .flatMap(service::getAlbumTracks);

        assertEquals(0, httpGet.count);
        Observable<Track> limit = tracks.take(100);
        assertEquals(100, limit.count().blockingGet().intValue());
    }

    @Test
    public void searchHiperAndCountAllResults() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Observable<Artist> artists = service.searchArtist("hiper").cache();
        assertEquals(0, httpGet.count);
        assertTrue(artists.count().blockingGet().intValue() > 700);
        assertEquals(25, httpGet.count);
        Maybe<Artist> last = artists.reduce((first,second)->second);
        assertEquals("Coma - Hipertrofia.(2008)", last.blockingGet().getName());
        //Expected Changed because of cache
        assertEquals(25, httpGet.count);
    }

    @Test
    public void getFirstAlbumOfMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Observable<Artist> artist = service.searchArtist("muse");
        assertEquals(0, httpGet.count);
        Artist muse = artist.blockingFirst();
        assertEquals(1, httpGet.count);
        Observable<Album> albums = muse.getAlbums();
        assertEquals(1, httpGet.count);
        Album first = albums.blockingFirst();
        assertEquals(2, httpGet.count);
        assertEquals("Black Holes and Revelations", first.getName());
    }

    @Test
    public void get111AlbumsOfMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Artist muse = service.searchArtist("muse").blockingFirst();
        Observable<Album> albums = muse.getAlbums().take(111);
        assertEquals(111, albums.count().blockingGet().intValue());
        assertEquals(4, httpGet.count); // 1 for artist + 3 pages of albums each with 50 albums
    }

    @Test
    public void getSecondSongFromBlackHolesAlbumOfMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Album blackHoles = service.searchArtist("muse").blockingFirst().getAlbums().blockingFirst();
        assertEquals(2, httpGet.count); // 1 for artist + 1 page of albums
        assertEquals("Black Holes and Revelations", blackHoles.getName());
        Track song = blackHoles.getTracks().skip(1).blockingFirst();
        assertEquals(3, httpGet.count); // + 1 to getTrack
        assertEquals("Starlight", song.getName());
    }

    @Test
    public void getLastSongFromSecondAlbumOfMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Album blackHoles = service.searchArtist("muse").blockingFirst().getAlbums().skip(1).blockingFirst();
        assertEquals(2, httpGet.count); // 1 for artist + 1 page of albums
        assertEquals("Absolution", blackHoles.getName());
        Maybe<Track> song = blackHoles.getTracks().reduce((first, second) -> second);
        assertEquals("Ruled by Secrecy", song.blockingGet().getName());
        assertEquals(3, httpGet.count); // + 1 to getTrack
    }

    @Test
    public void get42thTrackOfMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Observable<Track> tracks = service.searchArtist("muse").blockingFirst().getTracks();
        assertEquals(1, httpGet.count); // 1 for artist + 0 for tracks because it fetches lazily
        Track track = tracks.skip(42).blockingFirst();  // + 1 to getAlbums + 4 to get tracks of first 4 albums.
        assertEquals("MK Ultra", track.getName());
        assertTrue(httpGet.count>5);
    }

    @Test
    public void getLastTrackOfMuseOf500() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new TestRequest(httpGet)));
        Observable<Track> tracks = service.searchArtist("muse").blockingFirst().getTracks().take(500);
        assertEquals(500, tracks.count().blockingGet().intValue());
        assertTrue(httpGet.count > 70); // Each page has 50 albums => 50 requests to get their tracks. Some albums have no tracks.
    }

    static class HttpGet implements Function<String, CompletableFuture<String>> {

        int count = 0;

        @Override
        public CompletableFuture<String> apply(String path) {
            System.out.println("Requesting..." + ++count);
            HttpRequest httpRequest = new HttpRequest();
            return httpRequest.getLines(path);
        }
    }
}
