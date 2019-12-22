package com.test.app.base;

import org.reactivestreams.Publisher;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

public class RxTransformer<T, R> {
    public interface Commands<T, R> {
        ObservableSource<R> applyObs(Observable<T> upstream);

        CompletableSource applyCompletable(Completable upstream);

        Publisher<R> applyPublisher(Flowable<T> upstream);

        SingleSource<R> applySingle(Single<T> upstream);

        MaybeSource<R> applyMaybe(Maybe<T> upstream);
    }

    private final Commands<T, R> commands;

    public RxTransformer(Commands<T, R> commands) {
        this.commands = commands;
    }

    public ObservableTransformer<T, R> forObservable() {
        return commands::applyObs;
    }

    public CompletableTransformer forCompletable() {
        return commands::applyCompletable;
    }

    public FlowableTransformer<T, R> forFlowable() {
        return commands::applyPublisher;
    }

    public SingleTransformer<T, R> forSingle() {
        return commands::applySingle;
    }

    public MaybeTransformer<T, R> forMaybe() {
        return commands::applyMaybe;
    }
}
