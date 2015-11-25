package com.atease.at_ease;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.atease.at_ease.models.Payment;
import com.mikepenz.iconics.view.IconicsTextView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainMultipleManagerActivity extends AppCompatActivity {
    static final String TAG = "MultipleManagerActivity";
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private PropertyRecyclerViewAdapter adapter;
    private PropertyRecyclerViewAdapter.PropertyViewHolder viewHolder;
    private List<ParseObject> propertyList = new ArrayList<ParseObject>();
    Button btnWorkOrder;
    Button btnPaymentHistory;


    private BroadcastReceiver receiver = null;
    private Intent broadcastIntent = new Intent("com.atease.at_ease.MessageService");
    private LocalBroadcastManager broadcaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_multiple_manager);

        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        final Drawer myDrawer = application.getNewDrawerBuilder().withActivity(this).build();

        //Toolbar stuff
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("At Ease");

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

        showSpinner();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new PropertyRecyclerViewAdapter(MainMultipleManagerActivity.this,this.propertyList);
        recyclerView.setAdapter(adapter);

        btnPaymentHistory = (Button) findViewById(R.id.btnPaymentHistory);
        btnWorkOrder = (Button) findViewById(R.id.btnWorkOrder);

        btnPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMultipleManagerActivity.this, PaymentHistoryActivity.class);
                startActivity(intent);
            }
        });

        populate();
    }

    private void showSpinner() {
        //progress.setVisibility(View.VISIBLE);
        //btnMessaging.setAlpha(0.25f);
        broadcaster = LocalBroadcastManager.getInstance(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                //progressDialog.dismiss();
                //bindMessagingButton();
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

        return super.onOptionsItemSelected(item);
    }

    private void populate(){
        ParseQuery<ParseObject> propertyQuery = ParseQuery.getQuery("Property");
        propertyQuery.whereEqualTo("owner", ParseUser.getCurrentUser());
        propertyQuery.selectKeys(Arrays.asList("nickname"));
        propertyQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> tmpPropList, ParseException e) {
                if (e == null) {
                    for (ParseObject entry : tmpPropList) {
                        propertyList.add(entry);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "populate method broke");
                }
            }
        });
        Log.d(TAG,"populated the property list");
    }
}

