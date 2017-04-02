package com.bstar.powerdata.fragments.call;

import android.content.Context;
import android.text.TextUtils;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.base.BasePresenter;
import com.bstar.powerdata.events.OutGoingCallEvent;
import com.bstar.powerdata.models.CallLog;
import com.bstar.powerdata.models.Contact;
import com.bstar.powerdata.models.PBookAuth;
import com.bstar.powerdata.persistances.ApplicationPreferences;
import com.bstar.powerdata.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CallPresenter extends BasePresenter<CallView> {

    private static final String CAll_MINUTES_URL = "https://powerdata-test.herokuapp.com/callMinutes";

    private ApplicationPreferences mApplicationPreferences;

    public CallPresenter(){
        mApplicationPreferences = new ApplicationPreferences();
    }

    public void outGoingCall(Contact contact){
        post(new OutGoingCallEvent(contact));
    }

    public void doGetCallMinutesAvailable(Context context) {
        final PBookAuth mPBookAuth = mApplicationPreferences.getPBookAuth();
        if(mPBookAuth == null || TextUtils.isEmpty(mPBookAuth.getClientName())) return;
        Ion.with(context).load(CAll_MINUTES_URL).setBodyParameter("client", mPBookAuth.getClientName()).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject jsonObject) {
                if (e == null) {
                    try {
                        String time = jsonObject.get("minutes").getAsString();
                        if(TextUtils.isEmpty(time) || TextUtils.isEmpty(mPBookAuth.getFreeMin())) return;
                        int availableTime =  Integer.parseInt(mPBookAuth.getFreeMin())*60 - Integer.parseInt(time);
                        String result;
                        if(availableTime > 0){
                            result = PowerDataApplication.getStringResource(R.string.available_time, StringUtils.getDurationTimeFormat(availableTime));
                        } else {
                            result = PowerDataApplication.getStringResource(R.string.out_of_free_time);
                        }
                        view.onReturnCallMinutes(result);
                    }catch (JsonSyntaxException j){
                        return;
                    }
                } else {
                    return;
                }
            }
        });
    }
}
