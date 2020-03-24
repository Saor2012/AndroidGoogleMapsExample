package com.example.androidgooglemapsexample.data;

import com.example.androidgooglemapsexample.app.App;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

public class Repository implements IRepository {
    private final String TAG = "Repository";
    private long id = 0;

    public Repository() {}

    @Override
    public Single<Long> insert(LatLng post) {
        return Single.defer(() -> Single.just(App.localDB.insert(new EntityDB(++id,post.latitude,post.longitude))))
                .doOnError(throwable -> Timber.tag(TAG).e("Exception: insert() dao throw error: %s", throwable.getMessage()));
    }

    @Override
    public Single<List<EntityDB>> query() {
        return App.localDB.loadList()
                .doOnError(throwable -> Timber.tag(TAG).e("Exception: loadList() dao throw error - %s", throwable.getMessage()))
                .flatMap(v -> {
                    if (v != null && v.size() > 0) {
                        setId(v.get(v.size() - 1).getId());
                    }
                    return Single.just(v);
                });
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

    private long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }
}
