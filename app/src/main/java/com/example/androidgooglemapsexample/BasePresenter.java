package com.example.androidgooglemapsexample;

public interface BasePresenter<V> {
    void stopView();
    void startView(V view);
}
