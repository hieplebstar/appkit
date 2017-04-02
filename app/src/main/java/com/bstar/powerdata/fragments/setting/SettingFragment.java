package com.bstar.powerdata.fragments.setting;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.base.ButterKnifeEventFragment;
import com.bstar.powerdata.dialogs.Dialog;
import com.bstar.powerdata.models.CountryCallingCode;
import com.bstar.powerdata.models.IpLocation;
import com.bstar.powerdata.models.PBookAuth;
import com.bstar.powerdata.models.UserSettingData;
import com.bstar.powerdata.persistances.ApplicationPreferences;
import com.bstar.powerdata.utils.PhoneUtils;
import com.bstar.powerdata.utils.StringUtils;
import com.bstar.powerdata.views.adapters.CountrySpinnerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.TELEPHONY_SERVICE;

public class SettingFragment extends ButterKnifeEventFragment implements SettingView, Dialog.OnRetryVerificationListener {

    @BindView(R.id.setting_client_name_text_view)
    TextView mClientNameEditText;
    @BindView(R.id.setting_status_text_view)
    TextView mStatusTextView;
    @BindView(R.id.setting_phone_number_text_view)
    TextView mPhoneNumberTextView;
    @BindView(R.id.setting_start_date_text_view)
    TextView mStartDateTextView;
    @BindView(R.id.setting_end_date_text_view)
    TextView mEndDateTextView;
    @BindView(R.id.setting_country_text_view)
    TextView mCountryTextView;
    @BindView(R.id.setting_build_version_text_view)
    TextView mVersionTextView;
    @BindView(R.id.setting_days_spinner)
    AppCompatSpinner mSelectDaysSpinner;
    @BindView(R.id.setting_home_country_spinner)
    AppCompatSpinner mHomeCountrySpinner;
    @BindView(R.id.setting_destination_country_spinner)
    AppCompatSpinner mDestinationCountrySpinner;
    @BindView(R.id.setting_caller_number_spinner)
    AppCompatSpinner mCallerCountrySpinner;
    @BindView(R.id.setting_auto_forward_switch)
    SwitchCompat mCallForwardSwitch;
    @BindView(R.id.setting_new_caller_number_edittext)
    EditText mCallerNumberEditText;
    @BindView(R.id.setting_verify_new_caller_number)
    TextView mVerificationButton;
    @BindView(R.id.setting_incoming_forward_textview)
    TextView mIncomingForwardTextView;


