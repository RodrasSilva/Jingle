package org.isel.jingle;

import io.reactivex.Observable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class ObservableUtils {

    public static <T, U, R> Observable<R> merge(Observable<T> seq1, Observable<U> seq2,
            BiPredicate<T, U> pred,
            BiFunction<T, U, R> transf,
            U defaultVal) {

        List<U> list = new LinkedList<>();
        return Observable.create(
                subscriber -> seq2.subscribe(
                        u -> list.add(u),
                        error -> {throw new RuntimeException();},
                        () -> seq1.subscribe(
                                t -> {
                                    U u = defaultVal;
                                    for (U elem : list) {
                                        if (pred.test(t, elem)) {
                                            u = elem;
                                            break;
                                        }
                                    }
                                    subscriber.onNext(transf.apply(t, u));
                                },
                                error -> {throw new RuntimeException();},
                                () -> subscriber.onComplete())));
    }

}

