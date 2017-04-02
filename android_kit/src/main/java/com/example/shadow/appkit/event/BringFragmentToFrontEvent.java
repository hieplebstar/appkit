package com.example.shadow.appkit.event;


import android.support.v4.app.Fragment;

public class BringFragmentToFrontEvent extends TransactionFragmentEvent {

    private final String actionBarTitle;
    public Class<? extends Fragment> fragmentClass;

    public BringFragmentToFrontEvent(Class<? extends Fragment> fragmentClass, String actionBarTitle) {
        super(fragmentClass);
        this.actionBarTitle = actionBarTitle;
        this.fragmentClass = fragmentClass;
    }
}