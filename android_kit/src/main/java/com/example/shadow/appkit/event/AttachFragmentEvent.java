package com.example.shadow.appkit.event;


import android.support.annotation.AnimRes;
import android.support.v4.app.Fragment;

public class AttachFragmentEvent extends TransactionFragmentEvent {
    public Fragment fragment;
    public boolean addToBackStack;
    public String actionBarTitle;
    public  int enterAnimation;
    public int exitAnimation;
    public int popEnterAnimation;
    public int popExitAnimation;

    public AttachFragmentEvent(Fragment fragment) {
        this(fragment, false, null);
    }

    public AttachFragmentEvent(Fragment fragment, boolean addToBackStack, String actionBarTitle) {
        super((Class<? extends Fragment>) ((Object)fragment).getClass());
        this.fragment = fragment;
        this.addToBackStack = addToBackStack;
        this.actionBarTitle = actionBarTitle;
    }

    public void setAnimations(@AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit) {
        enterAnimation = enter;
        exitAnimation = exit;
        popEnterAnimation = popEnter;
        popExitAnimation = popExit;
    }
}
