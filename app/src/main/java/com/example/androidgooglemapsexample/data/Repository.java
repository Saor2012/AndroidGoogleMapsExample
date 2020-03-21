package com.example.androidgooglemapsexample.data;

import com.example.androidgooglemapsexample.app.App;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

public class Repository implements IRepository {
    private final String TAG = "Repository";

    public Repository() {}

    @Override
    public Single<Long> insert(String string) {
        return Single.defer(() -> Single.just(App.localDB.insert(new EntityDB(1,1,1))))
                .doOnError(throwable -> Timber.e("Exception: insert at insert dao throw error: %s", throwable.getMessage()));
    }

    @Override
    public Single<List<EntityDB>> query() {
        return App.localDB.loadList()
                .doOnError(throwable -> Timber.tag(TAG).e("Exception: loadList() dao throw error - %s", throwable.getMessage()));
    }

    @Override
    public Single<Long> getIndex() {
        return App.localDB.getIndex()
                .doOnError(throwable -> Timber.tag(TAG).e("Exception: getIndex() dao throw error - %s", throwable.getMessage()));
    }

    @Override
    public Completable deleteEntry(long id) {
        return App.localDB.deleteElement(id)
                .doOnError(throwable -> Timber.tag(TAG).e("Exception: deleteEntry() dao throw error - %s", throwable.getMessage()));
    }

}
