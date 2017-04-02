package com.example.shadow.appkit.eventbus;

import android.app.Application;
import android.os.Handler;
import android.text.style.TtsSpan;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by shadow on 4/2/2017.
 */

public abstract class EventBusController{

    protected abstract List<Object> createDefaultSubscribers();
    protected void onBeforeRegisterDefaultSubscribers(){}
    protected void onBeforeUnregisterAllEventSubscribers(){}

    protected final List<Object> eventSubscribers = new ArrayList<>();
    private final Handler mHandler;

    public EventBusController() {
        super();
        mHandler = new Handler();
    }

    public boolean postDelayed(final Object event, long delay) {
        return mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postEvent(event);
            }
        }, delay);
    }

    public void postEvent(Object event) {
        EventBus.getDefault().post(event);
    }

    public void postEventDelayed(Object event, long delay) {
        this.postDelayed(event, delay);
    }

    public void postStickyEvent(Object event) {
        EventBus.getDefault().postSticky(event);
    }

    public <T> T getSticky(Class<T> eventType) {
        return EventBus.getDefault().getStickyEvent(eventType);
    }

    public <T> T removeSticky(Class<T> eventType) {
        return EventBus.getDefault().removeStickyEvent(eventType);
    }

    public void registerDefaultSubcriber() {
        onBeforeRegisterDefaultSubscribers();
        registerAll(createDefaultSubscribers());
    }

    public void unregisterAllSubscribers() {
        onBeforeUnregisterAllEventSubscribers();
        unregisterAll(eventSubscribers);
        eventSubscribers.clear();
    }

    public void registerAll(List<Object> subscribers) {
        for (Object subscriber : subscribers){
            registerSubscriber(subscriber);
        }
    }

    public void unregisterAll(List<Object> subscribers) {
        for (Object subscriber : subscribers){
            unregisterSubscriber(subscriber);
        }
    }

    public void registerSubscriber(Object subscriber) {
        if(eventSubscribers.contains(subscriber)) return;
        eventSubscribers.add(subscriber);
        EventBus.getDefault().register(subscriber);
    }

    public void unregisterSubscriber(Object subscriber) {
        if(!eventSubscribers.contains(subscriber)) return;
        eventSubscribers.remove(subscriber.getClass());
        EventBus.getDefault().unregister(subscriber);
    }
}
