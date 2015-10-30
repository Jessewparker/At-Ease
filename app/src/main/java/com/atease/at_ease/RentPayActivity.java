package com.atease.at_ease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.stripe.android.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.model.Charge;
//import com.stripe.Stripe;

import fr.ganfra.materialspinner.MaterialSpinner;

public class RentPayActivity extends AppCompatActivity {

    final String TAG = "Rent Pay Activity";
    EditText etCardHolderName;
    EditText etCardHolderAddressOne;
    EditText etCardHolderAddressTwo;
    EditText etCity;
    Spinner stateSpinner;
    EditText etZipcode;
    Spinner countrySpinner;
    EditText etCardNumber;
    EditText etCardCVC;
    Spinner expMonthSpinner;
    Spinner expYearSpinner;
    EditText etAmount;
    TextView tvDueRent;
    TextView tvError;
    Button btnConfirm;
    ParseUser currentUser;
    Stripe stripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_pay);


        currentUser = ParseUser.getCurrentUser();
    // need property and manager -> manager's stripeAuth settings to make the payment.
        stateSpinner = (MaterialSpinner) findViewById(R.id.spinnerState);
        ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapterState);

        countrySpinner = (MaterialSpinner) findViewById(R.id.spinnerCountry);
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this, R.array.countries_array, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapterCountry);

        expMonthSpinner = (MaterialSpinner) findViewById(R.id.spinnerExpMonth);
        ArrayAdapter<CharSequence> adapterExpMonth = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
        adapterExpMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expMonthSpinner.setAdapter(adapterExpMonth);

        expYearSpinner = (MaterialSpinner) findViewById(R.id.spinnerExpYear);
        ArrayAdapter<CharSequence> adapterExpYear = ArrayAdapter.createFromResource(this, R.array.years_array, android.R.layout.simple_spinner_item);
        adapterExpYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expYearSpinner.setAdapter(adapterExpYear);

        etCardHolderName = (EditText) findViewById(R.id.etCardHolderName);
        etCardHolderAddressOne = (EditText) findViewById(R.id.etCardHolderAddressOne);
        etCardHolderAddressTwo = (EditText) findViewById(R.id.etCardHolderAddressTwo);
        etCity = (EditText) findViewById(R.id.etCity);
        etZipcode = (EditText) findViewById(R.id.etZipcode);
        etCardNumber = (EditText) findViewById(R.id.etCardNumber);
        etCardCVC = (EditText) findViewById(R.id.etCardCVC);
        etAmount = (EditText) findViewById(R.id.etAmount);
        tvDueRent = (TextView) findViewById(R.id.tvDueRent);
        tvError = (TextView) findViewById(R.id.tvError);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked Confirm button");

               /* Log.d(TAG, "spinner is: " + stateSpinner.getSelectedItem().toString());
                Log.d(TAG, "Card holder is: " + etCardHolderName.getText().toString());
                Log.d(TAG,"cardholdername is: " + etCardHolderName.getText());
                Log.d(TAG,"billing address is: " + etCardHolderAddressOne.getText());*/

                String error = "Error, Required Fields:\n";
                error += findErrors();
                Log.d(TAG,"Errors are : " + error);
                if (error.equals("Error, Required Fields:\n")){

                    Card card = makeCard();
                    if (!card.validateCard()){
                        //false card
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("Error, your credit card information is invalid");
                    }
                    else{
                        Log.d(TAG, "Process the payment!");
                        tvError.setVisibility(View.GONE);
                        try{
                            stripe = new Stripe(StripeUtils.TEST_SECRET_KEY);
                        }
                        catch(Exception ex){
                            Log.d(TAG,ex.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    "Problem Authenticating the At-Ease Stripe Account",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                        /*
                        Token token;
                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        // Send token to your server
                                        tokeen = token;
                                    }

                                    public void onError(Exception error) {
                                        // Show localized error message
                                        Toast.makeText(getApplicationContext(),
                                                error.getMessage().toString(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }
                        );*/
                        //process card
                        Charge charge = new Charge();

                    }
                }
                else{
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(error);
                }

            }
        });

    }

    /**
     *  etCardHolderName;
      etCardHolderAddressOne;
      etCardHolderAddressTwo;
      etCity;
      stateSpinner;
      countrySpinner;
      etCardNumber;
      etCardCVC;
      expMonthSpinner;
      expYearSpinner;
      etAmount;
      tvDueRent;
       tvError;
     Button btnConfirm;

     */


    private String findErrors(){
        String errors = "";
        if (etCardHolderName.getText().toString().equals("")){
            errors += "-Card Holder Name\n";
        }

        if (etCardHolderAddressOne.getText().toString().equals("")){
            errors += "-Address !\n";
        }

        if (etCity.getText().toString().equals("")){
            errors += "-City\n";
        }

        if (etZipcode.getText().toString().equals("")){
            errors += "-Zipcode\n";
        }
        else if(etZipcode.getText().toString().length() != 5){
            errors += "-Zipcode needs to be 5 digits\n";
        }

        if (stateSpinner.getSelectedItem().toString().equals("State")){
            errors += "-State \n";
        }

        if (countrySpinner.getSelectedItem().toString().equals("Country")){
            errors += "-Country \n";
        }
        if (etCardNumber.getText().toString().equals("")){
            errors += "-Card Number\n";
        }
        else if (etCardNumber.getText().toString().length() != 16){
            errors += "-Card Number should be 16 digits\n";
        }

        if (etCardCVC.getText().toString().equals("")){
            errors += "-CVC\n";
        }

        else if(etCardCVC.getText().toString().length() != 3){
            errors += "-CVC should be 3 digits\n";
        }

        if (expMonthSpinner.getSelectedItem().toString().equals("Month")){
            errors += "-Expiration Month \n";
        }

        if (expYearSpinner.getSelectedItem().toString().equals("Year")){
            errors += "-Expiration Year\n";
        }

        if (etAmount.getText().toString().equals("")){
            errors += "-Payment Amount\n";
        }

        return errors;
    }

    private Card makeCard(){
        Card card = new Card(
                etCardNumber.getText().toString(),
                Integer.parseInt(expMonthSpinner.getSelectedItem().toString()),
                Integer.parseInt(expYearSpinner.getSelectedItem().toString()),
                etCardCVC.getText().toString());
        card.setName(etCardHolderName.getText().toString());
        card.setAddressLine1(etCardHolderAddressOne.getText().toString());
        card.setAddressLine2(etCardHolderAddressTwo.getText().toString());
        card.setAddressCity(etCity.getText().toString());
        card.setAddressState(stateSpinner.getSelectedItem().toString());
        card.setAddressZip("52525");
        card.setAddressCountry(countrySpinner.getSelectedItem().toString());


        return card;
    }

    private void makeCharge(Card card){

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
