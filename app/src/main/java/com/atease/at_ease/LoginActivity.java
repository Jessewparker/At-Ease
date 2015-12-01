package com.atease.at_ease;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mikepenz.materialize.color.Material;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends Activity {

    private Button signUpButton;
    private Button loginButton;
    private EditText usernameField;
    private EditText passwordField;
    private String username;
    private String password;
    private Intent intent;
//    private Intent serviceIntent;
    final String TAG = "Login Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            determineActivity(currentUser);
        }

        //intent = new Intent(getApplicationContext(), MainActivity.class);

        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signupButton);
        usernameField = (MaterialEditText) findViewById(R.id.loginUsername);
        passwordField = (MaterialEditText) findViewById(R.id.loginPassword);

        /******* THIS SHOULD BE REMOVED BEFORE APP IS DONE *******/

        Button ggm = (Button) findViewById(R.id.btnGGManager);
        Button gus = (Button) findViewById(R.id.btnGusGuy );
        Button jesseTenant = (Button) findViewById(R.id.btnJesseTenant);
        Button jesseManager = (Button) findViewById(R.id.btnJesseManager);

        ggm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground("gusguymanager", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            determineActivity(user);//will launch the activity

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

                            determineActivity(user);//will launch the activity

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
                            determineActivity(user);//will launch the activity
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
                            determineActivity(user);//will launch the activity

                            Log.i(TAG, "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d(TAG, "User Log-in Failed");
                        }
                    }
                });
            }
        });

        /******* END OF THIS SHOULD BE REMOVED BEFORE APP IS DONE *******/


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //intent = new Intent(LoginActivity.this, NewMainActivity.class);
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();

                //should never happen
                /*
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }
                */
                if(isEmail(username)){
                    username = emailToUsername(username);
                }

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            determineActivity(user);
                        } else {
                            usernameField.setError("Wrong Username or Email");
                            passwordField.setError("Wrong Password");
                            Toast.makeText(getApplicationContext(),
                                    "Wrong username/password combo",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                // finish();
            }
        });
    }

    private void determineActivity(ParseUser user){

        Intent serviceIntent = new Intent(LoginActivity.this, MessageService.class);
        Log.d("DETTTTT","determineActivity");
        if(user.getBoolean("isManager")){
            if(user.getInt("managedProperties") > 1){
                Intent intent = new Intent(LoginActivity.this, MainMultipleManagerActivity.class);
                startActivity(intent);
                if(!isMyServiceRunning(MessageService.class)){
                    startService(serviceIntent);
                }

                finish();
            }
            else if(user.getInt("managedProperties") == 1){
                Intent intent = new Intent(LoginActivity.this, MainSingleManagerActivity.class);
                startActivity(intent);
                if(!isMyServiceRunning(MessageService.class)){
                    startService(serviceIntent);
                }
                finish();
            }
            else{
                //Force to add a property
            }
        }
        else if(user.getBoolean("isTenant")){
            Intent intent = new Intent(LoginActivity.this, MainTenantActivity.class);
            startActivity(intent);
            startService(serviceIntent);
            finish();
        }

    }

    private Boolean isEmail(String word){
       return word.contains("@") ? true : false;
    }

    private String emailToUsername(String email){
        String username = "";

        ParseQuery<ParseUser> emailQuery = ParseUser.getQuery();
        emailQuery.whereEqualTo("email", email);
        try{
            username = emailQuery.getFirst().getUsername();
        }catch(Exception e){
            Log.d("Login Activity", "Error with the query...");
            Log.d("Login Activity", e.getMessage());
        }
       //either null or actually username
        return username;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
