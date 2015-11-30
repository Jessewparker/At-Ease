package com.atease.at_ease;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsTextView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.concurrent.Semaphore;

public class MainSingleManagerActivity extends AppCompatActivity {
    Toolbar toolbar;
    IconicsButton btnWorkOrder;
    Button btnPaymentSettings;
    Button btnPaymentHistory;
    IconicsButton btnMessaging;
    ProgressBar progress;
    ProgressDialog progressDialog;
    ParseUser currentUser;
    ParseObject property;
    String propertyId;



    private BroadcastReceiver receiver = null;
    private Intent broadcastIntent = new Intent("com.atease.at_ease.MessageService");
    private LocalBroadcastManager broadcaster;

    final String TAG = "Main Single Manager";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_single_manager);



        currentUser = ParseUser.getCurrentUser();
        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        final Drawer myDrawer = application.getNewDrawerBuilder(currentUser.getBoolean("isManager"),this).withActivity(this).build();
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

        btnWorkOrder = (IconicsButton) findViewById(R.id.btnWorkOrder);
        btnPaymentSettings = (Button) findViewById(R.id.btnPaymentSettings);
        btnPaymentHistory = (Button) findViewById(R.id.btnPaymentHistory);
        btnMessaging = (IconicsButton) findViewById(R.id.btnMessaging);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);
        progress.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);

        //lock = new Semaphore(0);
        propertyId = "";
        retrieveProperty();


        btnWorkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSingleManagerActivity.this, WorkOrderInboxActivity.class);
                String propId = null;
                intent.putExtra("propId", propId);
                startActivity(intent);
            }
        });


        btnPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSingleManagerActivity.this, PaymentHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSingleManagerActivity.this, ListUsersActivity.class);
                // Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
                intent.putExtra("propId", propertyId);
                //startService(serviceIntent);
                startActivity(intent);

            }
        });

        btnPaymentSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSingleManagerActivity.this, ManagerSettingsActivity.class);
                intent.putExtra("propId", propertyId);
                startActivity(intent);
            }
        });



    }




    private void retrieveProperty(){

        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ParseQuery<ParseObject> propertyQuery = ParseQuery.getQuery("Property");
        propertyQuery.whereEqualTo("owner", currentUser);
        propertyQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject prop, ParseException e) {
                if (e == null) {
                    property = prop;
                    propertyId = prop.getObjectId();

                    progressDialog.dismiss();
                    Toast.makeText(MainSingleManagerActivity.this, "property retrieved", Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(MainSingleManagerActivity.this, "couldn't get property from parse", Toast.LENGTH_LONG).show();
                }
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
                Intent intent = new Intent(MainSingleManagerActivity.this, ListUsersActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
