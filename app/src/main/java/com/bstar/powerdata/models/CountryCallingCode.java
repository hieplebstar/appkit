package com.bstar.powerdata.models;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountryCallingCode {
    @SerializedName("country")
    private String name;
    @SerializedName("country_code")
    private String countryCode;
    @SerializedName("icon")
    private String icon;
    @SerializedName("code")
    private String callingCode;
}
