package com.atease.at_ease;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsButton;

public class MainTenantActivity extends AppCompatActivity {

    IconicsButton btnWorkOrder;
    IconicsButton btnPayRent;
    IconicsButton btnPaymentHistory;
    IconicsButton btnMessaging;
    ProgressBar progress;

    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;
    private Intent broadcastIntent = new Intent("com.atease.at_ease.MessageService");
    private LocalBroadcastManager broadcaster;


    final String TAG = "Tenant Main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tenant);



        Log.d(TAG, "OnCreate");
        btnWorkOrder = (IconicsButton) findViewById(R.id.btnWorkOrder);
        btnPayRent = (IconicsButton) findViewById(R.id.btnPayRent);
        btnPaymentHistory = (IconicsButton) findViewById(R.id.btnPaymentHistory);
        btnMessaging = (IconicsButton) findViewById(R.id.btnMessaging);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);
        progress.setVisibility(View.GONE);



        showSpinner();




        btnWorkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainTenantActivity.this, WorkOrderInboxActivity.class);
                startActivity(intent);
            }
        });

        btnPayRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainTenantActivity.this, RentPayActivity.class);
                startActivity(intent);
            }
        });

        btnPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainTenantActivity.this, PaymentHistoryActivity.class);
                startActivity(intent);
            }
        });




    }

    //show a loading spinner while the sinch client starts
    private void showSpinner() {
        progress.setVisibility(View.VISIBLE);
        btnMessaging.setAlpha(0.25f);
        broadcaster = LocalBroadcastManager.getInstance(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                //progressDialog.dismiss();
                bindMessagingButton();
                Log.d(TAG,"OnRecieve!?!?!");
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "message service connected!", Toast.LENGTH_LONG).show();
                }
            }
        };

        broadcaster.registerReceiver(receiver, new IntentFilter("com.atease.at_ease.ListUsersActivity"));
        broadcaster.sendBroadcast(broadcastIntent);

    }

    private void bindMessagingButton(){
        progress.setVisibility(View.GONE);
        btnMessaging.setAlpha(1);
        btnMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainTenantActivity.this, ListUsersActivity.class);
                startActivity(intent);

            }
        });
        Log.d(TAG, "btnMessaging has been set");
    }

    @Override
    public void onDestroy(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        Log.d(TAG, "OnDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
