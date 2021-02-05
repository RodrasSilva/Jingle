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
import org.isel.jingle.WebApp;
import org.isel.jingle.model.Album;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Table;
import org.xmlet.htmlapifaster.Tbody;

public class AlbumView implements View<Observable<Album>> {

    @Override
    public void write(HttpServerResponse resp, Observable<Album> model) {
        resp.setChunked(true);
        resp.putHeader("content-type", "text/html");
        model.subscribeWith(new Observer<>() {
            Tbody<Table<Body<Html<HtmlView>>>> tbody;
            @Override
            public void onSubscribe(Disposable d) { tbody = header(resp);}

            @Override
            public void onNext(Album album) { body(tbody,album);}

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
                        .text("Albums")
                    .__()
                .__()
                .body()
                    .h1()
                        .text("Albums")
                    .__()
                .table()
                    .thead()
                        .tr()
                            .th().text("Name").__()
                            .th().text("Playcount").__()
                            .th().text("Mbid").__()
                            .th().text("url").__()
                            .th().text("image").__()
                            .th().text("tracks").__()
                        .__()
                    .__()
                .tbody();
    }

    private void body(Tbody<Table<Body<Html<HtmlView>>>> tbody,
            Album album) {
        tbody
                .tr()
                    .td().text(album.getName()).__()
                    .td().text(album.getPlaycount()).__()
                    .td().text(album.getMbid()==null?"":album.getMbid()).__()
                    .td()
                        .a()
                            .attrHref(album.getUrl())
                            .text(album.getUrl()).__().__() // td
                    .td().img().attrSrc(album.getImage()).__().__()
                    .td()
                        .a()
                            .attrHref("/albums/"+album.getMbid()+"/tracks")
                                .text(album.getMbid()==null? "" : "Tracks")
                            .__()
                        .__()
                    .__();
    }

    @Override
    public void write(HttpServerResponse resp) {
        throw new UnsupportedOperationException("This view does not require a Model. You should invoke write(resp) instead!");
    }

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
