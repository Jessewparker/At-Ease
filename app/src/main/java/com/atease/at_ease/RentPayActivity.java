package com.atease.at_ease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.stripe.android.*;

import fr.ganfra.materialspinner.MaterialSpinner;

public class RentPayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_pay);

        Spinner stateSpinner = (MaterialSpinner) findViewById(R.id.spinnerState);
        ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapterState);

        Spinner countrySpinner = (MaterialSpinner) findViewById(R.id.spinnerCountry);
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this, R.array.countries_array, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapterCountry);

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
