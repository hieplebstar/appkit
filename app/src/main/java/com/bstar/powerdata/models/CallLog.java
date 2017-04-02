package com.bstar.powerdata.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

import lombok.Data;

@Data
public class CallLog implements Parcelable, Comparable<CallLog>{
    @SerializedName("contact")
    final String contact;
    @SerializedName("type")
    final String type;
    @SerializedName("status")
    final String status;
    @SerializedName("duration")
    final int duration;
    @SerializedName("starttime")
    final String startTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contact);
        dest.writeString(type);
        dest.writeString(status);
        dest.writeInt(duration);
        dest.writeString(startTime);
    }

    protected CallLog(Parcel in) {
        contact = in.readString();
        type = in.readString();
        status = in.readString();
        duration = in.readInt();
        startTime = in.readString();
    }

    public static final Creator<CallLog> CREATOR = new Creator<CallLog>() {
        @Override
        public CallLog createFromParcel(Parcel in) {
            return new CallLog(in);
        }


        @Override
        public CallLog[] newArray(int size) {
            return new CallLog[size];
        }
    };

    @Override
    public int compareTo(CallLog callLog) {
        return callLog.getStartTime().compareTo(this.startTime);
    }
}
