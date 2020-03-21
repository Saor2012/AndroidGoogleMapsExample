package com.example.androidgooglemapsexample;

public interface IMapsPresenter {

    interface View {}
    interface Presenter extends BasePresenter<View> {
        void init();
    }
}
