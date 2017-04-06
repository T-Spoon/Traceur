package com.tspoon.traceur;

import io.reactivex.Observable;

public class StreamFactory {

    public static Observable<String> createNullPointerExceptionObservable() {
        return Observable.just("Hello")
                .map(s -> null);
    }
}
