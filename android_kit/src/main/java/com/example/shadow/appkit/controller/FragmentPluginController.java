package com.example.shadow.appkit.controller;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.v4.app.Fragment;

import com.example.shadow.appkit.event.AttachFragmentEvent;
import com.example.shadow.appkit.presenter.EventPostingPresenter;

import org.greenrobot.eventbus.Subscribe;

public abstract class FragmentPluginController<ShowFragmentEvent, FragmentType extends Fragment> extends EventPostingPresenter {

    protected abstract FragmentType createFragment(ShowFragmentEvent event);
    protected abstract boolean shouldBeAddedToBackStack();

    private Class<? extends Fragment> mFragmentClass;

    public FragmentPluginController(Class<? extends FragmentType> fragmentClass) {
        mFragmentClass = fragmentClass;
    }

    public void onCapturedEvent(ShowFragmentEvent event) {
        attachFragment(event);
    }

    protected boolean shouldDisplayFragment(ShowFragmentEvent event) {
        return true;
    }

    private void attachFragment(ShowFragmentEvent event) {
        FragmentType fragment = createFragment(event);
        if (fragment.getArguments() == null) {
            fragment.setArguments(new Bundle());
        }
        AttachFragmentEvent attachFragmentEvent = new AttachFragmentEvent(fragment, shouldBeAddedToBackStack(), createActionBarTitle());
        attachFragmentEvent.setAnimations(createEnterAnimationResId(), createExitAnimationResId(), createPopEnterAnimationResId(), createPopExitAnimationResId());
        post(attachFragmentEvent);
    }

    /**
     * Override this method to set an actionbar title. Return String, empty String or null.
     * Return String to change the actionBarTitle when the fragment is attached
     * Return empty String in case you want to force no title to be shown
     * Return null if the current title should not be changed when this fragment is attached.
     * @return String the actionbar title
     */
    protected String createActionBarTitle() {
        return null;
    }

    protected @AnimRes
    int createEnterAnimationResId() {
        return 0;
    }

    protected @AnimRes
    int createExitAnimationResId() {
        return 0;
    }

    protected @AnimRes
    int createPopEnterAnimationResId() {
        return 0;
    }

    protected @AnimRes
    int createPopExitAnimationResId() {
        return 0;
    }
}
