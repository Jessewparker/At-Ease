package com.atease.at_ease;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsTextView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private HashMap<String,String> usernames;
    private ListView usersListView;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;
    private ParseUser currentUser;

    final String TAG = "ListUsersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        currentUser = ParseUser.getCurrentUser();
        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        final Drawer myDrawer = application.getNewDrawerBuilder(currentUser.getBoolean("isManager"),this).withActivity(this).build();

        //Toolbar stuff
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        title.setText("Messaging");
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



       // showSpinner();



        Log.d(TAG,getClass().getName());


    }

    //display clickable a list of all users
    private void setConversationsList() {

        currentUserId = currentUser.getObjectId();
        names = new ArrayList<String>();
        usernames = new HashMap<String,String>();

        if(currentUser.getBoolean("isTenant")){
            Log.d(TAG,"isTenant");
            ParseObject property;
            currentUser.fetchInBackground();
            property = currentUser.getParseObject("liveAt");
            property.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject property, com.parse.ParseException e) {
                    ParseUser owner = property.getParseUser("owner");
                    owner.fetchInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser pu, com.parse.ParseException e) {
                            names.add( pu.getString("firstName") + " " + pu.getString("lastName") );
                            usernames.put(pu.getString("firstName") + " " + pu.getString("lastName"), pu.getUsername() );
                            usersListView = (ListView)findViewById(R.id.usersListView);
                            namesArrayAdapter =
                                    new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.user_list_item, names);
                            usersListView.setAdapter(namesArrayAdapter);

                            usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                                    openConversation(names, i);
                                }
                            });
                            Log.d(TAG,"finished the isTenant");
                        }
                    });

                }
            });
        }
        if(currentUser.getBoolean("isManager")){
            //expect propId Extra
            Log.d(TAG,"is manager");
            ParseQuery<ParseObject> propQuery = ParseQuery.getQuery("Property");
            propQuery.whereEqualTo("objectId", getIntent().getStringExtra("propId"));
            propQuery.include("User");
            propQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> propList, com.parse.ParseException e) {
                    if(e == null){
                        List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<ParseUser>>();
                        for(ParseObject prop : propList){
                            ParseQuery<ParseUser> tenantQuery = ParseUser.getQuery();
                            tenantQuery.whereEqualTo("liveAt",prop);
                            tenantQuery.whereNotEqualTo("objectId", currentUserId);
                            queries.add(tenantQuery);
                        }
                        ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);
                        mainQuery.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list, com.parse.ParseException e) {
                                if (e == null) {
                                    for (ParseUser tenant : list) {
                                        names.add(tenant.getUsername());
                                    }
                                    usersListView = (ListView)findViewById(R.id.usersListView);
                                    namesArrayAdapter =
                                            new ArrayAdapter<String>(getApplicationContext(),
                                                    R.layout.user_list_item, names);
                                    usersListView.setAdapter(namesArrayAdapter);

                                    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                                            openConversation(names, i);
                                        }
                                    });
                                    Log.d(TAG, "finished the isManager");
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Error loading user list",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Error loading user list",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        /*
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("objectId", currentUserId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, com.parse.ParseException e) {
                if (e == null) {
                    for (int i=0; i<userList.size(); i++) {
                        names.add(userList.get(i).getUsername().toString());
                    }

                    usersListView = (ListView)findViewById(R.id.usersListView);
                    namesArrayAdapter =
                        new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.user_list_item, names);
                    usersListView.setAdapter(namesArrayAdapter);

                    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            openConversation(names, i);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error loading user list",
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    //open a conversation with one person
    public void openConversation(ArrayList<String> names, int pos) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", usernames.get(names.get(pos)));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, com.parse.ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(ListUsersActivity.this, MessagingActivity.class);
                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    intent.putExtra("USER_ID", user.get(0).getUsername());
                    intent.putExtra("USER_FULLNAME",user.get(0).getString("firstName") + " " + user.get(0).getString("lastName"));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //show a loading spinner while the sinch client starts
    private void showSpinner() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "message service connected!", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.atease.at_ease.ListUsersActivity"));
    }

    @Override
    public void onResume() {
        setConversationsList();
        super.onResume();
    }

    @Override
    public void onDestroy(){
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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
        else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


