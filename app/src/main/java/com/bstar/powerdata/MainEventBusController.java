package com.bstar.powerdata;

import com.bstar.powerdata.fragments.dial.ShowDialPluginController;
import com.example.shadow.appkit.eventbus.EventBusController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shadow on 4/2/2017.
 */

public class MainEventBusController extends EventBusController {
    @Override
    protected List<Object> createDefaultSubscribers() {
        List<Object> subscribers = new ArrayList<>();
        subscribers.add(new ShowDialPluginController());
        return subscribers;
    }
}
