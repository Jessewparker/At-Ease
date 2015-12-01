package com.atease.at_ease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.Date;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by lguthrie825 on 11/9/15.
 */
public class AddPropertyActivity extends Activity {

    private Button addPropertyButton;
    private MaterialEditText streetField;
    private MaterialEditText cityField;
    private MaterialSpinner stateField;
    private MaterialEditText zipField;
    private MaterialSpinner countryField;
    private MaterialEditText nicknameField;
    private MaterialEditText secondaryAddressField;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String nickname;
    String secondaryAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_property);

        addPropertyButton = (Button) findViewById(R.id.addButton);
        streetField = (MaterialEditText) findViewById(R.id.streetField);
        cityField = (MaterialEditText) findViewById(R.id.cityField);
        stateField = (MaterialSpinner) findViewById(R.id.stateField);
        zipField = (MaterialEditText) findViewById(R.id.zipField);
        countryField = (MaterialSpinner) findViewById(R.id.countryField);
        nicknameField = (MaterialEditText) findViewById(R.id.nicknameField);
        secondaryAddressField = (MaterialEditText) findViewById(R.id.secondaryAddressField);


        ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateField.setAdapter(adapterState);


        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this, R.array.countries_array, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryField.setAdapter(adapterCountry);

        /** pretend there is error checking atm **/
        addPropertyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(checkErrors()){
                    processAdd();
                }
                else{
                    //do nothing... errors will display
                }

            }
        });



    }

    /**
     street = streetField.getText().toString();
     city = cityField.getText().toString();
     state = stateField.getSelectedItem().toString();
     zip = zipField.getText().toString();
     country = countryField.getSelectedItem().toString();
     nickname = nicknameField.getText().toString();
     secondaryAddress = secondaryAddressField.getText().toString();
     **/

    private Boolean checkErrors(){
        Boolean success = true;

        if (streetField.getText().toString().equals("")){
            streetField.setError("Street is Required");
            success = false;
        }

        if (cityField.getText().toString().equals("")){
            cityField.setError("City is Required");
            success = false;
        }
        if (stateField.getSelectedItem().toString().equals("State")){
            stateField.setError("State is Required");
            success = false;
        }
        String zip = zipField.getText().toString();
        if (zip.equals("")){
            zipField.setError("Zipcode is Required");
            success = false;
        } else if(zip.length() != 5 || !zip.matches("[0-9]+")){
            zipField.setError("Zipcode Must Be Exactly 5 Digits");
            success = false;
        }
        if (countryField.getSelectedItem().toString().equals("Country")){
            countryField.setError("Country is Required");
            success = false;
        }

        return success;
    }

    private void processAdd(){
        street = streetField.getText().toString();
        city = cityField.getText().toString();
        state = stateField.getSelectedItem().toString();
        zip = zipField.getText().toString();
        country = countryField.getSelectedItem().toString();
        nickname = nicknameField.getText().toString();
        secondaryAddress = secondaryAddressField.getText().toString();

        ParseObject address = new ParseObject("Address");
        address.put("street", street);
        address.put("city", city);
        address.put("state", state);
        address.put("zipcode", zip);
        address.put("country", country);
        address.saveInBackground();
        Date date = new Date();
        //get address objectID
        if(nickname.equals("")){
            nickname = street + secondaryAddress;
        }
        final ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject property = ParseObject.create("Property");
        property.put("address", address);
        property.put("nickname", nickname);
        property.put("owner", currentUser);
        property.put("secondaryAddress", secondaryAddress);
        property.put("monthlyRentDue", 0);
        property.put("rentAmount",0);
        property.put("nextRentDue", date);
        property.put("occupied", false);


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


}
