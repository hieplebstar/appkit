package com.bstar.powerdata.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.models.Contact;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final String UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'z'";
    public static final String DEVICE_NAME_REGEX = "^[0-9a-zA-Z\\_]{1,50}$";

    public static String getDurationTimeFormat(int duration) {
        if(duration < 0) return null;

        int hours = duration/3600;
        int minutes = (duration%3600)/60;
        int secs = duration%60;

        if(duration < 60){
            return secs + " secs";
        }
        if(duration < 3600){
            return minutes + " mins " + secs + " secs";
        }
        return hours + " hours " + minutes + " mins " + secs + " secs";
    }

    public static String getDeviceNameFormat(String name) {
        return name.toLowerCase().trim().replace(" ", "");
    }

    public static boolean validateDeviceName(String name) {
        final Pattern pattern = Pattern.compile(DEVICE_NAME_REGEX);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        if(TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 8 || phoneNumber.length() > 12) return false;
        return true;
    }

    public static boolean validateVerificationCode(String verificationCode) {
        if(TextUtils.isEmpty(verificationCode) || verificationCode.length() != 6) return false;
        return true;
    }

    public static String getAvatarAltFromUserName(String name) {
        if(name.equals(PowerDataApplication.getStringResource(R.string.no_name))){
            return PowerDataApplication.getStringResource(R.string.default_no_name);
        }
        if(TextUtils.isEmpty(name)){
            return "";
        }
        String result = name.trim();
        if(TextUtils.indexOf(result, " ") >= 0){
            int position = TextUtils.indexOf(result, " ");
            for(int i = position; i < result.length(); i++){
                if(result.charAt(i) != ' '){
                    result = "" + result.charAt(0) + result.charAt(i);
                    return result.toUpperCase();
                }
            }
        }
        return result.substring(0, result.length() > 2 ? 2 : 1).toUpperCase();
    }

}