    private SettingPresenter mPresenter;
    private ApplicationPreferences mPreferences;
    private AlertDialog mAlertDialog;
    private List<CountryCallingCode> mCountryCallingCodes;
    private String[] mSelectDays;
    private PBookAuth mPBookAuth;
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SettingPresenter();
        mPreferences = new ApplicationPreferences();
        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);
        manager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start(this);

        mCountryCallingCodes = PhoneUtils.getListCountryFromAsset(getActivity());
        CountrySpinnerAdapter mHomeCountryAdapter = new CountrySpinnerAdapter(getActivity(), R.layout.country_spinner_list_item, mCountryCallingCodes);
        mHomeCountrySpinner.setAdapter(mHomeCountryAdapter);
        mHomeCountrySpinner.setSelection(mPreferences.getHomeCountryPosition());

        CountrySpinnerAdapter mDestinationCountryAdapter = new CountrySpinnerAdapter(getActivity(), R.layout.country_spinner_list_item, mCountryCallingCodes);
        mDestinationCountrySpinner.setAdapter(mDestinationCountryAdapter);
        mDestinationCountrySpinner.setSelection(mPreferences.getDestinationCountryPosition());

        CountrySpinnerAdapter mCallerNumberAdapter = new CountrySpinnerAdapter(getActivity(), R.layout.country_spinner_list_item, mCountryCallingCodes);
        mCallerCountrySpinner.setAdapter(mCallerNumberAdapter);
        mCallerCountrySpinner.setSelection(mPreferences.getCallerCountryPosition());

        mSelectDays = PowerDataApplication.getStringArrayResource(R.array.setting_select_days);
        ArrayAdapter mSelectDaysAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mSelectDays);
        mSelectDaysSpinner.setAdapter(mSelectDaysAdapter);
        mSelectDaysSpinner.setSelection(mPreferences.getSelectedDaysPosition());

        if(mPreferences.getCallForward()){
            mPresenter.doGetLocation(getActivity());
        }
        mCallForwardSwitch.setChecked(mPreferences.getCallForward());
        mCallerNumberEditText.setText(mPreferences.getCallerPhoneNumber());
    }


    @Override
    public void onResume() {
        super.onResume();
        if(mPreferences == null) return;

        mCallForwardSwitch.setChecked(mPreferences.getCallForward());
        mCallerNumberEditText.setText(mPreferences.getCallerPhoneNumber());

        if(mPreferences.getCallerVerified()){
            setEnableCallerPhoneNumberVerification(false);
            return;
        }
        String phoneNumber =  mCountryCallingCodes.get(mPreferences.getCallerCountryPosition()).getCallingCode() + mPreferences.getCallerPhoneNumber();
        mPresenter.doCheckPhoneNumber(getActivity(), phoneNumber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.stop();
    }

    @Override
    public void onUpdatePBookAuth(PBookAuth pBookAuth) {
        mClientNameEditText.setText(pBookAuth.getClientName());
        mPhoneNumberTextView.setText(pBookAuth.getPhoneNumber());
        mStatusTextView.setText(PowerDataApplication.getStringResource(R.string.activated));
        mStartDateTextView.setText(pBookAuth.getStartDate());
        mEndDateTextView.setText(pBookAuth.getEndDate());
        mCountryTextView.setText(pBookAuth.getCountry());
        mVersionTextView.setText(PowerDataApplication.getBuildVersion());
        mIncomingForwardTextView.setText(PowerDataApplication.getStringResource(R.string.forward_number_message, pBookAuth.getPhoneNumber()));
        mPBookAuth = pBookAuth;
    }

    @Override
    public void onUserDataReturn(UserSettingData userSettingData) {
        if (userSettingData == null) return;
        mAlertDialog = Dialog.createSettingAnnouncementDialog(getActivity(), userSettingData);
        mAlertDialog.show();
    }

    @Override
    public void onGetLocationReturn(IpLocation ipLocation) {
        if(ipLocation == null) return;
        if(mCountryCallingCodes.get(mHomeCountrySpinner.getSelectedItemPosition()).getCountryCode().equals(ipLocation.getCountry())){
            Dialog.createWelcomeHomeDialog(getActivity()).show();
        }
    }

    @Override
    public void onVerifyPhoneNumberReturn(String phoneNumber, String verifyCode) {
        if(!StringUtils.validateVerificationCode(verifyCode)) {
            Toast.makeText(getActivity(), PowerDataApplication.getStringResource(R.string.get_verification_code_error), Toast.LENGTH_LONG).show();
            return;
        }
        Dialog.createVerifyCodeDialog(getActivity(), phoneNumber, verifyCode, this).show();
    }

    @Override
    public void onCheckPhoneNumberReturn(boolean isVerified) {
        setEnableCallerPhoneNumberVerification(!isVerified);
    }

    @OnClick(R.id.setting_set_country)
    public void onSetButtonClick() {
        mPreferences.setDestinationCountry(mDestinationCountrySpinner.getSelectedItemPosition());
        mPreferences.setHomeCountry(mHomeCountrySpinner.getSelectedItemPosition());
        mPreferences.setSelectedDays(mSelectDaysSpinner.getSelectedItemPosition());
        mPresenter.getPhoneNumber(mCountryCallingCodes.get(mDestinationCountrySpinner.getSelectedItemPosition()).getCallingCode(),
                mCountryCallingCodes.get(mHomeCountrySpinner.getSelectedItemPosition()).getCallingCode(),
                mSelectDays[mSelectDaysSpinner.getSelectedItemPosition()]);
    }

    @OnClick(R.id.setting_verify_new_caller_number)
    public void onVerifyButtonClick() {
        if(!StringUtils.validatePhoneNumber(mCallerNumberEditText.getText().toString())) return;
        String phoneNumber =  mCountryCallingCodes.get(mCallerCountrySpinner.getSelectedItemPosition()).getCallingCode() + mCallerNumberEditText.getText().toString();
        mPresenter.doVerifyPhoneNumber(getActivity(), phoneNumber);
        mPreferences.setCallerPhoneNumber(mCallerNumberEditText.getText().toString());
        mPreferences.setCallerCountry(mCallerCountrySpinner.getSelectedItemPosition());
    }

    @OnClick(R.id.setting_auto_forward_switch)
    public void onCallForwardCheckedChanged() {
        String callForwardString;
        if (mCallForwardSwitch.isChecked()) {
            callForwardString = "**21*"+ mPBookAuth.getPhoneNumber() +"#";
        } else {
            callForwardString = "##21#";
        }
        Intent intentCallForward = new Intent(Intent.ACTION_CALL);
        Uri uri2 = Uri.fromParts("tel", callForwardString, "#");
        intentCallForward.setData(uri2);
        startActivity(intentCallForward);
    }

    @Override
    public void onRetryClicked() {
        if(!StringUtils.validatePhoneNumber(mCallerNumberEditText.getText().toString())) return;
        String phoneNumber =  mCountryCallingCodes.get(mCallerCountrySpinner.getSelectedItemPosition()).getCallingCode() + mCallerNumberEditText.getText().toString();
        mPresenter.doVerifyPhoneNumber(getActivity(), phoneNumber);
    }

    public void setEnableCallerPhoneNumberVerification(boolean isEnable) {
        mCallerCountrySpinner.setEnabled(isEnable);
        mCallerNumberEditText.setEnabled(isEnable);
        mVerificationButton.setEnabled(isEnable);
        if(isEnable){
            mVerificationButton.setText(PowerDataApplication.getStringResource(R.string.verify));
            mVerificationButton.setBackground(PowerDataApplication.getDrawableResource(R.drawable.button_blue_rounded_all));
        } else {
            mVerificationButton.setText(PowerDataApplication.getStringResource(R.string.verified));
            mVerificationButton.setBackground(PowerDataApplication.getDrawableResource(R.drawable.button_red_rounded_all));
            mPreferences.setCallerVerified(true);
        }
    }

    class MyPhoneStateListener extends PhoneStateListener{

        @Override
        public void onCallForwardingIndicatorChanged(boolean isActive) {
            super.onCallForwardingIndicatorChanged(isActive);
            mPreferences.setCallForward(isActive);
            if(mCallForwardSwitch == null) return;
            mCallForwardSwitch.setChecked(isActive);
        }

    }
}
