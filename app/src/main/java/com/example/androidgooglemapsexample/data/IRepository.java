package com.example.androidgooglemapsexample.data;

import com.example.androidgooglemapsexample.data.EntityDB;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface IRepository {
    Single<Long> insert(String string);
    Single<List<EntityDB>> query();
    Single<Long> getIndex();
    Completable deleteEntry(long id);
}
