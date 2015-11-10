package com.atease.at_ease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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
    private EditText passwardField;
    private EditText emailField;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        done = (Button) findViewById(R.id.doneSignUp);

    }
}
