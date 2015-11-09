package com.atease.at_ease;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TenantPaymentsActivity extends AppCompatActivity {
    TextView tvTitle;
    TextView tvRent;
    ParseObject property;
    static final String TAG ="TenantPaymentsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_payments);


        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("Rent Due ");
        //TextView tvRent = (TextView) findViewById(R.id.tvRent);
        //tvRent.setText("$590");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.logOut();
            Log.d(TAG, "current User has been logged out");
        }
       /* ParseUser.logInInBackground("jesseTenant", "password", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Log.d(TAG, "Tenant User Login successful");
                } else {
                    Log.d(TAG, "Tenant User Login Failed");
                }
            }
        });*/



        try{
            ParseUser.logIn("jesseTenant","password");
            currentUser = ParseUser.getCurrentUser();
            currentUser.fetch();
            property = currentUser.getParseObject("liveAt");
            property.fetch();
            setRentDuetv();
        }catch(ParseException ex){
            Log.d(TAG,ex.getMessage());
        }


        Button btnPayRent = (Button) findViewById(R.id.btnPayRent);
        btnPayRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TenantPaymentsActivity.this, RentPayActivity.class);
                startActivity(intent);
            }
        });

        Button btnPaymentHistory = (Button) findViewById(R.id.btnPaymentHistory);
        btnPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TenantPaymentsActivity.this, PaymentHistoryActivity.class);
                startActivity(intent);
            }
        });




    }
    private void setRentDuetv(){
        String rentdue = Integer.toString(property.getInt("rentAmount"));
        rentdue = rentdue.substring(0, rentdue.length() - 2) + "." + rentdue.substring(rentdue.length() - 2, rentdue.length());
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");
        String date = df.format(property.getDate("nextRentDue"));
        rentdue = "$" + rentdue + " due by " + date;
        tvTitle.setText(rentdue);
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
