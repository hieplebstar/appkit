package com.bstar.powerdata.activities.main;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatDrawableManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.activities.voice.VoiceActivity;
import com.bstar.powerdata.base.CalligraphyFontActivity;
import com.bstar.powerdata.dialogs.Dialog;
import com.bstar.powerdata.fragments.call.CallFragment;
import com.bstar.powerdata.fragments.dial.ShowDialEvent;
import com.bstar.powerdata.fragments.help.HelpFragment;
import com.bstar.powerdata.fragments.log.LogFragment;
import com.bstar.powerdata.fragments.setting.SettingFragment;
import com.bstar.powerdata.models.Contact;
import com.bstar.powerdata.receivers.RemoteControlReceiver;
import com.bstar.powerdata.services.gcm.GCMRegistrationService;
import com.bstar.powerdata.views.adapters.TabBarAdapter;
import com.example.shadow.appkit.activity.BaseFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.twilio.voice.IncomingCall;
import com.twilio.voice.IncomingCallMessage;
import com.twilio.voice.IncomingCallMessageListener;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.UnregistrationListener;
import com.twilio.voice.VoiceClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends BaseFragmentActivity implements MainView, Dialog.OnSetPBookCodeListener {

    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
    private static final int VOICE_CALL_CODE = 0;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    @BindView(R.id.tablayout_main)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager_main)
    ViewPager mViewPager;

    private MainPresenter mPresenter;
    private boolean isReceiverRegistered;
    private VoiceBroadcastReceiver voiceBroadcastReceiver;
    private RelativeLayout mContainerLayout;
    private Unbinder mUnbinder;
    private MediaPlayer  mRingtone ;


    public static final String ACTION_SET_GCM_TOKEN = "SET_GCM_TOKEN";
    public static final String INCOMING_CALL_MESSAGE = "INCOMING_CALL_MESSAGE";
    public static final String INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID";
    public static final String ACTION_INCOMING_CALL = "INCOMING_CALL";

    public static final String KEY_GCM_TOKEN = "GCM_TOKEN";

    private TabBarAdapter mAdapter;
    final static int CALL = 0;
    final static int LOG = 1;
    final static int SETTING = 2;
    final static int HELP = 3;

    private NotificationManager notificationManager;
    private int mNotificationId;
    private IncomingCallMessage mIncomingCallMessage;
    private String gcmToken;
    private String accessToken;
    private AlertDialog alertDialog;
    private boolean isBusy = false;
    private boolean isActivated = false;
    private boolean isInit;
    AudioManager mAudioManager;
    ComponentName mReceiverComponent;

    RegistrationListener registrationListener = registrationListener();
    UnregistrationListener unregistrationListener = unregistrationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        setContentView(R.layout.activity_main);
