package com.example.shadow.appkit.presenter;

import com.example.shadow.appkit.AndroidApplication;

public abstract class EventListeningPresenter<View> extends EventPostingPresenter {

    protected View view;

    public void setView(View view) {
        this.view = view;
    }

    public void start(View view) {
        this.view = view;
        AndroidApplication.getEvenBusController().registerSubscriber(this);
    }

    public void stop() {
        AndroidApplication.getEvenBusController().unregisterSubscriber(this);
        this.view = null;
    }
}
