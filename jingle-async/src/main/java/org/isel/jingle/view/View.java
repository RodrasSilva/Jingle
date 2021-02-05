package org.isel.jingle.view;

import io.vertx.core.http.HttpServerResponse;

public interface View<T> {

    void write(HttpServerResponse resp, T model);

    void write(HttpServerResponse resp);
}