//        mUnbinder = ButterKnife.bind(this);
//        mPresenter = new MainPresenter();
//        mPresenter.start(this);
//        mContainerLayout = (RelativeLayout) findViewById(R.id.container_main);
//
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        /*
//         * Setup the broadcast receiver to be notified of GCM Token updates
//         * or incoming call messages in this Activity.
//         */
//        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
//        registerReceiver();
//
//        /*
//         * Displays a call dialog if the intent contains an incoming call message
//         */
//        handleIncomingCallIntent(getIntent());
//
//        /*
//         * Ensure the microphone permission is enabled
//         */
//        if (!checkPermissionForMicrophone()) {
//            requestPermissionForMicrophone();
//        } else {
//            startGCMRegistration();
//        }
//        initTab();
//        mPresenter.getCurrentPBookAuth();
//
//        mAudioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        mReceiverComponent = new ComponentName(this,RemoteControlReceiver.class);
//        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);
        PowerDataApplication.getEvenBusController().postEvent(new ShowDialEvent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingCallIntent(intent);
    }

    private void startGCMRegistration() {
        if (checkPlayServices()) {
            Intent intent = new Intent(this, GCMRegistrationService.class);
            startService(intent);
        }
    }

    private RegistrationListener registrationListener() {
        return new RegistrationListener() {
            @Override
            public void onRegistered(String accessToken, String gcmToken) {
                showMessage(PowerDataApplication.getStringResource(R.string.ready_to_call));
            }

            @Override
            public void onError(RegistrationException error, String accessToken, String gcmToken) {
                showErrorMessage(PowerDataApplication.getStringResource(R.string.register_push_notification_failed));
            }
        };
    }

    private UnregistrationListener unregistrationListener() {
        return new UnregistrationListener() {
            @Override
            public void onUnregistered(String accessToken, String gcmToken) {
            }

            @Override
            public void onError(RegistrationException error, String accessToken, String gcmToken) {
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        PowerDataApplication.onMainActivityResumed();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected int getContainerId(Class<? extends Fragment> fragmentClass) {
        return R.id.container_main;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.stop();
        mUnbinder.unbind();
        PowerDataApplication.onMainActivityDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver);
        isReceiverRegistered = false;
        mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != VOICE_CALL_CODE) return;
        isBusy = false;
    }

    private void handleIncomingCallIntent(Intent intent) {
        if (isBusy) return;
        if (intent != null && intent.getAction() != null && intent.getAction() == MainActivity.ACTION_INCOMING_CALL) {
            mNotificationId = intent.getIntExtra(MainActivity.INCOMING_CALL_NOTIFICATION_ID, 0);
            if(isBusy) return;
            if(!mPresenter.isCurrentPBookAuthValid()) return;
            mIncomingCallMessage = intent.getParcelableExtra(INCOMING_CALL_MESSAGE);
            if(mIncomingCallMessage.isCancelled()){
                cancelIncomingCall();
            } else {
                showIncomingDialog();
            }
        }
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_SET_GCM_TOKEN);
            intentFilter.addAction(ACTION_INCOMING_CALL);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    voiceBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    @Override
    public void onOutGoingCall(Contact contact) {
        if (!isActivated) {
            showSetTokenDialog();
            return;
        }
        if(!mPresenter.isCurrentPBookAuthValid()) return;
        Intent voidIntent = new Intent(MainActivity.this, VoiceActivity.class);
        voidIntent.putExtra(VoiceActivity.EXTRA_IS_OUTGOING, true);
        voidIntent.putExtra(VoiceActivity.EXTRA_OUTGOING_CONTACT, contact);
        startActivityForResult(voidIntent, VOICE_CALL_CODE);
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        isBusy = true;
    }

    @Override
    public void onReturnAccessToken(String accessToken, boolean isSuccess) {
        if (isSuccess) {
            if(!TextUtils.isEmpty(gcmToken) && !TextUtils.isEmpty(MainActivity.this.accessToken)){
                VoiceClient.unregister(getApplicationContext(), MainActivity.this.accessToken, gcmToken, unregistrationListener);
            }
            MainActivity.this.accessToken = accessToken;
            isActivated = true;
            if (TextUtils.isEmpty(gcmToken)) return;
            register();
        } else {
            isActivated = false;
            showErrorMessage(PowerDataApplication.getStringResource(R.string.retrieving_access_token_failed));
        }
    }

    @Override
    public void onReturnPBookAuth(String message, boolean isSuccess) {
        if(!isNetworkAvailable()){
            showErrorMessage(PowerDataApplication.getStringResource(R.string.no_network));
            showSetTokenDialog();
            return;
        }
        if(!TextUtils.isEmpty(message)){
            showErrorMessage(message);
        }
        if(isSuccess){
            mPresenter.retrieveAccessToken(getApplicationContext());
        } else {
            showSetTokenDialog();
        }
    }

    @Override
    public void onMediaButtonClick() {
        if(isBusy) return;
        if(mIncomingCallMessage == null) return;
        cancelIncomingCall();
        Intent voidIntent = new Intent(MainActivity.this, VoiceActivity.class);
        voidIntent.putExtra(VoiceActivity.EXTRA_IS_OUTGOING, false);
        voidIntent.putExtra(VoiceActivity.INCOMING_CALL_MESSAGE, mIncomingCallMessage);
        startActivityForResult(voidIntent, VOICE_CALL_CODE);
        isBusy = true;
    }

    @Override
    public void onSetPBookCode(String token) {
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        mPresenter.checkNewPBookToken(getApplicationContext(), token);
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_SET_GCM_TOKEN)) {
                String gcmToken = intent.getStringExtra(KEY_GCM_TOKEN);
                MainActivity.this.gcmToken = gcmToken;
                if (gcmToken == null) {
                    showErrorMessage(PowerDataApplication.getStringResource(R.string.retrieving_gcm_token_failed));
                }
            } else if (action.equals(ACTION_INCOMING_CALL)) {
                mNotificationId = intent.getIntExtra(MainActivity.INCOMING_CALL_NOTIFICATION_ID, 0);
                if(isBusy) return;
                if(!mPresenter.isCurrentPBookAuthValid()) return;
                mIncomingCallMessage = intent.getParcelableExtra(INCOMING_CALL_MESSAGE);
                if(mIncomingCallMessage.isCancelled()){
                    cancelIncomingCall();
                } else {
                    showIncomingDialog();
                }
            }
        }
    }

    private View.OnClickListener contactClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{PowerDataApplication.getStringResource(R.string.contact_email)});
                intent.putExtra(Intent.EXTRA_SUBJECT, PowerDataApplication.getStringResource(R.string.contact_mail_subject));
                intent.putExtra(Intent.EXTRA_TEXT, PowerDataApplication.getStringResource(R.string.pbook_auth_report));
                startActivity(Intent.createChooser(intent, PowerDataApplication.getStringResource(R.string.send_email)));
            }
        };
    }

    private View.OnClickListener answerCallClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                cancelIncomingCall();
                if(mIncomingCallMessage == null) return;
                Intent voidIntent = new Intent(MainActivity.this, VoiceActivity.class);
                voidIntent.putExtra(VoiceActivity.EXTRA_IS_OUTGOING, false);
                voidIntent.putExtra(VoiceActivity.INCOMING_CALL_MESSAGE, mIncomingCallMessage);
                startActivityForResult(voidIntent, VOICE_CALL_CODE);
            }
        };
    }

    private View.OnClickListener cancelCallClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                cancelIncomingCall();
                if(mIncomingCallMessage == null) return;
                VoiceClient.handleIncomingCallMessage(getApplicationContext(), mIncomingCallMessage, new IncomingCallMessageListener() {
                    @Override
                    public void onIncomingCall(IncomingCall incomingCall) {
                        incomingCall.reject();
                    }

                    @Override
                    public void onIncomingCallCancelled(IncomingCall incomingCall) {
                    }
                });
            }
        };
    }

    private void cancelIncomingCall() {
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if(mRingtone != null) {
            mRingtone.stop();
        }
        /*
         * Remove the notification from the notification drawer
         */
        if(mNotificationId == 0) return;
        notificationManager.cancel(mNotificationId);
    }

    private void showIncomingDialog() {
        mPresenter.retrieveAccessToken(this);
        alertDialog = Dialog.createIncomingCallDialog(answerCallClickListener(), cancelCallClickListener(), mIncomingCallMessage.getFrom(), MainActivity.this);
        alertDialog.show();
        if(mRingtone != null && mRingtone.isPlaying()) return;
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mRingtone = MediaPlayer.create(getApplicationContext(), notification);
        if(mRingtone == null) return;
        mRingtone.start();
    }

    private void showSetTokenDialog() {
        alertDialog = Dialog.createSetTokenDialog(MainActivity.this, contactClickListener(),  MainActivity.this);
        alertDialog.show();
    }

    /*
     * Register your GCM token with Twilio to enable receiving incoming calls via GCM
     */
    private void register() {
        VoiceClient.register(getApplicationContext(), accessToken, gcmToken, registrationListener);
    }

    private boolean checkPermissionForMicrophone() {
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (resultMic == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            showMessage(PowerDataApplication.getStringResource(R.string.request_microphone_permissions));
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE},
                    MIC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar snackbar = Snackbar.make(mContainerLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundResource(R.color.Design_green);
        snackbar.show();
    }

    public void showErrorMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
         * Check if microphone permissions is granted
         */
        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            boolean granted = true;
            if (granted) {
                startGCMRegistration();
            } else {
                showErrorMessage(PowerDataApplication.getStringResource(R.string.request_microphone_permissions));
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void initTab() {
        mAdapter = new TabBarAdapter(getSupportFragmentManager());
        mAdapter.addFragment(CallFragment.newInstance());
        mAdapter.addFragment(LogFragment.newInstance());
        mAdapter.addFragment(SettingFragment.newInstance());
        mAdapter.addFragment(HelpFragment.newInstance());
        mAdapter.addTitle(R.array.bottom_tab_arrays);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterIconColor(tab.getIcon(), R.color.Design_gold);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                filterIconColor(tab.getIcon(), R.color.Design_darkWhite);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                filterIconColor(tab.getIcon(), R.color.Design_gold);
            }
        });
        updateTabSelectedItem(SETTING);
    }

    private void updateTabSelectedItem(final int tabIndex) {
        if (isInit) return;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TabLayout.Tab currentTab = mTabLayout.getTabAt(tabIndex);
                if (currentTab != null) {
                    View customView = currentTab.getCustomView();
                    if (customView != null) {
                        customView.setSelected(true);
                    }
                    currentTab.select();
                    isInit = true;
                }
            }
        }, 100);
    }

    private void setupTabIcons() {
        mTabLayout.getTabAt(CALL).setIcon(filterIconColor(R.drawable.ic_call));
        mTabLayout.getTabAt(LOG).setIcon(filterIconColor(R.drawable.ic_call_log));
        mTabLayout.getTabAt(SETTING).setIcon(filterIconColor(R.drawable.ic_setting));
        mTabLayout.getTabAt(HELP).setIcon(filterIconColor(R.drawable.ic_help));
    }

    private Drawable filterIconColor(@DrawableRes int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get()
                .getDrawable(PowerDataApplication.getInstance(), drawableId);
        return filterIconColor(drawable, R.color.Design_darkWhite);
    }

    private Drawable filterIconColor(Drawable drawable, @ColorRes int resColorId) {
        drawable.setColorFilter(PowerDataApplication.getIntColor(resColorId), PorterDuff.Mode.SRC_IN);
        return drawable;
    }
}
