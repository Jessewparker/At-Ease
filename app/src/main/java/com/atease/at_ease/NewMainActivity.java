package com.atease.at_ease;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class NewMainActivity extends AppCompatActivity {

    Button ggm;
    Button gus;
    Button jesseTenant;
    Button jesseManager;
    Button logout;

    final String TAG = "NewMainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        ggm = (Button) findViewById(R.id.btnGGManager);
        gus = (Button) findViewById(R.id.btnGusGuy );
        jesseTenant = (Button) findViewById(R.id.btnJesseTenant);
        jesseManager = (Button) findViewById(R.id.btnJesseManager);
        logout = (Button) findViewById(R.id.logoutButton);

        ggm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground("gusguymanager", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            //toastLogin(user.getUsername());
                            Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
                            startService(serviceIntent);

                            Log.i(TAG, "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d(TAG, "User Log-in Failed");
                        }
                    }
                });
            }
        });

        gus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground("gusguyman", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            //toastLogin(user.getUsername());
                            Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
                            startService(serviceIntent);
                            Log.i(TAG, "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d(TAG, "User Log-in Failed");
                        }
                    }
                });
            }
        });

        jesseManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground("jesseManager", "password", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            //toastLogin(user.getUsername());
                            Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
                            startService(serviceIntent);
                            Log.i(TAG, "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d(TAG, "User Log-in Failed");
                        }
                    }
                });
            }
        });

        jesseTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground("jesseTenant", "password", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            //toastLogin(user.getUsername());
                            Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
                            startService(serviceIntent);
                            determineActivity(user);//will launch the activity
                            Log.i(TAG, "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d(TAG, "User Log-in Failed");
                        }
                    }
                });
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getApplicationContext(), MessageService.class));
                String username = ParseUser.getCurrentUser().getUsername();
                ParseUser.logOut();
                hideLogout();
                showUsers();
                Toast.makeText(getApplicationContext(),
                        username + " Logged out",
                        Toast.LENGTH_LONG).show();

            }
        });

        hideLogout();



    }


    private void showLogout(){
        logout.setVisibility(View.VISIBLE);
    }
    private void hideLogout(){
        logout.setVisibility(View.GONE);
    }


    private void showUsers(){
        ggm.setVisibility(View.VISIBLE);
        gus.setVisibility(View.VISIBLE);
        jesseManager.setVisibility(View.VISIBLE);
        jesseTenant.setVisibility(View.VISIBLE);
    }
    private void hideUsers(){
        ggm.setVisibility(View.GONE);
        gus.setVisibility(View.GONE);
        jesseTenant.setVisibility(View.GONE);
        jesseManager.setVisibility(View.GONE);
    }
    private void toastLogin(String name){
        Toast.makeText(getApplicationContext(),
                name + " Logged in",
                Toast.LENGTH_LONG).show();
    }

    private void determineActivity(ParseUser user){
        //if manager && > 1 property "multiple manager?" (even if is also a tenant)
            //goto MainMultipleManagerActivity
        //else if manager && 1 property (even if is also a tenant)
            //go to MainSingleManagerActivity
        //else isTenant so go to MainTenantActivity
        if(user.getBoolean("isManager")){
            if(user.getInt("managedProperties") > 1){
                //multiple manage activity
            }
            else if(user.getInt("managedProperties") == 1){
                //single manager activity
            }
            else{
                //Force to add a property
            }
        }
        else if(user.getBoolean("isTenant")){
            Intent intent = new Intent(NewMainActivity.this, MainTenantActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onStop(){
        super.onStop();
        hideUsers();
        showLogout();

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
