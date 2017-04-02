package com.bstar.powerdata.activities.main;

import android.content.Context;
import android.text.TextUtils;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.base.BasePresenter;
import com.bstar.powerdata.events.OnMediaButtonClickEvent;
import com.bstar.powerdata.events.OnUpdatePBookAuth;
import com.bstar.powerdata.events.OutGoingCallEvent;
import com.bstar.powerdata.models.PBookAuth;
import com.bstar.powerdata.persistances.ApplicationPreferences;
import com.bstar.powerdata.utils.CalendarUtils;
import com.bstar.powerdata.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.Subscribe;

public class MainPresenter extends BasePresenter<MainView> {

    ApplicationPreferences mApplicationPreferences;

    public MainPresenter(){
        mApplicationPreferences = new ApplicationPreferences();
    }

    private static final String ACCESS_TOKEN_SERVICE_URL = "https://powerdata-test.herokuapp.com/accessToken";
    private static final String AUTH_PBOOK_URL = "https://pdbook.herokuapp.com/auth/";
    private static final String PLATFORM_KEY = "android";

    @Subscribe
    public void onOutGoingCall(OutGoingCallEvent event) {
        view.onOutGoingCall(event.getContact());
    }

    @Subscribe
    public void onOutGoingCall(OnMediaButtonClickEvent event) {
        view.onMediaButtonClick();
    }

    /*
     * Get an access token from your Twilio access token server
     */
    public void retrieveAccessToken(Context context) {
        PBookAuth mPBookAuth = mApplicationPreferences.getPBookAuth();
        if (mPBookAuth == null || TextUtils.isEmpty(mPBookAuth.getClientName())) {
            view.onReturnAccessToken(null, false);
            return;
        }
        Ion.with(context).load(ACCESS_TOKEN_SERVICE_URL).setBodyParameter("client", mPBookAuth.getClientName()).setBodyParameter("phoneNumber", mPBookAuth.getPhoneNumber()).setBodyParameter("platform", PLATFORM_KEY).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String accessToken) {
                if (e == null || !TextUtils.isEmpty(accessToken)) {
                    mApplicationPreferences.setTwilioToken(accessToken);
                    view.onReturnAccessToken(accessToken, true);
                } else {
                    view.onReturnAccessToken(accessToken, false);
                }
            }
        });
    }

    public void checkNewPBookToken(Context context, String token) {
        Ion.with(context).load(AUTH_PBOOK_URL + "?code=" + token).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray jsonArray) {
                if (e == null) {
                    try {
                        if(jsonArray.size() > 0){
                            String jsonString = jsonArray.get(0).toString();
                            PBookAuth mPBookAuth = new Gson().fromJson(jsonString, PBookAuth.class);
                            mApplicationPreferences.setPBookAuth(mPBookAuth);
                            getCurrentPBookAuth();
                            return;
                        }
                    } catch (JsonSyntaxException j) {
                    }
                }
                view.onReturnPBookAuth(PowerDataApplication.getStringResource(R.string.checking_pbook_auth_failed), false);
            }
        });
    }

    public void getCurrentPBookAuth() {
        PBookAuth mPBookAuth = mApplicationPreferences.getPBookAuth();
        if(mPBookAuth == null) {
            view.onReturnPBookAuth(null, false);
            return;
        }
        if(!validatePBookAuth(mPBookAuth)){
            view.onReturnPBookAuth(PowerDataApplication.getStringResource(R.string.pbook_auth_out_of_date), false);
            return;
        }
        mApplicationPreferences.setDeviceName(StringUtils.getDeviceNameFormat(mPBookAuth.getClientName()));
        postSticky(new OnUpdatePBookAuth(mPBookAuth));
        view.onReturnPBookAuth(null, true);
    }

    public boolean validatePBookAuth(PBookAuth pBookAuth) {
        if(pBookAuth == null || pBookAuth.getEndDate() == null) return false;
        if(CalendarUtils.isToday(CalendarUtils.getDateFromString(pBookAuth.getEndDate()))
                || CalendarUtils.isToday(CalendarUtils.getDateFromString(pBookAuth.getStartDate()))){
            return true;
        } else if(CalendarUtils.isAfter(CalendarUtils.getDateFromString(pBookAuth.getEndDate()), CalendarUtils.getCurrentTime())
                && CalendarUtils.isBefore(CalendarUtils.getDateFromString(pBookAuth.getStartDate()), CalendarUtils.getCurrentTime())){
            return true;
        } else {
            return false;
        }
    }

    public boolean isCurrentPBookAuthValid() {
        PBookAuth mPBookAuth = mApplicationPreferences.getPBookAuth();
        if(mPBookAuth == null) {
            view.onReturnPBookAuth(null, false);
            return false;
        }
        if(!validatePBookAuth(mPBookAuth)){
            view.onReturnPBookAuth(PowerDataApplication.getStringResource(R.string.pbook_auth_out_of_date), false);
            return false;
        }
        return true;
    }
}
