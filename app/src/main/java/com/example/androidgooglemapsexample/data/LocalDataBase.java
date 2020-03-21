package com.example.androidgooglemapsexample.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {EntityDB.class}, version = 1, exportSchema = false)
public abstract class LocalDataBase extends RoomDatabase {
    public abstract ILocalDB getDao();
}
