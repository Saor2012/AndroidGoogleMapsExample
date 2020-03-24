package com.example.androidgooglemapsexample.presentation;

import com.example.androidgooglemapsexample.data.EntityDB;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface IMapsPresenter {
    interface View {
        void handleGPS();
        void handleSearch();
        void handleRoute();

        void getList(List<EntityDB> list);
    }
    interface Presenter extends BasePresenter<View> {
//      List<EntityDB> init();
        void onGetGPS();
        void onGetSearch();
        void onGetRoute();

        long insert(LatLng post);
        void query();
    }
}
