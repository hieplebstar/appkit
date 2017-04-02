package com.bstar.powerdata.activities.voice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.base.CalligraphyFontActivity;
import com.bstar.powerdata.models.Contact;
import com.bstar.powerdata.persistances.ApplicationPreferences;
import com.bstar.powerdata.utils.PhoneUtils;
import com.bstar.powerdata.utils.StringUtils;
import com.bstar.powerdata.views.ui.CustomDialPad;
import com.twilio.voice.CallException;
import com.twilio.voice.IncomingCall;
import com.twilio.voice.IncomingCallMessage;
import com.twilio.voice.IncomingCallMessageListener;
import com.twilio.voice.OutgoingCall;
import com.twilio.voice.VoiceClient;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class VoiceActivity extends CalligraphyFontActivity implements VoiceView, SensorEventListener, CustomDialPad.OnDialPadButtonClickListener {


    public static final String EXTRA_IS_OUTGOING = "extra_is_outgoing";
    public static final String EXTRA_OUTGOING_CONTACT = "extra_outgoing_contact";
    public static final String INCOMING_CALL_MESSAGE = "INCOMING_CALL_MESSAGE";

    @BindView(R.id.imageview_voice_speakerphone)
    ImageView mSpeakerImageView;
    @BindView(R.id.imageview_voice_sound)
    ImageView mSoundImageView;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.voice_connect_text_view)
    TextView mConnectTextView;
    @BindView(R.id.voice_avatar_text_view)
    TextView mAvatarTextView;
    @BindView(R.id.voice_name_text_view)
    TextView mNameTextView;
    @BindView(R.id.voice_number_text_view)
    TextView mNumberTextView;
    @BindView(R.id.custom_dial_pad_voice)
    CustomDialPad mCustomDialPad;

    private boolean speakerPhone;
    private AudioManager audioManager;
    private int savedAudioMode = AudioManager.MODE_INVALID;
    private ApplicationPreferences mApplicationPreferences;
    private Unbinder mUnbinder;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private VoicePresenter mPresenter;

    HashMap<String, String> twiMLParams = new HashMap<>();

    private OutgoingCall activeOutgoingCall;
    private IncomingCall activeIncomingCall;

    OutgoingCall.Listener outgoingCallListener = outgoingCallListener();
    IncomingCall.Listener incomingCallListener = incomingCallListener();
    IncomingCallMessageListener incomingCallMessageListener = incomingCallMessageListener();

    @Override
    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        mUnbinder = ButterKnife.bind(this);
        /*
         * Needed for setting/abandoning audio focus during a call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        mApplicationPreferences = new ApplicationPreferences();
        /*
         *  Needed for wake lock during a call
         */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mPowerManager = ((PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE));

        Bundle bundle = getIntent().getExtras();
        boolean isOutGoing = bundle.getBoolean(EXTRA_IS_OUTGOING);
        if(isOutGoing){
            connect((Contact)bundle.getSerializable(EXTRA_OUTGOING_CONTACT));
        } else {
            IncomingCallMessage incomingCallMessage = bundle.getParcelable(INCOMING_CALL_MESSAGE);
            handleIncomingCall(incomingCallMessage);
        }

        mCustomDialPad.setOnDialPadButtonClickListener(this);
        mPresenter = new VoicePresenter();
        mPresenter.start(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        PowerDataApplication.onVoiceActivityResumed();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PowerDataApplication.onVoiceActivityPaused();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chronometer.stop();
        mUnbinder.unbind();
        mPresenter.stop();
    }

    private void handleIncomingCall(IncomingCallMessage incomingCallMessage) {
        VoiceClient.handleIncomingCallMessage(getApplicationContext(), incomingCallMessage, incomingCallMessageListener);
        mNumberTextView.setVisibility(View.GONE);
        mAvatarTextView.setText(StringUtils.getAvatarAltFromUserName(incomingCallMessage.getFrom()));
        mNameTextView.setText(incomingCallMessage.getFrom());
    }

    private IncomingCallMessageListener incomingCallMessageListener() {
        return new IncomingCallMessageListener() {
            @Override
            public void onIncomingCall(IncomingCall incomingCall) {
                activeIncomingCall = incomingCall;
                answer();
            }

            @Override
            public void onIncomingCallCancelled(IncomingCall incomingCall) {
                finishActivity(false);
            }

        };
    }

    private OutgoingCall.Listener outgoingCallListener() {
        return new OutgoingCall.Listener() {
            @Override
            public void onConnected(OutgoingCall outgoingCall) {
                if(!PowerDataApplication.isVoiceActivityVisible()){
                    disconnect();
                    return;
                }
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                mConnectTextView.setVisibility(View.GONE);
                chronometer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDisconnected(OutgoingCall outgoingCall) {
                finishActivity(false);
            }

            @Override
            public void onDisconnected(OutgoingCall outgoingCall, CallException error) {
                finishActivity(false);
            }
        };
    }

    private IncomingCall.Listener incomingCallListener() {
        return new IncomingCall.Listener() {
            @Override
            public void onConnected(IncomingCall incomingCall) {
                if(!PowerDataApplication.isVoiceActivityVisible()){
                    disconnect();
                    return;
                }
                mConnectTextView.setVisibility(View.GONE);
                chronometer.setVisibility(View.VISIBLE);
                try {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                } catch (Exception e) {}
            }

            @Override
            public void onDisconnected(IncomingCall incomingCall) {
                finishActivity(false);
            }

            @Override
            public void onDisconnected(IncomingCall incomingCall, CallException error) {
                finishActivity(false);
            }
        };
    }

    private void connect(Contact contact) {
        if(TextUtils.isEmpty(contact.getUserName())){
            contact.setUserName(PhoneUtils.getContactName(this,contact.getPhoneNumber() ));
        }
        mAvatarTextView.setText(StringUtils.getAvatarAltFromUserName(contact.getUserName()));
        mNameTextView.setText(contact.getUserName());
        mNumberTextView.setText(contact.getPhoneNumber());
        twiMLParams.put("To", contact.getPhoneNumber());
        twiMLParams.put("Called", contact.getPhoneNumber());
        twiMLParams.put("From", mApplicationPreferences.getPBookAuth().getClientName());
        activeOutgoingCall = VoiceClient.call(getApplicationContext(), mApplicationPreferences.getTwilioToken(), twiMLParams, outgoingCallListener);
    }


    @OnClick(R.id.imageview_voice_hangup)
    void onHangupButtonClick() {
        disconnect();
        finishActivity(true);
    }

    @OnClick(R.id.imageview_voice_speakerphone)
    void onSpeakerPhoneButtonClick() {
        toggleSpeakerPhone();
    }

    @OnClick(R.id.imageview_voice_sound)
    void onSoundButtonClick() {
        AudioManager audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
        if(audioManager.getRingerMode() == audioManager.RINGER_MODE_NORMAL){
            audioManager.setRingerMode(audioManager.RINGER_MODE_SILENT);
            mSoundImageView.setImageDrawable(ContextCompat.getDrawable(VoiceActivity.this, R.drawable.ic_mute));
        } else {
            audioManager.setRingerMode(audioManager.RINGER_MODE_NORMAL);
            mSoundImageView.setImageDrawable(ContextCompat.getDrawable(VoiceActivity.this, R.drawable.ic_sound));
        }
    }

    /*
     * Accept an incoming Call
     */
    private void answer() {
        activeIncomingCall.accept(incomingCallListener);
    }

    /*
     * Disconnect an active Call
     */
    private void disconnect() {
        if (activeOutgoingCall != null) {
            activeOutgoingCall.disconnect();
            activeOutgoingCall = null;
        } else if (activeIncomingCall != null) {
            activeIncomingCall.reject();
            activeIncomingCall = null;
        }
    }

    private void toggleSpeakerPhone() {
        speakerPhone = !speakerPhone;

        setAudioFocus(speakerPhone);
        audioManager.setSpeakerphoneOn(speakerPhone);

        if(speakerPhone) {
            mSpeakerImageView.setImageDrawable(ContextCompat.getDrawable(VoiceActivity.this, R.drawable.ic_mic));
        } else {
            mSpeakerImageView.setImageDrawable(ContextCompat.getDrawable(VoiceActivity.this, R.drawable.ic_no_mic));
        }
    }

    private void setAudioFocus(boolean setFocus) {
        if (audioManager != null) {
            if (setFocus) {
                savedAudioMode = audioManager.getMode();
                // Request audio focus before making any device switch.
                audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                /*
                 * Start by setting MODE_IN_COMMUNICATION as default audio mode. It is
                 * required to be in this mode when playout and/or recording starts for
                 * best possible VoIP performance. Some devices have difficulties with speaker mode
                 * if this is not set.
                 */
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(savedAudioMode);
                audioManager.abandonAudioFocus(null);
            }
        }
    }

    private void finishActivity(boolean isSuccess) {
        if(isSuccess){
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values[0] == 0) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
            mWakeLock.acquire();
        } else {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "TAG");
            mWakeLock.acquire();
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onDialPadButtonClick(String value) {
        if(activeOutgoingCall != null) {
            activeOutgoingCall.sendDigits(value);
        }
        if(activeIncomingCall != null) {
            activeIncomingCall.sendDigits(value);
        }
    }

    @Override
    public void onMediaButtonClick() {
        disconnect();
        finishActivity(true);
    }
}
