package com.bstar.powerdata.fragments.log;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.base.BasePresenter;
import com.bstar.powerdata.events.OnUpdateDeviceName;
import com.bstar.powerdata.events.OutGoingCallEvent;
import com.bstar.powerdata.models.CallLog;
import com.bstar.powerdata.models.CallLogGroup;
import com.bstar.powerdata.models.Contact;
import com.bstar.powerdata.models.PBookAuth;
import com.bstar.powerdata.persistances.ApplicationPreferences;
import com.bstar.powerdata.utils.CalendarUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class LogPresenter extends BasePresenter<LogView> {

    private static final String CAll_LOG_URL = "https://powerdata-test.herokuapp.com/callLog";

    public static final int CALL_LOG_FILTER_TODAY = 0;
    public static final int CALL_LOG_FILTER_YESTERDAY = 1;
    public static final int CALL_LOG_FILTER_DAY_BEFORE = 2;
    public static final int CALL_LOG_FILTER_OTHERS = 3;

    private ApplicationPreferences mApplicationPreferences;
    private List<CallLog> mCallLogList;
    private boolean isShowAllCallLog;

    public LogPresenter(){
        mApplicationPreferences = new ApplicationPreferences();
        isShowAllCallLog = true;
    }

    @Subscribe
    public void onUpdateDeviceNameEvent(OnUpdateDeviceName event) {
        view.onDeviceNameChanged();
    }

    public void doLoadCallLog(Context context) {
        PBookAuth mPBookAuth = mApplicationPreferences.getPBookAuth();
        if(mPBookAuth == null || TextUtils.isEmpty(mPBookAuth.getClientName())) return;
        Ion.with(context).load(CAll_LOG_URL).setBodyParameter("client", mPBookAuth.getClientName()).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject jsonObject) {
                if (e == null) {
                    try {
                        String jsonString = jsonObject.getAsJsonArray("Call").toString();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<CallLog>>() {}.getType();
                        mCallLogList = gson.fromJson(jsonString, listType);
                        if(view == null) return;
                        Collections.sort(mCallLogList);
                        handleFilterCallLog();
                    }catch (JsonSyntaxException j){
                        return;
                    }
                } else {
                    return;
                }
            }
        });
    }

    public void outGoingCall(Contact contact){
        post(new OutGoingCallEvent(contact));
    }



    private void handleFilterCallLog() {
        List<CallLog> visibleCallLogList;
        if(mCallLogList == null) return;
        if (isShowAllCallLog) {
            visibleCallLogList = mCallLogList;
        } else {
            visibleCallLogList = new ArrayList<>();
            for (int i = 0; i < mCallLogList.size(); i++) {
                if (!mCallLogList.get(i).getStatus().equals(PowerDataApplication.getStringResource(R.string.twilio_status_completed))
                        && mCallLogList.get(i).getType().equals(PowerDataApplication.getStringResource(R.string.twilio_inbox_type))) {
                    visibleCallLogList.add(mCallLogList.get(i));
                }
            }
        }
        List<CallLogGroup> callLogGroupList = new ArrayList<>();
        List<CallLog> todayCallLogList = new ArrayList<>();
        List<CallLog> yesterdayCallLogList = new ArrayList<>();
        List<CallLog> dayBeforeCallLogList = new ArrayList<>();
        List<CallLog> otherCallLogList = new ArrayList<>();
        for (int i = 0; i < visibleCallLogList.size(); i++) {
            Calendar day = CalendarUtils.getDateWithStringFormat(visibleCallLogList.get(i).getStartTime(), CalendarUtils.DATE_LOG_FORMAT);
            switch (CalendarUtils.dayBefore(day, CalendarUtils.getCurrentTime())) {
                case CALL_LOG_FILTER_TODAY:
                    todayCallLogList.add(visibleCallLogList.get(i));
                    break;
                case CALL_LOG_FILTER_YESTERDAY:
                    yesterdayCallLogList.add(visibleCallLogList.get(i));
                    break;
                case CALL_LOG_FILTER_DAY_BEFORE:
                    dayBeforeCallLogList.add(visibleCallLogList.get(i));
                    break;
                default:
                    otherCallLogList.add(visibleCallLogList.get(i));
                    break;
            }
        }
        callLogGroupList.add(new CallLogGroup(PowerDataApplication.getStringResource(R.string.log_filter_today), todayCallLogList));
        callLogGroupList.add(new CallLogGroup(PowerDataApplication.getStringResource(R.string.log_filter_yesterday), yesterdayCallLogList));
        callLogGroupList.add(new CallLogGroup(PowerDataApplication.getStringResource(R.string.log_filter_day_before), dayBeforeCallLogList));
        callLogGroupList.add(new CallLogGroup(PowerDataApplication.getStringResource(R.string.log_filter_other), otherCallLogList));
        view.showCallLog(callLogGroupList);
    }

    void setShowAllCallLog(boolean isShowAll){
        isShowAllCallLog = isShowAll;
        handleFilterCallLog();
    }
}
