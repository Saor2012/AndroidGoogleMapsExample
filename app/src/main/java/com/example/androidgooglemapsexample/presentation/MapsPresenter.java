package com.example.androidgooglemapsexample.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.androidgooglemapsexample.data.EntityDB;
import com.example.androidgooglemapsexample.data.IRepository;
import com.example.androidgooglemapsexample.data.Repository;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MapsPresenter implements IMapsPresenter.Presenter {
    private IMapsPresenter.View view;
    private IRepository repository;
    private List<EntityDB> list;
    private static final String TAG = "MapsPresenter";

    private static final LatLng Post1 = new LatLng(48.527657,35.015481);
    private static final LatLng Post2 = new LatLng(48.525476, 35.033480);

    public MapsPresenter() {
        repository = new Repository();
        list = null;
    }

    @Override
    public void stopView() {
        if (view != null) {
            view = null;
        }
    }

    @Override
    public void startView(IMapsPresenter.View view) {
        this.view = view;
    }

//    @Override
//    public List<EntityDB> init() {
//        return query();
//    }

    @Override
    public void onGetGPS() {
        Timber.e("On click get GPS device location");
        view.handleGPS();
    }

    @Override
    public void onGetSearch() {
        Timber.e("On click search");
        view.handleSearch();
    }

    @Override
    public void onGetRoute() {
        Timber.e("On click get route");
        view.handleRoute();
    }

    @Override
    public long insert(LatLng post) {
        final long[] result = {-1};
        repository.insert(post).subscribe(new DisposableSingleObserver<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                result[0] = aLong;
                Timber.tag(TAG).e("Get index from insert() = %s", aLong);
                Timber.tag(TAG).e("Get index from insert() = %s", result[0]);
                dispose();
            }

            @Override
            public void onError(Throwable e) {
                Timber.tag(TAG).e("Error insert() throw: %s", e.getMessage());
            }
        });
        return result[0];
    }

    @Override
    public void query() {
        repository.query()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableSingleObserver<List<EntityDB>>() {
                @Override
                public void onSuccess(List<EntityDB> entityDBS) {
                    list = entityDBS;
                    if (entityDBS == null && entityDBS.size() == 0) {
                        list = new ArrayList<EntityDB>();
                        int index = 0;
                        list.add(new EntityDB(++index, Post1.latitude, Post1.longitude));
                        list.add(new EntityDB(++index, Post2.latitude, Post2.longitude));

                        Timber.tag(TAG).e("Get index at query().onSuccess() from insert() = %s", insert(Post1));
                        Timber.tag(TAG).e("Get index at query().onSuccess() from insert() = %s", insert(Post2));
                    }
                    Timber.tag(TAG).e("Is list null = %s", list == null);
                    view.getList(list);
                    dispose();
                }

                @Override
                public void onError(Throwable e) {
                    Timber.tag(TAG).e("Error query() throw: %s", e.getMessage());
                }
            });
    }
}
