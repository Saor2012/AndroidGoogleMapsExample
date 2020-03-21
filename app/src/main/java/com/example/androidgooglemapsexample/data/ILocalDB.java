package com.example.androidgooglemapsexample.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface ILocalDB {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(EntityDB entry);

    @Query("SELECT * FROM entity_db")
    Single<List<EntityDB>> loadList();

    @Query("SELECT MAX(id) FROM entity_db")
    Single<Long> getIndex();

    @Query("DELETE FROM entity_db WHERE id = :id")
    Completable deleteElement(long id);
}
