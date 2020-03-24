package com.example.androidgooglemapsexample.data;

import com.example.androidgooglemapsexample.data.EntityDB;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface IRepository {
    Single<Long> insert(LatLng post);
    Single<List<EntityDB>> query();
    Single<Long> getIndex();
    Completable deleteEntry(long id);
}
