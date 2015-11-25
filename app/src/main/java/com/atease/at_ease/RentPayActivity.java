package com.atease.at_ease;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.atease.at_ease.models.ManagerSettings;
import com.atease.at_ease.models.Payment;
import com.atease.at_ease.models.Property;
import com.mikepenz.iconics.view.IconicsTextView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.stripe.android.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.util.DateUtils;
import com.stripe.model.Charge;
//import com.stripe.Stripe;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class RentPayActivity extends AppCompatActivity {

    private Toolbar toolbar;

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

    ParseObject property;
    ParseUser manager;
    ParseObject stripeAuth;
    String fixedAmount;

    final String TAG = "Rent Pay Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_pay);

        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        final Drawer myDrawer = application.getNewDrawerBuilder().withActivity(this).build();

        //Toolbar stuff
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Rent Pay");
        IconicsTextView rightToggle = (IconicsTextView) toolbar.findViewById(R.id.rightToggle);
        rightToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myDrawer.isDrawerOpen()) {
                    myDrawer.closeDrawer();
                } else {
                    myDrawer.openDrawer();
                }
            }
        });

        setSupportActionBar(toolbar);// Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        currentUser = ParseUser.getCurrentUser();
        try{
            currentUser.fetch();
            property = currentUser.getParseObject("liveAt");
            property.fetch();
            manager = property.getParseUser("owner");
            manager.fetch();
        }catch(ParseException ex){
            Log.d(TAG, ex.getMessage());
        }

        getStripeAuth();

        Date date = property.getDate("nextRentDue");
        DateTime dzate = new DateTime(date);
        Log.d(TAG, "day: " + Integer.toString(dzate.getDayOfMonth()));
        Log.d(TAG, "month: " + Integer.toString(dzate.getMonthOfYear()));
        Log.d(TAG, "year: " + Integer.toString(dzate.getYear()));




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


        etCardHolderName.setText("Jesse Parker");
        etCardHolderAddressOne.setText("718 Vicksburg Drive");
        etCity.setText("Tuscaloosa");
        etZipcode.setText("35406");
        etCardNumber.setText("4242424242424242");
        etCardCVC.setText("123");
        etAmount.setText("5000");

        Log.d(TAG, property.getString("nickname"));
        Log.d(TAG, manager.getEmail());
        Log.d(TAG, currentUser.getEmail());
        setRentDuetv();


        btnConfirm.setOnClickListener(new View.OnClickListener() {




            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked Confirm button");

                Log.d(TAG, "spinner is: " + stateSpinner.getSelectedItem().toString());
                Log.d(TAG, "Card holder is: " + etCardHolderName.getText().toString());
                Log.d(TAG,"cardholdername is: " + etCardHolderName.getText());
                Log.d(TAG,"billing address is: " + etCardHolderAddressOne.getText());

                String error = "Error, Required Fields:\n";
                error += findErrors();

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
                            Log.d(TAG, " in the try to make stripe");
                            stripe = new Stripe(StripeUtils.PUBLISHABLE_KEY);
                            stripe.createToken(
                                    card,
                                    new TokenCallback() {
                                        public void onSuccess(Token token) {
                                            fixedAmount = fixAmount(etAmount.getText().toString());
                                            final Map<String, Object> chargeParams = new HashMap<String, Object>();
                                            chargeParams.put("amount", Integer.parseInt(fixedAmount));
                                            chargeParams.put("destination",stripeAuth.getString("stripe_user_id"));
                                            chargeParams.put("currency", "usd");
                                            chargeParams.put("card", token.getId());
                                            Log.d(TAG,"inside OnSuccess");

                                            new AsyncTask<Void, Void, Void>() {
                                                Charge charge;
                                                @Override
                                                protected Void doInBackground(Void... params) {
                                                    try {
                                                        com.stripe.Stripe.apiKey = StripeUtils.TEST_SECRET_KEY;
                                                        charge = Charge.create(chargeParams);

                                                    } catch (Exception e) {
                                                        Toast.makeText(RentPayActivity.this,
                                                                "Error creating charge :" + e.getLocalizedMessage(),
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                    }
                                                    return null;
                                                }

                                                protected void onPostExecute(Void result) {
                                                    persistPayment();
                                                    adjustRent();
                                                    finish();
                                                   // Toast.makeText(TenantPaymentsActivity,
                                                     //       //might want to change this
                                                      //      "Card Successfully Charged",
                                                      //      Toast.LENGTH_LONG
                                                   // ).show();
                                                }
                                            }.execute();
                                        }
                                        public void onError(Exception error) {
                                            // Show localized error message
                                            Toast.makeText(RentPayActivity.this,
                                                    error.getMessage().toString(),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    }
                            );
                        }
                        catch(Exception ex) {
                            Log.d(TAG, ex.getMessage());
                            Toast.makeText(RentPayActivity.this,
                                    "Problem Authenticating the At-Ease Stripe Account",
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                    }
                }
                else{
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(error);
                    Log.d(TAG, "Errors are : " + error);
                }

            }
        });


    }

    private void persistPayment(){
        double amount = Double.parseDouble(fixedAmount);
        amount /= 100.0;

        ParseObject payment = ParseObject.create("Payment");
        payment.put("manager", manager);
        payment.put("tenant", currentUser);
        payment.put("property",property);
        payment.put("amount", amount);
        payment.saveInBackground();

    }

    private void adjustRent(){
        int amountPayed = Integer.parseInt(fixedAmount);
        int amountNeeded = property.getInt("rentAmount");
        int differ = amountNeeded - amountPayed;
        if(differ > 0 ){
            property.put("rentAmount", differ);
        }
        else{ //move up one month,
            int monthly = property.getInt("monthlyRentDue");
            Date date = property.getDate("nextRentDue");
            DateTime dt = new DateTime(date);
            dt = dt.plusMonths(1);
            amountPayed = differ * -1; // extra that rolled over if any
            while(amountPayed > monthly){
                amountPayed -= monthly;
                dt = dt.plusMonths(1);
            }
            differ = monthly - amountPayed;
            property.put("rentAmount", differ);
            property.put("nextRentDue", dt.toDate());
        }
        property.saveInBackground();
    }

    public boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private String fixAmount(String amount){
        String ans;

        if(amount.contains(".")){
            Log.d(TAG,amount);
            String[] ansSplit = amount.split("[.]");
            Log.d(TAG,ansSplit[0]);
            Log.d(TAG,ansSplit[1]);
            if(ansSplit[1].length() > 2){
                ansSplit[1] = ansSplit[1].substring(0,2);
                ans = ansSplit[0] + ansSplit[1];
            }
            else if(ansSplit[1].length() == 2){
                ans = ansSplit[0] + ansSplit[1];
            }
            else if(ansSplit[1].length() == 1){
                ans = ansSplit[0] + ansSplit[1] + "0";
            }
            else{
                ans = ansSplit[0] + "00";
            }
        }
        else{
            ans = amount += "00";
        }
        return ans;
    }


    private void setRentDuetv(){
        String rentdue = Integer.toString(property.getInt("rentAmount"));
        rentdue = rentdue.substring(0, rentdue.length() - 2) + "." + rentdue.substring(rentdue.length() - 2, rentdue.length());
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");
        String date = df.format(property.getDate("nextRentDue"));
        rentdue = "$" + rentdue + " due by " + date;
        tvDueRent.setText(rentdue);
    }


    private void getStripeAuth(){
        ParseQuery<ParseObject> stripeAuthQuery = ParseQuery.getQuery("StripeAuth");
        stripeAuthQuery.whereEqualTo("manager", manager);

        stripeAuthQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject stripeA, ParseException e) {
                if (e == null) {
                    stripeAuth = stripeA;
                } else {
                    Log.d(TAG, "couldn't get stripeAuth from Parse");
                }
            }
        });
    }


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
        card.setAddressZip(etZipcode.getText().toString());
        card.setAddressCountry(countrySpinner.getSelectedItem().toString());


        return card;
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
        else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
