package com.bstar.powerdata.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class PBookAuth {
    @SerializedName("_id")
    final String id;
    @SerializedName("code")
    final String code;
    @SerializedName("createDate")
    final String createDate;
    @SerializedName("startDate")
    final String startDate;
    @SerializedName("endDate")
    final String endDate;
    @SerializedName("country")
    final String country;
    @SerializedName("clientName")
    final String clientName;
    @SerializedName("phoneNumber")
    final String phoneNumber;
    @SerializedName("freemin")
    final String freeMin;
}
