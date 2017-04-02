package com.bstar.powerdata.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class IpLocation{
    @SerializedName("ip")
    final String ip;
    @SerializedName("city")
    final String city;
    @SerializedName("country")
    final String country;
}
