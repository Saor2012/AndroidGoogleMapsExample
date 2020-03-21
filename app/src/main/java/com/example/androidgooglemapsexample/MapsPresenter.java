package com.example.androidgooglemapsexample;

public class MapsPresenter implements IMapsPresenter.Presenter {
    private IMapsPresenter.View view;

    @Override
    public void stopView() {
        if (view != null) {
            view = null;
        }
    }

    @Override
    public void startView(IMapsPresenter.View view) {
        this.view = view;
    }

    @Override
    public void init() {

    }
}
