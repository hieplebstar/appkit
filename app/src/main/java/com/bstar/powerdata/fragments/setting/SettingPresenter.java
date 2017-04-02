package com.bstar.powerdata.fragments.setting;

import android.content.Context;

import com.bstar.powerdata.base.BasePresenter;
import com.bstar.powerdata.events.OnUpdatePBookAuth;
import com.bstar.powerdata.models.IpLocation;
import com.bstar.powerdata.models.PBookAuth;
import com.bstar.powerdata.models.UserSettingData;
import com.bstar.powerdata.persistances.ApplicationPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.Subscribe;

public class SettingPresenter extends BasePresenter<SettingView> {

    private static final String IP_LOCATION_URL = "http://ipinfo.io/json";
    private static final String PHONE_NUMBER_VERIFICATION_URL = "https://powerdata-test.herokuapp.com/verification";
    private static final String CHECK_PHONE_NUMBER_URL = "https://powerdata-test.herokuapp.com/checkPhoneNumber";

    ApplicationPreferences mApplicationPreferences;
    PBookAuth mPBookAuth;

    SettingPresenter() {
        mApplicationPreferences = new ApplicationPreferences();
    }

    @Subscribe(sticky = true)
    public void onUpdatePBookAuth(OnUpdatePBookAuth event) {
        mPBookAuth = mApplicationPreferences.getPBookAuth();
        view.onUpdatePBookAuth(mApplicationPreferences.getPBookAuth());
    }

    public void getPhoneNumber(String destination, String hone, String days) {
        //create mock data
        view.onUserDataReturn(new UserSettingData("7324225521", "7324225521", "1 USD per 10 mins", "23/02/2017"));
    }

    public void doGetLocation(Context context) {
        Ion.with(context).load(IP_LOCATION_URL).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject jsonObject) {
                if (e == null) {
                    try {
                        IpLocation ipLocation = new Gson().fromJson(jsonObject, IpLocation.class);
                        view.onGetLocationReturn(ipLocation);
                    }catch (JsonSyntaxException j){
                        return;
                    }
                } else {
                    return;
                }
            }
        });
    }

    public void doVerifyPhoneNumber(Context context, final String phoneNumber) {
        if(mPBookAuth == null) return;
        Ion.with(context).load(PHONE_NUMBER_VERIFICATION_URL).setBodyParameter("phoneNumber", phoneNumber).setBodyParameter("friendlyName", mPBookAuth.getClientName()).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e == null) {
                    view.onVerifyPhoneNumberReturn(phoneNumber, result.get("validation_code").getAsString());
                } else {
                    view.onVerifyPhoneNumberReturn(phoneNumber, "");
                }
            }
        });
    }

    public void doCheckPhoneNumber(Context context, final String phoneNumber) {
        if(mPBookAuth == null) return;
        Ion.with(context).load(CHECK_PHONE_NUMBER_URL).setBodyParameter("phoneNumber", phoneNumber).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e == null) {
                    view.onCheckPhoneNumberReturn(result.get("verified").getAsBoolean());
                } else {
                    view.onCheckPhoneNumberReturn(false);
                }
            }
        });
    }
}
