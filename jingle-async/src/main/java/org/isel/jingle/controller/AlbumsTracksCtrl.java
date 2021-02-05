package org.isel.jingle.controller;

import io.reactivex.Observable;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.isel.jingle.JingleService;
import org.isel.jingle.model.Album;
import org.isel.jingle.model.Track;
import org.isel.jingle.view.AlbumView;
import org.isel.jingle.view.AlbumsTracksView;

public class AlbumsTracksCtrl {

    private final AlbumsTracksView view;

    public AlbumsTracksCtrl(Router router) {
        view = new AlbumsTracksView();
        router.route("/albums/:id/tracks").handler(this::albumsTracksHandler);
    }

    private void albumsTracksHandler(RoutingContext context) {
        JingleService service = new JingleService();
        String mbId = context.request().getParam("id");
        Observable<Track> tracks = service.getAlbumTracks(mbId);
        view.write(context.response(),tracks);
    }

}
