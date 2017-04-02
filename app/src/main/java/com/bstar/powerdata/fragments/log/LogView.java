package com.bstar.powerdata.fragments.log;

import com.bstar.powerdata.models.CallLog;
import com.bstar.powerdata.models.CallLogGroup;

import java.util.List;

public interface LogView {
    void showCallLog(List<CallLogGroup> callLogGroups);
    void onDeviceNameChanged();
}
