package com.bstar.powerdata.activities.voice;

import com.bstar.powerdata.base.BasePresenter;
import com.bstar.powerdata.events.OnMediaButtonClickEvent;

import org.greenrobot.eventbus.Subscribe;

public class VoicePresenter extends BasePresenter<VoiceView> {

    @Subscribe
    public void onOutGoingCall(OnMediaButtonClickEvent event) {
        view.onMediaButtonClick();
    }
}
