package com.atease.at_ease;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.concurrent.Semaphore;

public class MainSingleManagerActivity extends AppCompatActivity {

    Button btnWorkOrder;
    Button btnPaymentSettings;
    Button btnPaymentHistory;
    Button btnMessaging;

    ParseObject property;
    String propertyId;
    Semaphore lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_single_manager);

        btnWorkOrder = (Button) findViewById(R.id.btnWorkOrder);
        btnPaymentSettings = (Button) findViewById(R.id.btnPaymentSettings);
        btnPaymentHistory = (Button) findViewById(R.id.btnPaymentHistory);
        btnMessaging = (Button) findViewById(R.id.btnMessaging);

        lock = new Semaphore(0);
        propertyId = "";


        btnWorkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSingleManagerActivity.this, WorkOrderInboxActivity.class);
                startActivity(intent);
            }
        });

       /* btnPaymentSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSingleManagerActivity.this, RentPayActivity.class);
                intent.putExtra("propId",propertyId);
                startActivity(intent);

            }
        });*/
        btnPaymentSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToSettings();
                stopListener();
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

                //startService(serviceIntent);
                startActivity(intent);

            }
        });
        retrieveProperty();

    }

    private void stopListener(){
        btnPaymentSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainSingleManagerActivity.this, "Clicked", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void moveToSettings(){
       try{
           lock.acquire();
           Intent intent = new Intent(MainSingleManagerActivity.this, ManagerSettingsActivity.class);
           intent.putExtra("propId", propertyId);
           startActivity(intent);
       }catch(Exception e){
       }

    }

    private void retrieveProperty(){


        ParseQuery<ParseObject> propertyQuery = ParseQuery.getQuery("Property");
        propertyQuery.whereEqualTo("owner", ParseUser.getCurrentUser());
        propertyQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject prop, ParseException e) {
                if (e == null) {
                    try{
                        Thread.sleep(2000);
                    }
                    catch(Exception xe){

                    }
                    property = prop;
                    propertyId = prop.getObjectId();
                    lock.release();
                    Toast.makeText(MainSingleManagerActivity.this, "property retrieved", Toast.LENGTH_LONG).show();
                    btnPaymentSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainSingleManagerActivity.this, ManagerSettingsActivity.class);
                            intent.putExtra("propId", propertyId);
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(MainSingleManagerActivity.this, "couldn't get property from parse", Toast.LENGTH_LONG).show();
                }
            }
        });
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
}
