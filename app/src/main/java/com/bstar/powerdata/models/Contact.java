package com.bstar.powerdata.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class Contact implements Serializable{
    String userName;
    String phoneNumber;
}
