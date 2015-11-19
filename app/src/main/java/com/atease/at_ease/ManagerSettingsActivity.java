package com.atease.at_ease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
//import android.widget.CheckBox;

import android.widget.Switch;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.joda.time.DateTime;

import com.rey.material.widget.CheckBox;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.rey.material.widget.EditText;

import java.util.Date;

public class ManagerSettingsActivity extends AppCompatActivity {

    TextView tvTitle;
    Button btnStripeConnect;
    Switch swUseStripe;
    CheckBox cbOccupied;
    MaterialCalendarView calendar;
    EditText etMonthlyRentDue;
    EditText etProrateDays;

    Date rentDueDate;
    Boolean occupied;
    Boolean stripePay;
    Boolean stripeAuthorized;
    int rentdue;
    int prorateDays;

    ParseObject stripeAuth;
    ParseObject mgrSettings;
    ParseObject property;



    final static String TAG = "ManagerSettings";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_settings);


        ParseUser currentUser = ParseUser.getCurrentUser();

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        btnStripeConnect = (Button) findViewById(R.id.btnStripeConnect);
        swUseStripe = (Switch) findViewById(R.id.swUseStripe);
        cbOccupied = (CheckBox) findViewById(R.id.cbOccupied);
        calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
        etMonthlyRentDue = (EditText) findViewById(R.id.etMonthlyRent);
        etProrateDays = (EditText) findViewById(R.id.etProrateDays);


        try{
            currentUser.fetchIfNeeded();
        }catch(ParseException ex){
            Log.d(TAG,ex.getMessage());
        }
    /*
        ParseQuery<ParseObject> stripeAuthQuery = ParseQuery.getQuery("StripeAuth");
        stripeAuthQuery.whereEqualTo("manager", currentUser);
        stripeAuthQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject stripeA, ParseException e) {
                if (e == null) {
                    stripeAuth = stripeA;
                    if(stripeAuth != null){
                    }
                } else {
                    Log.d(TAG, "couldn't get stripeAuth from Parse");
                }
            }
        });
*/
        ParseQuery<ParseObject> mgrSettingsQuery = ParseQuery.getQuery("ManagerSettings");
        mgrSettingsQuery.whereEqualTo("manager", currentUser);
        mgrSettingsQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject mgr, ParseException e) {
                if (e == null) {
                    mgrSettings = mgr;
                    stripeAuthorized = mgrSettings.getBoolean("authorizedStripe");
                    if(stripeAuthorized){
                        btnStripeConnect.setVisibility(View.GONE);
                        tvTitle.setText("Here you can choose if you want to turn Stripe Payments on or off");
                        stripePay = mgrSettings.getBoolean("useStripePayments");
                        swUseStripe.setChecked(stripePay);
                    }
                    else{
                        swUseStripe.setVisibility(View.GONE);
                        stripePay = false;
                    }


                } else {
                    Log.d(TAG, "couldn't get ManagerSettings from Parse");
                }
            }
        });

        ParseQuery<ParseObject> propertyQuery = ParseQuery.getQuery("Property");
        propertyQuery.whereEqualTo("owner", currentUser);
        propertyQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject prop, ParseException e) {
                if (e == null) {
                    property = prop;
                    rentDueDate = property.getDate("nextRentDue");
                    //DateTime dt = new DateTime(date);
                    if(rentDueDate != null){
                        calendar.setDateSelected(rentDueDate, true);
                        calendar.setCurrentDate(rentDueDate);
                    }
                    occupied = property.getBoolean("occupied");
                    if(occupied != null){
                        cbOccupied.setCheckedImmediately(occupied);
                    }
                    // assume 0 if doesn't exist?
                    rentdue = property.getInt("monthlyRentDue");

                    prorateDays = property.getInt("prorateDays");

                } else {
                    Log.d(TAG, "couldn't get Property from Parse");
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
