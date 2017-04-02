package com.bstar.powerdata.persistances;


import android.text.TextUtils;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.base.BasePreferences;
import com.bstar.powerdata.models.CountryCallingCode;
import com.bstar.powerdata.models.PBookAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HiepLe on 18/10/2016.
 */
public class ApplicationPreferences extends BasePreferences {
    public static final String PREF_DEVICE_NAME = APPLICATION_PREFERENCES + "_DEVICE_NAME";
    public static final String PREF_TWILIO_TOKEN = APPLICATION_PREFERENCES + "_TWILIO_TOKEN";
    public static final String PREF_PBOOK_AUTH = APPLICATION_PREFERENCES + "_PBOOK_AUTH";
    public static final String PREF_RECENT_COUNTRIES = APPLICATION_PREFERENCES + "_RECENT_COUNTRIES";
    public static final String PREF_DESTINATION_COUNTRY = APPLICATION_PREFERENCES + "_DESTINATION_COUNTRY";
    public static final String PREF_HOME_COUNTRY = APPLICATION_PREFERENCES + "_HOME_COUNTRY";
    public static final String PREF_SELECTED_DAYS = APPLICATION_PREFERENCES + "_SELECTED_DAYS";
    public static final String PREF_CALL_FORWARD = APPLICATION_PREFERENCES + "_CALL_FORWARD";
    public static final String PREF_CALLER_COUNTRY = APPLICATION_PREFERENCES + "_CALLER_COUNTRY";
    public static final String PREF_CALLER_PHONE_NUMBER = APPLICATION_PREFERENCES + "_CALLER_PHONE_NUMBER";
    public static final String PREF_CALLER_PHONE_NUMBER_VERIFIED = APPLICATION_PREFERENCES + "_CALLER_PHONE_NUMBER_VERIFIED";


    public static final int DEFAULT_DESTINATION_COUNTRY_POSITION = 18;
    public static final int DEFAULT_HOME_COUNTRY_POSITION = 38;
    public static final int DEFAULT_SELECTED_DAYS_POSITION = 0;
    public static final int DEFAULT_CALLER_COUNTRY_POSITION = 0;

    public ApplicationPreferences() {
        super(PowerDataApplication.getInstance());
    }

    public String getTwilioToken() {
        return mPreferences.getString(PREF_TWILIO_TOKEN, null);
    }

    public void setTwilioToken(String token) {
        mPreferences.edit().putString(PREF_TWILIO_TOKEN, token).apply();
    }

    public String getDeviceName() {
        return mPreferences.getString(PREF_DEVICE_NAME, null);
    }

    public void setDeviceName(String deviceName) {
        mPreferences.edit().putString(PREF_DEVICE_NAME, deviceName).apply();
    }

    public String getCallerPhoneNumber() {
        return mPreferences.getString(PREF_CALLER_PHONE_NUMBER, "");
    }

    public void setCallerPhoneNumber(String phoneNumber) {
        mPreferences.edit().putString(PREF_CALLER_PHONE_NUMBER, phoneNumber).apply();
    }

    public PBookAuth getPBookAuth() {
        String jsonString = mPreferences.getString(PREF_PBOOK_AUTH, null);
        if(TextUtils.isEmpty(jsonString)) return null;
        return new Gson().fromJson(jsonString, PBookAuth.class);
    }

    public void setPBookAuth(PBookAuth pBookAuth) {
        if(pBookAuth == null) return;
        mPreferences.edit().putString(PREF_PBOOK_AUTH, new Gson().toJson(pBookAuth).toString()).apply();
    }

    public boolean getCallForward() {
        return mPreferences.getBoolean(PREF_CALL_FORWARD, false);
    }

    public void setCallForward(boolean isAuto) {
        mPreferences.edit().putBoolean(PREF_CALL_FORWARD, isAuto).apply();
    }

    public List<CountryCallingCode> getRecentCountries() {
        String jsonString = mPreferences.getString(PREF_RECENT_COUNTRIES, null);
        if(TextUtils.isEmpty(jsonString)) return new ArrayList<CountryCallingCode>();
        Type listType = new TypeToken<ArrayList<CountryCallingCode>>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }

    public void setRecentCountries(List<CountryCallingCode> countryCallingCodes) {
        if(countryCallingCodes == null) return;
        Type listType = new TypeToken<ArrayList<CountryCallingCode>>() {}.getType();
        mPreferences.edit().putString(PREF_RECENT_COUNTRIES, new Gson().toJson(countryCallingCodes, listType).toString()).apply();
    }

    public int getDestinationCountryPosition() {
        return mPreferences.getInt(PREF_DESTINATION_COUNTRY, DEFAULT_DESTINATION_COUNTRY_POSITION);
    }

    public void setDestinationCountry(int position) {
        mPreferences.edit().putInt(PREF_DESTINATION_COUNTRY, position).apply();
    }

    public int getHomeCountryPosition() {
        return mPreferences.getInt(PREF_HOME_COUNTRY, DEFAULT_HOME_COUNTRY_POSITION);
    }

    public void setHomeCountry(int position) {
        mPreferences.edit().putInt(PREF_HOME_COUNTRY, position).apply();
    }

    public int getSelectedDaysPosition() {
        return mPreferences.getInt(PREF_SELECTED_DAYS, DEFAULT_SELECTED_DAYS_POSITION);
    }

    public void setSelectedDays(int position) {
        mPreferences.edit().putInt(PREF_SELECTED_DAYS, position).apply();
    }

    public int getCallerCountryPosition() {
        return mPreferences.getInt(PREF_CALLER_COUNTRY, DEFAULT_CALLER_COUNTRY_POSITION);
    }

    public void setCallerCountry(int position) {
        mPreferences.edit().putInt(PREF_CALLER_COUNTRY, position).apply();
    }

    public boolean getCallerVerified() {
        return mPreferences.getBoolean(PREF_CALLER_PHONE_NUMBER_VERIFIED, false);
    }

    public void setCallerVerified(boolean isVerified) {
        mPreferences.edit().putBoolean(PREF_CALLER_PHONE_NUMBER_VERIFIED, isVerified).apply();
    }

    @Override
    public void clear() {
        mPreferences.edit().remove(PREF_DEVICE_NAME).apply();
        mPreferences.edit().remove(PREF_TWILIO_TOKEN).apply();
        mPreferences.edit().remove(PREF_PBOOK_AUTH).apply();
        mPreferences.edit().remove(PREF_DESTINATION_COUNTRY).apply();
        mPreferences.edit().remove(PREF_HOME_COUNTRY).apply();
        mPreferences.edit().remove(PREF_SELECTED_DAYS).apply();
        mPreferences.edit().remove(PREF_CALL_FORWARD).apply();
        mPreferences.edit().remove(PREF_CALLER_PHONE_NUMBER).apply();
        mPreferences.edit().remove(PREF_CALLER_COUNTRY).apply();
        mPreferences.edit().remove(PREF_CALLER_PHONE_NUMBER_VERIFIED).apply();
    }
}
