package org.isel.jingle;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.isel.jingle.controller.AlbumsCtrl;
import org.isel.jingle.controller.AlbumsTracksCtrl;
import org.isel.jingle.controller.ArtistTracksCtrl;
import org.isel.jingle.controller.ArtistsCtrl;
import org.isel.jingle.controller.HomeHandlerCtrl;

public class WebApp {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        HttpServer server = vertx.createHttpServer();


        new ArtistsCtrl(router);
        new ArtistTracksCtrl(router);
        new AlbumsCtrl(router);
        new HomeHandlerCtrl(router);
        new AlbumsTracksCtrl(router);
        server.requestHandler(router).listen(8080);
    }

    private static Handler<RoutingContext> start() {
        return routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html");
            response.end("Start");
        };
    }

    public static String getStyle() {
        return "table, td, th {  \n"
                + "  border: 1px solid #ddd;\n"
                + "  text-align: center;\n"

                + "}\n"
                + "\n"
                + "table {\n"
                + "  border-collapse: collapse;\n"
                + "  width: 100%;"
                + "}\n"
                + "\n"
                + "th, td {\n"
                + "  padding: 15px;\n"
                + "}"
                + "h1 {\n"
                + "    text-align:center;\n"
                + "    padding: 15px;\n"
                + "}"
                +"body{"
                + "background-color : #f5890b;"
                + "}";
    }

}
