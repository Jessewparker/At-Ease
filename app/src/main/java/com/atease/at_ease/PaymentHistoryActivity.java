package com.atease.at_ease;

import android.content.Intent;
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
import com.mikepenz.iconics.view.IconicsTextView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
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

        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        final Drawer myDrawer = application.getNewDrawerBuilder().withActivity(this).build();

        //Toolbar stuff
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Payment History");
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new PaymentHistoryRecyclerViewAdapter(this.paymentsList);
        recyclerView.setAdapter(adapter);
        populate();


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

    public void backUp(){
        finish();
    }
    private void populate(){
        Intent old = getIntent();
        ParseObject property = null;

        if(old.getStringExtra("propId") != null){
            ParseQuery<ParseObject> propertyQuery = ParseQuery.getQuery("Property");
            try {
               property = propertyQuery.get(old.getStringExtra("propId"));
            }
            catch(Exception e){

            }
        }

        ParseQuery<Payment> tenantQuery = ParseQuery.getQuery("Payment");
        ParseQuery<Payment> managerQuery = ParseQuery.getQuery("Payment");
        tenantQuery.whereEqualTo("tenant",ParseUser.getCurrentUser());
        if(property != null){
            tenantQuery.whereEqualTo("property",property);
        }
        managerQuery.whereEqualTo("manager", ParseUser.getCurrentUser());
        if(property != null){
            managerQuery.whereEqualTo("property",property);
        }
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
