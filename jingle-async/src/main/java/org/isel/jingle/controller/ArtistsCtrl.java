package org.isel.jingle.controller;

import io.reactivex.Observable;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.isel.jingle.JingleService;
import org.isel.jingle.model.Artist;
import org.isel.jingle.view.ArtistView;

public class ArtistsCtrl {
    private final ArtistView view;

    public ArtistsCtrl(Router router) {
        view = new ArtistView();
        router.route("/artists").handler(this::artistHandler);
    }

    private void artistHandler(RoutingContext context) {
        JingleService service = new JingleService();
        String artistName = context.request().getParam("name");
        Observable<Artist> artists = service.searchArtist(artistName).take(100);
        view.write(context.response(),artists);
    }


}
