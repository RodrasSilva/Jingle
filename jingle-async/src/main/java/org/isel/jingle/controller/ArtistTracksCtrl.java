package org.isel.jingle.controller;

import io.reactivex.Observable;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.isel.jingle.JingleService;
import org.isel.jingle.model.Track;
import org.isel.jingle.view.ArtistTracksView;

public class ArtistTracksCtrl {

    private final ArtistTracksView view;

    public ArtistTracksCtrl(Router router) {
        view = new ArtistTracksView();
        router.route("/artists/:id/tracks").handler(this::artistTracksHandler);
    }

    private void artistTracksHandler(RoutingContext context) {
        JingleService service = new JingleService();
        String mbId = context.request().getParam("id");
        Observable<Track> tracks = service.getTracks(mbId);
        view.write(context.response(), tracks);
    }
}
