package com.example.androidgooglemapsexample.app;

import android.app.Application;

import androidx.room.Room;

import com.example.androidgooglemapsexample.BuildConfig;
import com.example.androidgooglemapsexample.presentation.Constants;
import com.example.androidgooglemapsexample.data.ILocalDB;
import com.example.androidgooglemapsexample.data.LocalDataBase;

import timber.log.Timber;

public class App extends Application {
    public static ILocalDB localDB;
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        if (localDB == null) {
            localDB = provideEntityDataBase();
        }
    }

    private ILocalDB provideEntityDataBase() {
        LocalDataBase database = Room.databaseBuilder(this, LocalDataBase.class, Constants.NAME_DAO)
                .allowMainThreadQueries()
                .build();
        return database.getDao();
    }
}
