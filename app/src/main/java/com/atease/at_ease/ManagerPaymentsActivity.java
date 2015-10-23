package com.atease.at_ease;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ManagerPaymentsActivity extends AppCompatActivity {
    final String TAG = "ManagerPaymentsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_payments);


        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.logOut();
            Log.d(TAG, "current User has been logged out");
        }
        ParseUser.logInInBackground("jesseManager", "password", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null){
                    Log.d(TAG, "Manager User Login successful");
                }
                else{
                    Log.d(TAG, "Manager User Login Failed");
                }
            }
        });
        //set the currentUser because it should exist
        //currentUser = ParseUser.getCurrentUser();

        Button btnAddStripeAccount = (Button) findViewById(R.id.btnAddStripeAccount);
        btnAddStripeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagerPaymentsActivity.this, StripeConnectActivity.class);
                startActivity(intent);
            }
        });
        Button btnPaymentHistory = (Button) findViewById(R.id.btnPaymentHistory);
        btnPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagerPaymentsActivity.this, MainActivity.class);
                startActivity(intent);
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
