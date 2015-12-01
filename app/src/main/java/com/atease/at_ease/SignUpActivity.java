package com.atease.at_ease;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.content.Intent;
import android.widget.Switch;
import android.widget.Toast;


import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by lguthrie825 on 11/5/15.
 */
public class SignUpActivity extends Activity {

    private Button done;
    private RadioButton isTenantField;
    private RadioButton isManagerField;
    private MaterialEditText firstNameField;
    private MaterialEditText lastNameField;
    private MaterialEditText usernameField;
    private MaterialEditText passwordField;
    private MaterialEditText passwordConfirmField;
    private MaterialEditText emailField;
    private MaterialEditText phoneNumberField;
    private Boolean tenantChecked;
    private Boolean managerChecked;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String passwordConfirm;
    private String email;
    private String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            currentUser.logOut();
        }

        setContentView(R.layout.activity_signup);

        done = (Button) findViewById(R.id.doneSignUp);
        isTenantField = (RadioButton) findViewById(R.id.isTenant);
        isManagerField = (RadioButton) findViewById(R.id.isManager);
        firstNameField = (MaterialEditText) findViewById(R.id.firstNameField);
        lastNameField = (MaterialEditText) findViewById(R.id.lastNameField);
        usernameField = (MaterialEditText) findViewById(R.id.usernameField);
        passwordField = (MaterialEditText) findViewById(R.id.passwordField);
        passwordConfirmField = (MaterialEditText) findViewById(R.id.passwordConfirmField);
        emailField = (MaterialEditText) findViewById(R.id.emailField);
        phoneNumberField = (MaterialEditText) findViewById(R.id.phoneNumberField);
        isTenantField.setChecked(true);
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
                if(errorCheck()) {
                    tenantChecked = isTenantField.isChecked();
                    managerChecked = isManagerField.isChecked();
                    firstName = firstNameField.getText().toString();
                    lastName = lastNameField.getText().toString();
                    username = usernameField.getText().toString();
                    password = passwordField.getText().toString();
                    passwordConfirm = passwordConfirmField.getText().toString();
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
                    user.put("managedProperties", 0);


                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseUser.logInInBackground(username, password, new LogInCallback() {
                                    @Override
                                    public void done(ParseUser parseUser, ParseException e) {
                                        if (parseUser.getBoolean("isManager")) {
                                            Log.d("sign up", "is manager");
                                            ParseObject mgrSet = ParseObject.create("ManagerSettings");
                                            mgrSet.put("manager", parseUser);
                                            mgrSet.put("authorizedStripe", false);
                                            mgrSet.put("useStripePayments", false);
                                            mgrSet.saveInBackground();
                                        }
                                        determineActivity(parseUser);
                                    }
                                });
                            } else {
                                Log.d("Signup", "error signing up user");
                                Log.d("Singup", e.getMessage());
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "Please Fill Out All of the Information",
                            Toast.LENGTH_LONG).show();
                }
            }
        });




    }


    /**
     tenantChecked = isTenantField.isChecked();
     managerChecked = isManagerField.isChecked();
     firstName = firstNameField.getText().toString();
     lastName = lastNameField.getText().toString();
     username = usernameField.getText().toString();
     password = passwordField.getText().toString();
     email = emailField.getText().toString();
     phoneNumber = phoneNumberField.getText().toString();
     *
     * **/
    private Boolean errorCheck(){
        Boolean success = true;
        if(firstNameField.getText().toString().equals("")){
            firstNameField.setError("First Name is Required");
            success = false;
        }
        if(lastNameField.getText().toString().equals("")){
            lastNameField.setError("Last Name is Required");
            success = false;
        }
        if(usernameField.getText().toString().equals("")){
            usernameField.setError("Username is Required");
            success = false;
        }else if(usernameField.getText().toString().contains("@")) {
            usernameField.setError("Username cannot contain '@' ");
            success = false;
        }

        if(passwordField.getText().toString().equals("")){
            passwordField.setError("Password is Required");
            passwordConfirmField.setError("Password is Required");
            success = false;
        }
         else if(!passwordConfirmField.getText().toString().equals(passwordField.getText().toString())){
            passwordField.setError("Passwords Must Match");
            passwordConfirmField.setError("Passwords Must Match");
            success = false;
        }
        if(emailField.getText().toString().equals("")){
            emailField.setError("Email Address is Required");
            success = false;
        }else if(!emailField.getText().toString().contains("@")){
            emailField.setError("Enter a Valid Email Address");
            success = false;
        }
        if(phoneNumberField.getText().toString().equals("")){
            phoneNumberField.setError("Phone Number is Required");
            success = false;
        }

        return success;
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
                Intent intent = new Intent(SignUpActivity.this, AddPropertyActivity.class);
                startActivity(intent);
                startService(serviceIntent);
                finish();
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
