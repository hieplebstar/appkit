package com.bstar.powerdata.base;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

public abstract class BasePresenter<View>  {

    protected View view;

    public void setView(View view){
        this.view = view;
    }

    public void start(View view) {
        this.view = view;
        EventBus.getDefault().register(this);
    }

    public void stop() {
        EventBus.getDefault().unregister(this);
        this.view = null;
    }

    public void post(Object object) {
        EventBus.getDefault().post(object);
    }

    public void postSticky(Object object) {
        EventBus.getDefault().postSticky(object);
    }
}
