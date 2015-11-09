package com.atease.at_ease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ManagerSettingsActivity extends AppCompatActivity {

    TextView tvTitle;
    Button btnStripeConnect;
    Switch swUseStripe;

    ParseObject stripeAuth;
    ParseObject mgrSettings;
    ParseObject property;


    final static String TAG = "ManagerSettings";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_settings);

        ParseUser currentUser = ParseUser.getCurrentUser();

        try{
            currentUser.fetchIfNeeded();
        }catch(ParseException ex){
            Log.d(TAG,ex.getMessage());
        }

        ParseQuery<ParseObject> stripeAuthQuery = ParseQuery.getQuery("StripeAuth");
        stripeAuthQuery.whereEqualTo("manager", currentUser);
        stripeAuthQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject stripeA, ParseException e) {
                if (e == null) {
                    stripeAuth = stripeA;
                } else {
                    Log.d(TAG, "couldn't get stripeAuth from Parse");
                }
            }
        });

        ParseQuery<ParseObject> mgrSettingsQuery = ParseQuery.getQuery("StripeAuth");
        mgrSettingsQuery.whereEqualTo("manager", currentUser);
        mgrSettingsQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject mgr, ParseException e) {
                if (e == null) {
                    mgrSettings = mgr;
                } else {
                    Log.d(TAG, "couldn't get stripeAuth from Parse");
                }
            }
        });

        ParseQuery<ParseObject> propertyQuery = ParseQuery.getQuery("StripeAuth");
        propertyQuery.whereEqualTo("owner", currentUser);
        propertyQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject prop, ParseException e) {
                if (e == null) {
                    property = prop;
                } else {
                    Log.d(TAG, "couldn't get stripeAuth from Parse");
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
