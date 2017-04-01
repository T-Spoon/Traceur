package com.tspoon.traceur;

import io.reactivex.Observable;

public class StreamFactory {

    public static Observable<String> createErrorObservable() {
        return Observable.just("Hello")
                .map(s -> s + s)
                .map(s -> null);
    }
}
