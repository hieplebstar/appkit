package com.bstar.powerdata.activities.main;

import com.bstar.powerdata.models.Contact;
import com.bstar.powerdata.models.PBookAuth;

public interface MainView {
    void showMessage(String message);
    void onOutGoingCall(Contact contact);
    void onReturnAccessToken(String accessToken, boolean isSuccess);
    void onReturnPBookAuth(String message, boolean isSuccess);
    void onMediaButtonClick();
}
