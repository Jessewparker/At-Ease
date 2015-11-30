package com.atease.at_ease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.*;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by lguthrie825 on 11/19/15.
 */
public class AddTenantToPropertyActivity extends Activity {
    private Button add;
    private MaterialEditText propertyID;
    private String id;
    private ParseObject place;

final String TAG = "AddTenantToProp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser == null){
            Intent login = new Intent(AddTenantToPropertyActivity.this, LoginActivity.class);
            startActivity(login);
            finish();

        }

        setContentView(R.layout.activity_add_tenant_to_property);

        add = (Button) findViewById(R.id.addButton);
        propertyID = (MaterialEditText) findViewById(R.id.propertyID);
        Log.d(TAG, "started Acitivity");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = propertyID.getText().toString();
                final Intent intentAdd = new Intent(AddTenantToPropertyActivity.this, MainTenantActivity.class);

                ParseQuery<ParseObject> placeQuery = ParseQuery.getQuery("Property");
                placeQuery.whereEqualTo("objectId", id);
                placeQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(final ParseObject prop, ParseException e) {
                        if (prop == null) {
                            Toast.makeText(AddTenantToPropertyActivity.this, "Property does not exist", Toast.LENGTH_LONG).show();
                            //startActivity(intentAdd);
                        } else {
                            ParseUser user = ParseUser.getCurrentUser();
                            user.put("liveAt", prop);
                            user.saveInBackground();
                            startActivity(intentAdd);
                            finish();
                        }
                    }
                });
            }
        });
    }
}
