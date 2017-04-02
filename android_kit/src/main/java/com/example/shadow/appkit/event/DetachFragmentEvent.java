package com.example.shadow.appkit.event;

import android.support.v4.app.Fragment;

public class DetachFragmentEvent {

    public Class<? extends Fragment> fragmentClass;

    public DetachFragmentEvent(Class<? extends Fragment> fragmentClass) {
        this.fragmentClass = fragmentClass;
    }
}
