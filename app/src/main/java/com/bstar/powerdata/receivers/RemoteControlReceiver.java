package com.bstar.powerdata.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.bstar.powerdata.events.OnMediaButtonClickEvent;

import org.greenrobot.eventbus.EventBus;

public class RemoteControlReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event == null) {
                return;
            }

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                EventBus.getDefault().post(new OnMediaButtonClickEvent());
            }
        }
    }

}