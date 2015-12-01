package com.atease.at_ease;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseUser;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

public class MessageService extends Service implements SinchClientListener {

    private static final String APP_KEY = "e68c44c6-9472-492a-8902-017c5f00294b";
    private static final String APP_SECRET = "4HmaV0RrykCBhNBrNUhHEA==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private final MessageServiceInterface serviceInterface = new MessageServiceInterface();
    private SinchClient sinchClient = null;
    private MessageClient messageClient = null;
    private String currentUserId;
    private int count = 0;
    private LocalBroadcastManager broadcaster;
    private BroadcastReceiver receiver;
    private Intent broadcastIntent = new Intent("com.atease.at_ease.ListUsersActivity");

    final String TAG = "MessageService";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentUserId = ParseUser.getCurrentUser().getObjectId();

        if (currentUserId != null && !isSinchClientStarted()) {
            Log.d(TAG, currentUserId + " is logged in! " + Integer.toString(count) );
            count += 1;
            startSinchClient(currentUserId);
        }

        broadcaster = LocalBroadcastManager.getInstance(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Boolean success = intent.getBooleanExtra("success", false);
                Log.d(TAG,"OnRecieve message service");
                Intent broadIntent = new Intent("com.atease.at_ease.ListUsersActivity");
                broadIntent.putExtra("success", isSinchClientStarted());
                broadcaster.sendBroadcast(broadIntent);
            }
        };

        broadcaster.registerReceiver(receiver, new IntentFilter("com.atease.at_ease.MessageService"));

        return super.onStartCommand(intent, flags, startId);
    }

    public void startSinchClient(String username) {
        sinchClient = Sinch.getSinchClientBuilder().context(this.getApplicationContext()).userId(username).applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET).environmentHost(ENVIRONMENT).build();

        sinchClient.addSinchClientListener(this);

        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);

        sinchClient.checkManifest();
        sinchClient.start();
        Log.d(TAG, "started sinch client");
    }

    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

    @Override
    public void onClientFailed(SinchClient client, SinchError error) {
        Log.d(TAG, "on Client Failed");
        broadcastIntent.putExtra("success", false);
        broadcaster.sendBroadcast(broadcastIntent);

        sinchClient = null;
    }

    @Override
    public void onClientStarted(SinchClient client) {


        broadcastIntent.putExtra("success", true);
        broadcaster.sendBroadcast(broadcastIntent);
        //Log.d(TAG, broadcaster.toString() + "doesn't look null to me?");
        client.startListeningOnActiveConnection();
        messageClient = client.getMessageClient();
        Log.d(TAG,"client started successfully");
    }

    @Override
    public void onClientStopped(SinchClient client) {
        sinchClient = null;
        Log.d(TAG,"sinch client stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceInterface;
    }

    @Override
    public void onLogMessage(int level, String area, String message) {
        Log.d(TAG, "Log message " + message);
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration clientRegistration) {
        Log.d(TAG, "client registration?");
    }

    public void sendMessage(String recipientUserId, String textBody) {
        if (messageClient != null) {
            Log.d(TAG, "client isn't null");
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            messageClient.send(message);
        }
        else{
            Log.d(TAG, "client is null, no message being sent");
        }
    }

    public void addMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.removeMessageClientListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminateGracefully();
        broadcaster.unregisterReceiver(receiver);
        Log.d("MESSAGING SERVICE", "On Destroy");

    }

    public class MessageServiceInterface extends Binder {
        public void sendMessage(String recipientUserId, String textBody) {
            MessageService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            MessageService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            MessageService.this.removeMessageClientListener(listener);
        }

        public boolean isSinchClientStarted() {
            return MessageService.this.isSinchClientStarted();
        }
    }
}

