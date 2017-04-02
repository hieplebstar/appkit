package com.example.shadow.appkit.event;

import android.support.v4.app.Fragment;

public class FragmentLifeCycleEvent {
    public Class<? extends Fragment> fragmentClass;
    private boolean onCreate = false;
    private boolean onStart = false;
    public boolean onAttach = false;
    public boolean onResume = false;
    public boolean onPause = false;
    public boolean onDetach = false;
    public boolean onStop = false;
    public boolean onDestroy = false;

    public static FragmentLifeCycleEvent onCreate(Class<? extends Fragment> fragmentClass) {
        FragmentLifeCycleEvent event = new FragmentLifeCycleEvent();
        event.fragmentClass = fragmentClass;
        event.onCreate = true;
        return event;
    }

    public static FragmentLifeCycleEvent onStart(Class<? extends Fragment> fragmentClass) {
        FragmentLifeCycleEvent event = new FragmentLifeCycleEvent();
        event.fragmentClass = fragmentClass;
        event.onStart = true;
        return event;
    }

    public static FragmentLifeCycleEvent onAttach(Class<? extends Fragment> fragmentClass) {
        FragmentLifeCycleEvent event = new FragmentLifeCycleEvent();
        event.fragmentClass = fragmentClass;
        event.onAttach = true;
        return event;
    }

    public static FragmentLifeCycleEvent onResume(Class<? extends Fragment> fragmentClass) {
        FragmentLifeCycleEvent event = new FragmentLifeCycleEvent();
        event.fragmentClass = fragmentClass;
        event.onResume = true;
        return event;
    }

    public static FragmentLifeCycleEvent onPause(Class<? extends Fragment> fragmentClass) {
        FragmentLifeCycleEvent event = new FragmentLifeCycleEvent();
        event.fragmentClass = fragmentClass;
        event.onPause = true;
        return event;
    }

    public static FragmentLifeCycleEvent onDetach(Class<? extends Fragment> fragmentClass) {
        FragmentLifeCycleEvent event = new FragmentLifeCycleEvent();
        event.fragmentClass = fragmentClass;
        event.onDetach = true;
        return event;
    }

    public static FragmentLifeCycleEvent onStop(Class<? extends Fragment> fragmentClass) {
        FragmentLifeCycleEvent event = new FragmentLifeCycleEvent();
        event.fragmentClass = fragmentClass;
        event.onStop = true;
        return event;
    }

    public static FragmentLifeCycleEvent onDestroy(Class<? extends Fragment> fragmentClass) {
        FragmentLifeCycleEvent event = new FragmentLifeCycleEvent();
        event.fragmentClass = fragmentClass;
        event.onDestroy = true;
        return event;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fragmentClass.getSimpleName());
        sb.append(".");
        if (onCreate) {
            sb.append("onCreate()");
        } else if (onStart) {
            sb.append("onStart()");
        } else if (onResume) {
            sb.append("onResume()");
        } else if (onAttach) {
            sb.append("onAttach()");
        } else if (onDetach) {
            sb.append("onDetach()");
        } else if (onPause) {
            sb.append("onPause()");
        } else if (onStop) {
            sb.append("onStop()");
        } else if (onDestroy) {
            sb.append("onDestroy()");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
