package org.isel.jingle.view;

import htmlflow.StaticHtml;
import io.vertx.core.http.HttpServerResponse;
import org.isel.jingle.WebApp;
import org.xmlet.htmlapifaster.EnumTypeInputType;

public class HomeView implements View {
    @Override
    public void write(HttpServerResponse resp, Object model) {
        throw new UnsupportedOperationException("This view does not require a Model. You should invoke write(resp) instead!");
    }

    @Override
    public void write(HttpServerResponse resp) {
        String html = StaticHtml.view()
                .html()
                    .head()
                        .meta()
                            .attrCharset("UTF-8")
                        .__()
                        .style()
                            .text(WebApp.getStyle())
                            .text("body {\n"
                                    + "background-image: url(\"https://i.imgur.com/0T4wO6E.png\");"
                                    + "background-repeat: no-repeat;"
                                    + "background-size: cover"
                                    + "}")
                            .text("form {"
                                    + " width: 50%;"
                                    + "margin-left: 40%;"
                                    + "margin-right: 50%;"
                                    + "margin-top : 5%"

                                    + "}")

                        .__()
                        .title()
                            .text("Home Page")
                        .__()
                    .__()
                    .body()
                        .div().attrClass("container")
                            .form()
                                .attrId("form")
                                .attrAction("/artists")
                                    .text("Insert Artist Name :")
                                .input()
                                    .attrOninput("text")
                                        .attrName("name")
                                .__()
                                .input()
                                    .attrType(EnumTypeInputType.SUBMIT)
                                .__()
                            .__()
                        .__()
                     .__()
                .__()
                .render();

        resp.putHeader("content-type", "text/html");
        // write to the response and end it
        resp.end(html);

    }
}