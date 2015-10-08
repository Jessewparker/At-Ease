package com.atease.at_ease;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
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
    SwipeToAction swipeToAction;

    List<WorkOrder> workOrderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_inbox);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        application.getNewDrawerBuilder().withActivity(this).build();

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

        adapter = new WorkOrderInboxRecyclerViewAdapter(this.workOrderList);
        recyclerView.setAdapter(adapter);

        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<WorkOrder>() {
            @Override
            public boolean swipeLeft(final WorkOrder inWorkOrder) {
                final int pos = removeWorkOrder(inWorkOrder);
                displaySnackbar(inWorkOrder.get_name() + " removed", "Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addWorkOrder(pos, inWorkOrder);
                    }
                });
                return true;
            }

            @Override
            public boolean swipeRight(WorkOrder inWorkOrder) {
                displaySnackbar(inWorkOrder.get_name() + " loved", null, null);
                return true;
            }

            @Override
            public void onClick(WorkOrder inWorkOrder) {
                displaySnackbar(inWorkOrder.get_name() + " clicked", null, null);
            }

            @Override
            public void onLongClick(WorkOrder inWorkOrder) {
                displaySnackbar(inWorkOrder.get_name() + " long-clicked", null, null);
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            Log.i("At-Ease", "Populating inbox for current user: "
                    + currentUser.getUsername());
            populateFromParse(currentUser);
        } else {
            // show the signup or login screen
            Log.d("MYAPPTAG", "No Current User, populating with default");
            populate();
        }

    }

    private void populate() {
        this.workOrderList.add(new WorkOrder("Mark", "1"));
        this.workOrderList.add(new WorkOrder("Mike", "2"));
        this.workOrderList.add(new WorkOrder("Mark", "3"));
        this.workOrderList.add(new WorkOrder("Derek", "4"));
        this.workOrderList.add(new WorkOrder("Bobby", "5"));
    }

    private void populateFromParse(final ParseUser user) {
        ParseQuery<ParseObject> tenantQuery = ParseQuery.getQuery("WorkOrder");
        ParseQuery<ParseObject> managerQuery = ParseQuery.getQuery("WorkOrder");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("WorkOrder");
        if(user.getBoolean("isTenant") && user.getBoolean("isManager")) {
            tenantQuery.whereEqualTo("tenant", user);
            managerQuery.whereEqualTo("manager", user);
            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
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

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.d("At-Ease", "Retrieved " + list.size() + " work orders");
                    for (int i = 0; i < list.size(); i++) {
                        WorkOrderInboxActivity.this.workOrderList.add(new WorkOrder(list.get(i), user));
                        Log.d("At-Ease", "Created work order " + Integer.toString(i));
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

    private int removeWorkOrder(WorkOrder workOrder) {
        int pos = workOrderList.indexOf(workOrder);
        workOrderList.remove(workOrder);
        adapter.notifyItemRemoved(pos);
        return pos;
    }

    private void addWorkOrder(int pos, WorkOrder workOrder) {
        workOrderList.add(pos, workOrder);
        adapter.notifyItemInserted(pos);
    }
}


