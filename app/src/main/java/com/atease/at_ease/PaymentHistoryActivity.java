package com.atease.at_ease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.atease.at_ease.models.Payment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class PaymentHistoryActivity extends AppCompatActivity {
    static final String TAG = "PaymentHistoryActivity";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private PaymentHistoryRecyclerViewAdapter adapter;
    private RecyclerView.ViewHolder viewHolder;

    private List<Payment> paymentsList = new ArrayList<Payment>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);


        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backUp();
            }
        });
        setSupportActionBar(toolbar);// Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        application.getNewDrawerBuilder().withActivity(this).build();

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new PaymentHistoryRecyclerViewAdapter(this.paymentsList);
        recyclerView.setAdapter(adapter);
        populate();


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
        Log.d(TAG,"home: " + Integer.toString(R.id.home));
        Log.d(TAG,"home2: " + Integer.toString(android.R.id.home));
        Log.d(TAG,Integer.toString(id));
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void backUp(){
        finish();
    }
    private void populate(){
        ParseQuery<Payment> tenantQuery = ParseQuery.getQuery("Payment");
        ParseQuery<Payment> managerQuery = ParseQuery.getQuery("Payment");
        tenantQuery.whereEqualTo("tenant",ParseUser.getCurrentUser());
        managerQuery.whereEqualTo("manager", ParseUser.getCurrentUser());
        List<ParseQuery<Payment>> queries = new ArrayList<ParseQuery<Payment>>();
        queries.add(tenantQuery);
        queries.add(managerQuery);

        ParseQuery<Payment> mainQuery = ParseQuery.or(queries);
        mainQuery.include("User");
        mainQuery.orderByDescending("createdAt");

        mainQuery.findInBackground(new FindCallback<Payment>() {
            @Override
            public void done(List<Payment> tmpPaymentList, ParseException e) {
                if (e == null) {
                    for (Payment entry : tmpPaymentList) {
                        paymentsList.add(entry);
                        Log.d(TAG,entry.getAmount() );
                        Log.d(TAG,entry.getDate() );
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "populate method broke");
                }
            }
        });
        Log.d(TAG,"populated the payment history list");
    }
}
