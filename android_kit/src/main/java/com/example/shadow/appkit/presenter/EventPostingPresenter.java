package com.example.shadow.appkit.presenter;


import com.example.shadow.appkit.AndroidApplication;
import com.example.shadow.appkit.eventbus.EventBusController;

public abstract class EventPostingPresenter {

    public void post(Object event) {
        AndroidApplication.getEvenBusController().postEvent(event);
    }

    protected void postDelay(Object event, long delay) {
        AndroidApplication.getEvenBusController().postEventDelayed(event, delay);
    }

    protected void postSticky(Object event) {
        AndroidApplication.getEvenBusController().postStickyEvent(event);
    }

    protected <T> T removeSticky(Class<T> eventClass) {
        return AndroidApplication.getEvenBusController().removeSticky(eventClass);
    }

    protected <T> T getSticky(Class<T> eventClass) {
        return AndroidApplication.getEvenBusController().getSticky(eventClass);
    }
}
