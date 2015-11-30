package com.atease.at_ease;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsTextView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

public class MainTenantActivity extends AppCompatActivity {

    Toolbar toolbar;
    IconicsButton btnWorkOrder;
    IconicsButton btnPayRent;
    IconicsButton btnPaymentHistory;
    IconicsButton btnMessaging;
    //IconicsButton btnLeave;
    ProgressBar progress;

    private ParseUser currentUser;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;
    private Intent broadcastIntent = new Intent("com.atease.at_ease.MessageService");
    private LocalBroadcastManager broadcaster;


    final String TAG = "Tenant Main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tenant);

        //Drawer Stuff
        currentUser = ParseUser.getCurrentUser();
        if(currentUser == null){
            Intent login = new Intent(MainTenantActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }
        //Log.d("Tenant main",currentUser.getParseObject("liveAt").getObjectId());
        if(currentUser.getParseObject("liveAt") == null){
            //no property,
            Intent addProp = new Intent(MainTenantActivity.this, AddTenantToPropertyActivity.class);
            startActivity(addProp);
            finish();
        }
        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        final Drawer myDrawer = application.getNewDrawerBuilder(currentUser.getBoolean("isManager"),MainTenantActivity.this).withActivity(this).build();

        //Toolbar stuff
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object


        IconicsTextView rightToggle = (IconicsTextView) toolbar.findViewById(R.id.rightToggle);
        rightToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myDrawer.isDrawerOpen()) {
                    myDrawer.closeDrawer();
                } else {
                    myDrawer.openDrawer();
                }
            }
        });

        setSupportActionBar(toolbar);// Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayShowCustomEnabled(true);



        Log.d(TAG, "OnCreate");
        btnWorkOrder = (IconicsButton) findViewById(R.id.btnWorkOrder);
        btnPayRent = (IconicsButton) findViewById(R.id.btnPayRent);
        btnPaymentHistory = (IconicsButton) findViewById(R.id.btnPaymentHistory);
        btnMessaging = (IconicsButton) findViewById(R.id.btnMessaging);
        //btnLeave = (IconicsButton) findViewById(R.id.btnLeave);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);
        progress.setVisibility(View.GONE);



        showSpinner();




        btnWorkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainTenantActivity.this, WorkOrderInboxActivity.class);
                String propId = null;
                intent.putExtra("propId", propId);
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

       /* btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MainTenantActivity.this)
                        .title("Confirm Leaving Property")
                        .content("Are you sure you want to leave this property? This action can not be undone!")
                        .positiveText("Leave")
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                return;
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.remove("liveAt");
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.i("AT-EASE", "User has succesfully left property");
                                        } else {
                                            Log.d("AT-EASE", "User has not left the property. Error: " + e.toString());
                                        }
                                    }
                                });
                            }
                        })
                        .show();
            }
        });
        */




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
        Log.d(TAG,"sending broadcast");
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
    public void onStop(){
        Log.d(TAG, "OnStop");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
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
