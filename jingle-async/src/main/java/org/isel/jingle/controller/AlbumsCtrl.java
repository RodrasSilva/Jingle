package org.isel.jingle.controller;

import io.reactivex.Observable;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.isel.jingle.JingleService;
import org.isel.jingle.model.Album;
import org.isel.jingle.view.AlbumView;

public class AlbumsCtrl {

    private final AlbumView view;

    public AlbumsCtrl(Router router) {
        view = new AlbumView();
        router.route("/artists/:id/albums").handler(this::albumsHandler);
    }

    private void albumsHandler(RoutingContext context) {
        JingleService service = new JingleService();
        String mbId = context.request().getParam("id");
        Observable<Album> albums = service.getAlbums(mbId).take(100);
        view.write(context.response(),albums);
    }


}
