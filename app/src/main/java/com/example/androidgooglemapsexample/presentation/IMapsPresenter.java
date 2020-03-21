package com.example.androidgooglemapsexample.presentation;

public interface IMapsPresenter {

    interface View {}
    interface Presenter extends BasePresenter<View> {
        void init();
    }
}
