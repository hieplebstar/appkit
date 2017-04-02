package com.bstar.powerdata.fragments.setting;

import com.bstar.powerdata.models.IpLocation;
import com.bstar.powerdata.models.PBookAuth;
import com.bstar.powerdata.models.UserSettingData;

public interface SettingView {
    void onUpdatePBookAuth(PBookAuth pBookAuth);
    void onUserDataReturn(UserSettingData userSettingData);
    void onGetLocationReturn(IpLocation ipLocation);
    void onVerifyPhoneNumberReturn(String phoneNumber, String verifyCode);
    void onCheckPhoneNumberReturn(boolean isVerified);
}
