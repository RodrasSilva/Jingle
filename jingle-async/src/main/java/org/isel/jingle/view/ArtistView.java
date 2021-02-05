package org.isel.jingle.view;

import htmlflow.HtmlView;
import htmlflow.StaticHtml;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.vertx.core.http.HttpServerResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;
import org.isel.jingle.WebApp;
import org.isel.jingle.model.Artist;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Table;
import org.xmlet.htmlapifaster.Tbody;

public class ArtistView implements View<Observable<Artist>> {

    @Override
    public void write(HttpServerResponse resp, Observable<Artist> model) {
        resp.setChunked(true);
        resp.putHeader("content-type", "text/html");
        model.subscribeWith(new Observer<Artist>() {
            Tbody<Table<Body<Html<HtmlView>>>> tbody;
            @Override
            public void onSubscribe(Disposable d) { tbody = header(resp);}

            @Override
            public void onNext(Artist artist) { body(tbody,artist);}

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() {
                tbody.__().__().__().__(); //ends the elements
                resp.end();
            }
        });
    }

    private Tbody<Table<Body<Html<HtmlView>>>> header(HttpServerResponse resp) {
        return StaticHtml.view(new ResponsePrintStream(resp))
                .html()
                    .head()
                        .meta()
                            .attrCharset("UTF-8")
                        .__()
                        .style()
                            .text(WebApp.getStyle())
                        .__()
                        .title()
                            .text("Artists")

                        .__()
                    .__()
                    .body()
                        .h1()
                            .text("Artists")
                        .__()
                        .table()
                            .thead()
                                .tr()
                                    .th().text("Name").__()
                                    .th().text("Listeners").__()
                                    .th().text("Mbid").__()
                                    .th().text("Url").__()
                                    .th().text("Image").__()
                                    .th().text("Albums").__()
                                    .th().text("Tracks").__()
                                .__()
                            .__()
                            .tbody();
    }


    private void body(Tbody<Table<Body<Html<HtmlView>>>> tbody,
            Artist artist) {
            tbody
                .tr()
                    .td().text(artist.getName()).__()
                    .td().text(artist.getListeners()).__()
                    .td().text(artist.getMbid()).__()
                    .td()
                        .a()
                            .attrHref(artist.getUrl())
                                .text(artist.getUrl()).__().__()
                    .td().img().attrSrc(artist.getImage()).__().__()
                    .td()
                        .a()
                            .attrHref("/artists/"+artist.getMbid()+"/albums")
                                .text(artist.getMbid().equals("") ? "" : "Albums")
                        .__()
                    .__()
                    .td()
                        .a()
                            .attrHref("/artists/"+artist.getMbid()+"/tracks")
                                .text(artist.getMbid().equals("")?"" : "Tracks")
                        .__()
                    .__()
                    .__();
    }

    @Override
    public void write(HttpServerResponse resp) { throw new UnsupportedOperationException("This view requires a Model. You should invoke write(resp, model) instead!"); }


    private class ResponsePrintStream extends PrintStream {

        public ResponsePrintStream(HttpServerResponse resp) {
            super(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    char c = (char) b;
                    resp.write(String.valueOf(c));
                }
            });
        }
    }
}
