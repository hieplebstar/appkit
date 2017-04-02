package com.bstar.powerdata.views.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;


public class CustomDialPad extends RelativeLayout implements View.OnClickListener{

    private OnDialPadButtonClickListener mListener;

    public interface OnDialPadButtonClickListener {
        void onDialPadButtonClick(String value);
    }

    public CustomDialPad(Context context) {
        super(context);
    }

    public CustomDialPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomDialPad(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.dial_pad, this);
        findViewById(R.id.button_dial_pad_0).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_1).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_2).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_3).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_4).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_5).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_6).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_7).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_8).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_9).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_star).setOnClickListener(this);
        findViewById(R.id.button_dial_pad_pound).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(mListener == null) return;
        switch (view.getId()){
            case R.id.button_dial_pad_0: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_0));
                break;
            case R.id.button_dial_pad_1: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_1));
                break;
            case R.id.button_dial_pad_2: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_2));
                break;
            case R.id.button_dial_pad_3: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_3));
                break;
            case R.id.button_dial_pad_4: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_4));
                break;
            case R.id.button_dial_pad_5: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_5));
                break;
            case R.id.button_dial_pad_6: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_6));
                break;
            case R.id.button_dial_pad_7: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_7));
                break;
            case R.id.button_dial_pad_8: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_8));
                break;
            case R.id.button_dial_pad_9: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.number_9));
                break;
            case R.id.button_dial_pad_star: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.star));
                break;
            case R.id.button_dial_pad_pound: mListener.onDialPadButtonClick(PowerDataApplication.getStringResource(R.string.pound));
        }
    }

    public void setOnDialPadButtonClickListener(OnDialPadButtonClickListener listener){
        mListener = listener;
    }
}
