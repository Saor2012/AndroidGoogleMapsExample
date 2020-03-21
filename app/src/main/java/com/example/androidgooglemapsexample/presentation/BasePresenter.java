package com.example.androidgooglemapsexample.presentation;

public interface BasePresenter<V> {
    void stopView();
    void startView(V view);
}
