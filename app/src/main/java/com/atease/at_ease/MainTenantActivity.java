package com.atease.at_ease;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainTenantActivity extends AppCompatActivity {

    Button btnWorkOrder;
    Button btnPayRent;
    Button btnPaymentHistory;
    Button btnMessaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tenant);

        btnWorkOrder = (Button) findViewById(R.id.btnWorkOrder);
        btnPayRent = (Button) findViewById(R.id.btnPayRent);
        btnPaymentHistory = (Button) findViewById(R.id.btnPaymentHistory);
        btnMessaging = (Button) findViewById(R.id.btnMessaging);

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

        btnMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainTenantActivity.this, ListUsersActivity.class);
               // Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);

                //startService(serviceIntent);
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
