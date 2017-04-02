package com.bstar.powerdata.models;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

import lombok.Data;

@Data
public class CallLogGroup implements Parent<CallLog> {
    final String title;
    final private List<CallLog> mCallLogList;

    public CallLog getCallLog(int position) {
        return mCallLogList.get(position);
    }

    @Override
    public List<CallLog> getChildList() {
        return mCallLogList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
