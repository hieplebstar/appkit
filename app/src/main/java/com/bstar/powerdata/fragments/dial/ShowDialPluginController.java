package com.bstar.powerdata.fragments.dial;

import com.example.shadow.appkit.controller.FragmentPluginController;

import org.greenrobot.eventbus.Subscribe;

public class ShowDialPluginController extends FragmentPluginController<ShowDialEvent, DialFragment> {
    public ShowDialPluginController() {
        super(DialFragment.class);
    }

    @Override
    protected String createActionBarTitle() {
        return null;
    }

    @Subscribe
    public void onEvent(ShowDialEvent event) {
        super.onCapturedEvent(event);
    }

    @Override
    protected boolean shouldBeAddedToBackStack() {
        return true;
    }

    @Override
    protected DialFragment createFragment(ShowDialEvent event) {
        return DialFragment.newInstance();
    }
}
