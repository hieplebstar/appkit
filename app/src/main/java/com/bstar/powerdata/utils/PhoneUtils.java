package com.bstar.powerdata.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.models.Contact;
import com.bstar.powerdata.models.CountryCallingCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PhoneUtils {

    public static String getContactName(Context context, String phoneNumber) {
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(uri,  new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()){
                    return cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                }
            }
        } catch (Exception ex){
            return PowerDataApplication.getStringResource(R.string.no_name);
        }
        return PowerDataApplication.getStringResource(R.string.no_name);
    }

    public static List<CountryCallingCode> getListCountryFromAsset(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("country.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, "UTF-8");
            Type listType = new TypeToken<ArrayList<CountryCallingCode>>() {}.getType();
            return new Gson().fromJson(jsonString, listType);
        } catch (IOException e) {
            return null;
        }
    }

    public static List<String> getListCountryName(List<CountryCallingCode> countryCallingCodes) {
        List<String> results = new ArrayList<>();
        for (CountryCallingCode countryCallingCode : countryCallingCodes) {
            results.add(countryCallingCode.getName());
        }
        return results;
    }

    public static CountryCallingCode getCountryByPhoneNumber(List<CountryCallingCode> countryCallingCodes, String phoneNumber) {
        List<String> results = new ArrayList<>();
        for (CountryCallingCode countryCallingCode : countryCallingCodes) {
            if(phoneNumber.startsWith("+" + countryCallingCode.getCallingCode())){
                return countryCallingCode;
            }
        }
        return null;
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     * @param uri
     */
    public static Contact contactPicked(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String phoneNo = cursor.getString(phoneIndex);
            String name = cursor.getString(nameIndex);
            return  new Contact(name, phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
