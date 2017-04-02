package com.bstar.powerdata.fragments.call;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.base.ButterKnifeEventFragment;
import com.bstar.powerdata.models.Contact;
import com.bstar.powerdata.models.CountryCallingCode;
import com.bstar.powerdata.persistances.ApplicationPreferences;
import com.bstar.powerdata.utils.PhoneUtils;
import com.bstar.powerdata.views.adapters.ContactListAdapter;
import com.bstar.powerdata.views.adapters.CountrySpinnerAdapter;
import com.bstar.powerdata.views.ui.CustomDialPad;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class CallFragment extends ButterKnifeEventFragment implements CallView, CustomDialPad.OnDialPadButtonClickListener {

    private final static int PICK_CONTACT_CODE = 0;

    @BindView(R.id.textview_call_number)
    TextView mNumberTextView;
    @BindView(R.id.textview_call_available_time)
    TextView mTimeTextView;
    @BindView(R.id.spinner_call_country_name)
    AppCompatSpinner mSpinner;
    @BindView(R.id.call_custom_dial_pad)
    CustomDialPad mCustomDialPad;

    private CallPresenter mPresenter;
    private ApplicationPreferences mPreferences;
    private List<CountryCallingCode> mCountryCallingCodes;

    public static CallFragment newInstance() {
        return new CallFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CallPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.setView(this);
        mPresenter.doGetCallMinutesAvailable(getActivity());
        mPreferences = new ApplicationPreferences();
        mCountryCallingCodes = PhoneUtils.getListCountryFromAsset(getActivity());
        if (mCountryCallingCodes == null) {
            return;
        }
        CountrySpinnerAdapter mAdapter = new CountrySpinnerAdapter(getActivity(), R.layout.country_spinner_list_item, mCountryCallingCodes);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setSelection(mPreferences.getDestinationCountryPosition());
        mCustomDialPad.setOnDialPadButtonClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == PICK_CONTACT_CODE) {
            Contact contact = PhoneUtils.contactPicked(getActivity(), data.getData());
            mPresenter.outGoingCall(contact);
        }
    }

    @Override
    public void onDialPadButtonClick(String value) {
        mNumberTextView.setText(mNumberTextView.getText() + value);
    }

    @OnClick({R.id.button_dial_pad_call, R.id.imageview_call_back, R.id.button_dial_pad_phone_contact, R.id.button_dial_pad_support_call})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.button_dial_pad_call:
                if (TextUtils.isEmpty(mNumberTextView.getText())) break;
                CountryCallingCode countryCallingCode = mCountryCallingCodes.get(mSpinner.getSelectedItemPosition());
                mPresenter.outGoingCall(new Contact(null, countryCallingCode.getCallingCode() + mNumberTextView.getText().toString()));
                break;
            case R.id.imageview_call_back:
                if (TextUtils.isEmpty(mNumberTextView.getText())) break;
                mNumberTextView.setText(mNumberTextView.getText().subSequence(0, mNumberTextView.getText().length() - 1));
                break;
            case R.id.button_dial_pad_phone_contact:
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, PICK_CONTACT_CODE);
                break;
            case R.id.button_dial_pad_support_call:
                break;
        }
    }

    @Override
    public void onReturnCallMinutes(String time) {
        if(TextUtils.isEmpty(time) || mTimeTextView == null) return;
        mTimeTextView.setText(time);
    }
}
