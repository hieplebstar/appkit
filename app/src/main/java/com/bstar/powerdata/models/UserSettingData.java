package com.bstar.powerdata.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class UserSettingData {
    @SerializedName("forward_no")
    final String forwardNo;
    @SerializedName("new_number")
    final String newNumber;
    @SerializedName("cost_10")
    final String cost;
    @SerializedName("expired_date")
    final String expiredDate;
}
