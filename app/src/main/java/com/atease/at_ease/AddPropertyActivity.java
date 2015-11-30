package com.atease.at_ease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
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

        /** pretend there is error checking atm **/
        addPropertyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

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
                final ParseUser currentUser = ParseUser.getCurrentUser();
                ParseObject property = ParseObject.create("Property");
                property.put("address", address);
                property.put("nickname", nickname);
                property.put("owner", currentUser);

                currentUser.put("managedProperties", currentUser.getInt("managedProperties") + 1);
                currentUser.saveInBackground();
                property.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            currentUser.saveInBackground();
                            Intent intent = new Intent(AddPropertyActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d("add property", "Error with saving");
                            Toast.makeText(getApplicationContext(),
                                    "Could not create the property, check connection",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });




            }
        });

    }


}
