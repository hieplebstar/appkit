package com.bstar.powerdata.services.gcm;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.activities.main.MainActivity;
import com.bstar.powerdata.events.IncomingCallCanceledEvent;
import com.google.android.gms.gcm.GcmListenerService;
import com.twilio.voice.IncomingCallMessage;

import org.greenrobot.eventbus.EventBus;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class VoiceGCMListenerService extends GcmListenerService {

    private static final String TAG = "VoiceGCMListenerService";

    /*
     * Notification related keys
     */
    private static final int MISSING_CALL_NOTIFICATION_ID = 0;
    private static final String NOTIFICATION_ID_KEY = "NOTIFICATION_ID";
    private static final String CALL_SID_KEY = "CALL_SID";

    private NotificationManager notificationManager;
    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        Log.d(TAG, "onMessageReceived " + from);

        if (IncomingCallMessage.isValidMessage(bundle)) {
            /*
             * Generate a unique notification id using the system time
             */
            int notificationId = (int) System.currentTimeMillis();

            /*
             * Create an IncomingCallMessage from the bundle
             */
            IncomingCallMessage incomingCallMessage = new IncomingCallMessage(bundle);

            showNotification(incomingCallMessage, notificationId);
            sendIncomingCallMessageToActivity(incomingCallMessage, notificationId);
        }

    }

    /*
     * Show the notification in the Android notification drawer
     */
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private void showNotification(IncomingCallMessage incomingCallMessage, int notificationId) {
        String callSid = incomingCallMessage.getCallSid();

        if (!incomingCallMessage.isCancelled()) {
            /*
             * Pass the notification id and call sid to use as an identifier to cancel the
             * notification later
             */
            Bundle extras = new Bundle();
            extras.putInt(NOTIFICATION_ID_KEY, notificationId);
            extras.putString(CALL_SID_KEY, callSid);

            /*
              * Create the notification shown in the notification drawer
             */
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_call_white_24px)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(incomingCallMessage.getFrom() + " is calling.")
                            .setExtras(extras)
                            .setColor(PowerDataApplication.getIntColor(R.color.Design_green));
            notificationManager.notify(notificationId, notificationBuilder.build());
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                /*
                 * If the incoming call was cancelled then remove the notification by matching
                 * it with the call sid from the list of notifications in the notification drawer.
                 */
                StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
                for (StatusBarNotification statusBarNotification : activeNotifications) {
                    Notification notification = statusBarNotification.getNotification();
                    Bundle extras = notification.extras;
                    String notificationCallSid = extras.getString(CALL_SID_KEY);
                    if (callSid.equals(notificationCallSid)) {
                        notificationManager.cancel(extras.getInt(NOTIFICATION_ID_KEY));
                    }
                }
            } else {
                /*
                 * Prior to Android M the notification manager did not provide a list of
                 * active notifications so we lazily clear all the notifications when
                 * receiving a cancelled call.
                 *
                 * In order to properly cancel a notification using
                 * NotificationManager.cancel(notificationId) we should store the call sid &
                 * notification id of any incoming calls using shared preferences or some other form
                 * of persistent storage.
                 */
                notificationManager.cancelAll();
            }
            if (PowerDataApplication.isVoiceActivityVisible()) return;
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_call_white_24px)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText("Missing call from " + incomingCallMessage.getFrom())
                            .setAutoCancel(true)
                            .setColor(PowerDataApplication.getIntColor(R.color.Design_red));

            notificationManager.notify(MISSING_CALL_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    /*
     * Send the IncomingCallMessage to the MainActivity
     */
    private void sendIncomingCallMessageToActivity(IncomingCallMessage incomingCallMessage, int notificationId) {
        mWakeLock = ((PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        mWakeLock.release();

        KeyguardManager.KeyguardLock k1;
        KeyguardManager km =(KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        k1= km.newKeyguardLock("IN");
        k1.disableKeyguard();

        if (!PowerDataApplication.isMainActivityExist() && !PowerDataApplication.isVoiceActivityVisible() && !incomingCallMessage.isCancelled()) {
            Intent showActivityIntent = new Intent(this, MainActivity.class);
            showActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            showActivityIntent.setAction(MainActivity.ACTION_INCOMING_CALL);
            showActivityIntent.putExtra(MainActivity.INCOMING_CALL_MESSAGE, incomingCallMessage);
            showActivityIntent.putExtra(MainActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId);
            showActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(showActivityIntent);
        } else {
            Intent intent = new Intent(MainActivity.ACTION_INCOMING_CALL);
            intent.putExtra(MainActivity.INCOMING_CALL_MESSAGE, incomingCallMessage);
            intent.putExtra(MainActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

}
