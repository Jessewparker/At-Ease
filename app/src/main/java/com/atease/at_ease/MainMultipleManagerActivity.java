package com.atease.at_ease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.atease.at_ease.models.Payment;
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

    private RecyclerView recyclerView;
    private PropertyRecyclerViewAdapter adapter;
    private RecyclerView.ViewHolder viewHolder;
    private List<ParseObject> propertyList = new ArrayList<ParseObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_multiple_manager);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new PropertyRecyclerViewAdapter(MainMultipleManagerActivity.this,this.propertyList);
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

