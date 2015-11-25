package com.atease.at_ease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by lguthrie825 on 11/9/15.
 */
public class AddPropertyActivity extends Activity {

    private Button addPropertyButton;
    private MaterialEditText streetField;
    private MaterialEditText cityField;
    private MaterialEditText stateField;
    private MaterialEditText zipField;
    private MaterialEditText countryField;
    private MaterialEditText nicknameField;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String nickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_property);

        addPropertyButton = (Button) findViewById(R.id.addButton);
        streetField = (MaterialEditText) findViewById(R.id.streetField);
        cityField = (MaterialEditText) findViewById(R.id.cityField);
        stateField = (MaterialEditText) findViewById(R.id.stateField);
        zipField = (MaterialEditText) findViewById(R.id.zipField);
        streetField = (MaterialEditText) findViewById(R.id.streetField);
        countryField = (MaterialEditText) findViewById(R.id.countryField);
        nicknameField = (MaterialEditText) findViewById(R.id.nicknameField);

        addPropertyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(AddPropertyActivity.this, MainActivity.class);
                street = streetField.getText().toString();
                city = cityField.getText().toString();
                state = stateField.getText().toString();
                zip = zipField.getText().toString();
                country = countryField.getText().toString();
                nickname = nicknameField.getText().toString();

                ParseObject address = new ParseObject("Address");
                address.put("street", street);
                address.put("city", city);
                address.put("state", state);
                address.put("zipcode", zip);
                address.put("country", country);
                address.saveInBackground();

                //get address objectID
                ParseUser currentUser = ParseUser.getCurrentUser();
                ParseObject property = new ParseObject("Property");
                property.put("address", address);
                property.put("nickname", nickname);
                property.put("owner", currentUser);

                currentUser.put("managedProperties", currentUser.getInt("managedProperties") + 1);
                currentUser.saveInBackground();
                property.saveInBackground();

                startActivity(intent);

            }
        });

    }


}
