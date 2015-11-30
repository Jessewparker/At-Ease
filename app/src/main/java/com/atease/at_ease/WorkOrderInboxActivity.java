package com.atease.at_ease;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.iconics.view.IconicsTextView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;

/**
 * Created by Mark on 10/3/2015.
 */
public class WorkOrderInboxActivity extends AppCompatActivity {
    private Toolbar toolbar;
    RecyclerView recyclerView;
    WorkOrderInboxRecyclerViewAdapter adapter;

    ParseUser currentUser;
    List<WorkOrder> workOrderList = new ArrayList<>();


    @Override
    protected void onResume(){
        super.onResume();
        populateFromParse(currentUser);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_inbox);




        currentUser = ParseUser.getCurrentUser();
        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        final Drawer myDrawer = application.getNewDrawerBuilder(currentUser.getBoolean("isManager"),this).withActivity(this).build();

        //Toolbar stuff
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        title.setText("Work Orders");
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



        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkOrderInboxActivity.this, NewWorkOrderExpandableActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new WorkOrderInboxRecyclerViewAdapter(WorkOrderInboxActivity.this, this.workOrderList);
        recyclerView.setAdapter(adapter);


        if (currentUser != null) {
            // do stuff with the user
            ParseObject lives = currentUser.getParseObject("liveAt");
            if (lives == null) {
                Log.d("AT-EASE", "New work order disabled");
                actionButton.setEnabled(false);
                actionButton.setClickable(false);
                actionButton.setButtonColor(getResources().getColor(R.color.fab_material_blue_grey_500));
                actionButton.setButtonColorPressed(getResources().getColor(R.color.fab_material_blue_grey_900));
            } else {
                Log.d("AT-EASE", "Lives: " + lives.toString());
            }
            Log.i("At-Ease", "Populating inbox for current user: "
                    + currentUser.getUsername());
            //populateFromParse(currentUser);

        } else {
            // show the signup or login screen
            Log.d("MYAPPTAG", "No Current User, populating with default");
            //populate();
        }

    }

    private void populate() {
    }

    private void populateFromParse(final ParseUser user) {
        ParseQuery<WorkOrder> tenantQuery = ParseQuery.getQuery("WorkOrder");
        ParseQuery<WorkOrder> managerQuery = ParseQuery.getQuery("WorkOrder");
        ParseQuery<WorkOrder> query = ParseQuery.getQuery("WorkOrder");
        if(user.getBoolean("isTenant") && user.getBoolean("isManager")) {
            tenantQuery.whereEqualTo("tenant", user);
            managerQuery.whereEqualTo("manager", user);
            List<ParseQuery<WorkOrder>> queries = new ArrayList<>();
            queries.add(tenantQuery);
            queries.add(managerQuery);
            query = ParseQuery.or(queries);
        }
        else if(user.getBoolean("isTenant")) {
            query.whereEqualTo("tenant", user);
        }
        else if(user.getBoolean("isManager")) {
            query.whereEqualTo("manager", user);
        }

        query.whereEqualTo("isDeleted", false);

        String propId = getIntent().getStringExtra("propId");
        if (propId != null) {
            query.whereEqualTo("propId", propId);
        }

        WorkOrderInboxActivity.this.workOrderList.clear();

        query.findInBackground(new FindCallback<WorkOrder>() {
            @Override
            public void done(List<WorkOrder> results, ParseException e) {
                if (e == null) {
                    Log.d("At-Ease", "Retrieved " + results.size() + " work orders");
                    int i = 0;
                    for (WorkOrder workOrder : results) {
                        WorkOrderInboxActivity.this.workOrderList.add(workOrder);
                        Log.d("At-Ease", "Created work order " + Integer.toString(i));
                        i++;
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("At-Ease", "Error: " + e.getMessage());
                }
            }
        });
        Log.i("At-Ease", "Populated " + workOrderList.size() + " items from Parse");
        adapter.notifyDataSetChanged();
    }

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snackbar.getView();
        v.setBackgroundColor(getResources().getColor(R.color.secondary));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snackbar.show();
    }

    public static int removeWorkOrder(WorkOrder inWorkOrder, List<WorkOrder> inWorkOrderList, WorkOrderInboxRecyclerViewAdapter inAdapter) {
        int pos = inWorkOrderList.indexOf(inWorkOrder);
        inWorkOrderList.remove(inWorkOrder);
        inAdapter.notifyItemRemoved(pos);
        return pos;
    }

    private void addWorkOrder(int pos, WorkOrder workOrder) {
        workOrderList.add(pos, workOrder);
        adapter.notifyItemInserted(pos);
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


