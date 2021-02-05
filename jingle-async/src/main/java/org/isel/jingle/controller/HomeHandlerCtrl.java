package org.isel.jingle.controller;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.isel.jingle.view.HomeView;

public class HomeHandlerCtrl {
    private final HomeView view;

    public HomeHandlerCtrl(Router router) {
        view = new HomeView();
        router.route("/").handler(this::index);
    }

    private void index(RoutingContext context) {
        HttpServerResponse response = context.response();
        view.write(response);
    }
}
