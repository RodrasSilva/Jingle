package org.isel.jingle.util.req;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class TestRequest implements Request {


    private Function<String, CompletableFuture<String>> function;

    public TestRequest(Function<String,CompletableFuture<String>> function ) {
        this.function = function;
    }

    @Override
    public CompletableFuture<String> getLines(String path) {
        return function.apply(path);
    }
}
