package com.bstar.powerdata.dialogs;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.models.CountryCallingCode;
import com.bstar.powerdata.models.UserSettingData;
import com.bstar.powerdata.utils.PhoneUtils;
import com.bstar.powerdata.views.adapters.CountrySpinnerAdapter;

import java.util.List;

/**
 * Created by tconnors on 3/4/16.
 */
public class Dialog {

    public interface OnSetPBookCodeListener {
        void onSetPBookCode(String token);
    }

    public interface OnRetryVerificationListener {
        void onRetryClicked();
    }

    public static AlertDialog createSetTokenDialog(final OnSetPBookCodeListener listener, View.OnClickListener contactListener, Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.dialog_set_token, null);

        final EditText mTokenEditText = (EditText) dialogView.findViewById(R.id.edittext_set_token);

        final Spinner mSpinner = (Spinner) dialogView.findViewById(R.id.spinner_set_token_country);
        List<CountryCallingCode> mCountryCallingCodes = PhoneUtils.getListCountryFromAsset(context);
        CountrySpinnerAdapter mHomeCountryAdapter = new CountrySpinnerAdapter(context, R.layout.country_spinner_list_item, mCountryCallingCodes);
        mSpinner.setAdapter(mHomeCountryAdapter);

        TextView mSetTextView = (TextView) dialogView.findViewById(R.id.textview_set_token);
        mSetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSetPBookCode(String.valueOf(mTokenEditText.getText()));
            }
        });

        TextView mContactTextView = (TextView) dialogView.findViewById(R.id.textview_contact_server);
        mContactTextView.setOnClickListener(contactListener);

        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    public static AlertDialog createSettingAnnouncementDialog(Context context, UserSettingData userSettingData) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(PowerDataApplication.getStringResource(R.string.setting));
        alertDialogBuilder.setNegativeButton(PowerDataApplication.getStringResource(R.string.close), null);
        alertDialogBuilder.setMessage(PowerDataApplication.getStringResource(R.string.setting_announcement, userSettingData.getForwardNo(), userSettingData.getForwardNo(), userSettingData.getNewNumber(), userSettingData.getCost(), userSettingData.getExpiredDate()));
        alertDialogBuilder.setCancelable(true);
        return alertDialogBuilder.create();
    }

    public static AlertDialog createWelcomeHomeDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(PowerDataApplication.getStringResource(R.string.welcome_home));
        alertDialogBuilder.setNegativeButton(PowerDataApplication.getStringResource(R.string.close), null);
        alertDialogBuilder.setMessage(PowerDataApplication.getStringResource(R.string.welcome_home_announcement));
        alertDialogBuilder.setCancelable(true);
        return alertDialogBuilder.create();
    }

    public static AlertDialog createVerifyCodeDialog(Context context, String phoneNumber, String verificationCode, final OnRetryVerificationListener onRetryVerificationListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(PowerDataApplication.getStringResource(R.string.verify));
        alertDialogBuilder.setNegativeButton(PowerDataApplication.getStringResource(R.string.done), null);
        alertDialogBuilder.setPositiveButton(PowerDataApplication.getStringResource(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onRetryVerificationListener.onRetryClicked();
            }
        });
        alertDialogBuilder.setMessage(PowerDataApplication.getStringResource(R.string.verification_phone_number_announcement, phoneNumber, verificationCode));
        alertDialogBuilder.setCancelable(true);
        return alertDialogBuilder.create();
    }

    public static AlertDialog createIncomingCallDialog(View.OnClickListener answerCallClickListener, View.OnClickListener cancelClickListener, String caller, Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.dialog_incoming_call, null);

        TextView mCallerNameTextView = (TextView) dialogView.findViewById(R.id.textview_incoming_caller_name);
        TextView mCallerNumberTextView = (TextView) dialogView.findViewById(R.id.textview_incoming_caller_number);
        String callerName = PhoneUtils.getContactName(context, caller);
        mCallerNumberTextView.setText(caller);
        mCallerNameTextView.setText(callerName);

        ImageView mRejectImageView= (ImageView) dialogView.findViewById(R.id.imageview_incoming_reject);
        mRejectImageView.setOnClickListener(cancelClickListener);

        ImageView mAnswerImageView = (ImageView) dialogView.findViewById(R.id.imageview_incoming_answer);
        mAnswerImageView.setOnClickListener(answerCallClickListener);

        alertDialogBuilder.setView(dialogView);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        return dialog;
    }
}
