package com.example.shadow.appkit.event;


import android.support.v4.app.Fragment;

import lombok.Data;

@Data
public class TransactionFragmentEvent {

    public Class<? extends Fragment> fragmentClass;

    public TransactionFragmentEvent(Class<? extends Fragment> fragmentClass) {
        this.fragmentClass = fragmentClass;
    }
}
