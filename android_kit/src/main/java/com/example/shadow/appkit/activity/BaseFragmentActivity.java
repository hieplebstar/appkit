package com.example.shadow.appkit.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.shadow.appkit.AndroidApplication;
import com.example.shadow.appkit.event.AttachFragmentEvent;
import com.example.shadow.appkit.event.DetachFragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by shadow on 4/2/2017.
 */

public abstract class BaseFragmentActivity  extends AppCompatActivity{

    protected abstract int getContainerId(Class<? extends Fragment> fragmentClass);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplication.getEvenBusController().registerSubscriber(this);
    }

    @Override
    protected void onDestroy() {
        AndroidApplication.getEvenBusController().unregisterSubscriber(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAttachFragmentEvent(AttachFragmentEvent event) {
        Fragment fragment = event.fragment;
        if (fragment instanceof DialogFragment) {
            openDialog((DialogFragment) fragment);
        } else {
            attachFragment(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDetachFragmentEvent(DetachFragmentEvent event) {
        String tag = event.fragmentClass.getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            return;
        }
        removeFragment(fragment, tag);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object event) {
        Log.d("Eventbus",event.getClass().getName());
    }

    private void openDialog(DialogFragment dialog) {
        dialog.show(getSupportFragmentManager(), ((Object) dialog).getClass().getSimpleName());
    }

    private void attachFragment(AttachFragmentEvent event) {
        int containerId = getContainerId(event.getFragmentClass());
        if(getSupportFragmentManager().findFragmentByTag(event.getFragmentClass().getName()) != null){
            boolean isPopped = getSupportFragmentManager().popBackStackImmediate(event.getFragmentClass().getName(), 0);
            if(isPopped) return;
        }
        if (!event.addToBackStack) {
            clearBackStackFragments();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (event.addToBackStack) {
            transaction.addToBackStack(event.fragment.getClass().getName());
            transaction.setBreadCrumbTitle(event.actionBarTitle);
        } else if (event.actionBarTitle != null) {
            getSupportActionBar().setTitle(event.actionBarTitle);
        }
        transaction.setCustomAnimations(event.enterAnimation, event.exitAnimation, event.popEnterAnimation, event.popExitAnimation);
        transaction.replace(containerId, event.fragment, event.fragment.getClass().getName());
        transaction.commit();
    }

    private void bringFragmentToFront(AttachFragmentEvent event) {
        getSupportFragmentManager().popBackStackImmediate(event.getFragmentClass().getName(), 0);
        attachFragment(event);
    }

    private void clearBackStackFragments() {
        FragmentManager fm = getSupportFragmentManager();
        while (fm.getBackStackEntryCount() != 0) {
            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(0);
            Fragment fragment = fm.findFragmentByTag(entry.getName());
            if (fragment != null) {
                removeFragment(fragment, fragment.getTag());
            }
        }
    }

    private void removeFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
        getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
