package com.atease.at_ease;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.content.Intent;
import android.widget.Switch;


import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by lguthrie825 on 11/5/15.
 */
public class SignUpActivity extends Activity {

    private Button done;
    private RadioButton isTenantField;
    private RadioButton isManagerField;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText emailField;
    private EditText phoneNumberField;
    private Boolean tenantChecked;
    private Boolean managerChecked;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);

        setContentView(R.layout.activity_signup);

        done = (Button) findViewById(R.id.doneSignUp);
        isTenantField = (RadioButton) findViewById(R.id.isTenant);
        isManagerField = (RadioButton) findViewById(R.id.isManager);
        firstNameField = (EditText) findViewById(R.id.firstNameField);
        lastNameField = (EditText) findViewById(R.id.lastNameField);
        usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        emailField = (EditText) findViewById(R.id.emailField);
        phoneNumberField = (EditText) findViewById(R.id.phoneNumberField);

        isTenantField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTenantField.isChecked()){
                    isManagerField.setChecked(false);
                }
            }
        });
        isManagerField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isManagerField.isChecked()){
                    isTenantField.setChecked(false);
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tenantChecked = isTenantField.isChecked();
                managerChecked = isManagerField.isChecked();
                firstName = firstNameField.getText().toString();
                lastName = lastNameField.getText().toString();
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();
                email = emailField.getText().toString();
                phoneNumber = phoneNumberField.getText().toString();

                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.put("firstName", firstName);
                user.put("lastName", lastName);
                user.put("phone", phoneNumber);
                user.put("isTenant", tenantChecked);
                user.put("isManager", managerChecked);


                user.signUpInBackground();
                if(user.getBoolean("isManager")){
                    ParseObject mgrSet = new ParseObject("ManagerSettings");
                    mgrSet.add("manager",user);
                    mgrSet.add("authorizedStripe",false);
                    mgrSet.add("useStripePayments",false);
                    mgrSet.saveInBackground();
                }

                ParseUser.logInInBackground(username, password);
                startActivity(intent);



            }
        });




    }
    private void determineActivity(ParseUser user){
        Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);

        if(user.getBoolean("isManager")){
            if(user.getInt("managedProperties") > 1){
                Intent intent = new Intent(SignUpActivity.this, MainMultipleManagerActivity.class);
                startActivity(intent);
                startService(serviceIntent);
                finish();
            }
            else if(user.getInt("managedProperties") == 1){
                Intent intent = new Intent(SignUpActivity.this, MainSingleManagerActivity.class);
                startActivity(intent);
                startService(serviceIntent);
                finish();
            }
            else{
                //Force to add a property
            }
        }
        else if(user.getBoolean("isTenant")){
            Intent intent = new Intent(SignUpActivity.this, MainTenantActivity.class);
            startActivity(intent);
            startService(serviceIntent);
            finish();
        }

    }
}