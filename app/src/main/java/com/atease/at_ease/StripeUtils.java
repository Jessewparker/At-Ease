package com.atease.at_ease;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Jesse on 10/15/2015.
 */
public class StripeUtils {

    public final static String CLIENT_ID = "ca_79qGpRhksQeSY0fBdhGS6WE3EfJCEsjM";
    public final static String PUBLISHABLE_KEY = "pk_test_i4PhLnDdGymAFGdZP3jBmJat";
    public final static String TEST_SECRET_KEY = "sk_test_rbeh1corMgXe5RugC6oro1eg";
    private static final String TOKEN_URL = "https://connect.stripe.com/oauth/token";

    public static Map<String, String> splitQuery(String query){
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        try{
            String[] pairs = query.split("&");
            for (String pair : pairs){
                int index = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0,index), "UTF-8"),
                                URLDecoder.decode(pair.substring(index + 1),"UTF-8"));
            }
        }
        catch(UnsupportedEncodingException e){
            query_pairs.put("error", "UnsupportedEncodingException");
            query_pairs.put("error_description", e.getMessage());
        }
        return query_pairs;
    }

    public static String executePost(String url, String parameters) throws IOException {

        URL request = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) request.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "StripeConnectAndroid");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
        connection.setUseCaches (false);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();

        String response = streamToString(connection.getInputStream());
        connection.disconnect();
        return response;
    }

    private static String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }


    /*public static void getAccessToken( final String code, final ParseUser user) {
        //mProgress.setMessage("Connecting with Stripe");
       // mProgress.show();
        try {

            URL url = new URL(TOKEN_URL);
            String urlParameters = "code=" + code
                    + "&client_secret=" + TEST_SECRET_KEY
                    + "&grant_type=authorization_code";

            String response = StripeUtils.executePost(TOKEN_URL, urlParameters);
            JSONObject obj = new JSONObject(response);

            //Store data in parse now

            ParseObject stripeAuth = new ParseObject("StripeAuth");

            stripeAuth.put("access_token", obj.getString("access_token"));
            stripeAuth.put("livemode", obj.getBoolean("livemode"));
            stripeAuth.put("refresh_token", obj.getString("refresh_token"));
            stripeAuth.put("token_type",obj.getString("token_type"));
            stripeAuth.put("stripe_publishable_key",obj.getString("stripe_publishable_key"));
            stripeAuth.put("stripe_user_id",obj.getString("stripe_user_id"));
            stripeAuth.put("scope",obj.getString("scope"));
            stripeAuth.put("manager",user);
            stripeAuth.saveInBackground();

            Log.d("getAccessToken", "String data[access_token]:			" + obj.getString("access_token"));
            Log.d("getAccessToken", "String data[livemode]:				" + obj.getBoolean("livemode"));
            Log.d("getAccessToken", "String data[refresh_token]:			" + obj.getString("refresh_token"));
            Log.d("getAccessToken", "String data[token_type]:			" + obj.getString("token_type"));
            Log.d("getAccessToken", "String data[stripe_publishable_key]: " + obj.getString("stripe_publishable_key"));
            Log.d("getAccessToken", "String data[stripe_user_id]:		" + obj.getString("stripe_user_id"));
            Log.d("getAccessToken", "String data[scope]:					" + obj.getString("scope"));

        }
        catch (Exception ex) {
            ex.printStackTrace();
            Log.d("ERROR in StripeUtils", ex.toString());
        }
    } */
     public static void getAccessToken( final String code, final ParseUser user) {
       //mProgress.setMessage("Connecting with Stripe");
       // mProgress.show();

        new Thread() {
            @Override
            public void run() {
                //int what = SUCCESS;

                try {

                    URL url = new URL(TOKEN_URL);
                    String urlParameters = "code=" + code
                            + "&client_secret=" + TEST_SECRET_KEY
                            + "&grant_type=authorization_code";

                    String response = StripeUtils.executePost(TOKEN_URL, urlParameters);
                    JSONObject obj = new JSONObject(response);

                    //Store data in parse now

                    ParseObject stripeAuth = new ParseObject("StripeAuth");

                    stripeAuth.put("access_token", obj.getString("access_token"));
                    stripeAuth.put("livemode", obj.getBoolean("livemode"));
                    stripeAuth.put("refresh_token", obj.getString("refresh_token"));
                    stripeAuth.put("token_type",obj.getString("token_type"));
                    stripeAuth.put("stripe_publishable_key",obj.getString("stripe_publishable_key"));
                    stripeAuth.put("stripe_user_id",obj.getString("stripe_user_id"));
                    stripeAuth.put("scope",obj.getString("scope"));
                    stripeAuth.put("manager",user);
                    stripeAuth.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ManagerSettings");
                                query.whereEqualTo("manager",user);
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject parseObject, ParseException e) {
                                        if(e == null){
                                            parseObject.put("authorizedStripe", true);
                                            parseObject.saveInBackground();
                                            Log.d("Stripe Utils", "Saved in mgr settings");
                                        }
                                        else{
                                            Log.d("Stripe Utils", "error with Mgr Settings");
                                        }
                                    }
                                });
                            }
                            else{
                                Log.d("Stripe Utils", "error with stripeAuth");
                            }
                        }
                    });

                    Log.d("getAccessToken", "String data[access_token]:			" + obj.getString("access_token"));
                    Log.d("getAccessToken", "String data[livemode]:				" + obj.getBoolean("livemode"));
                    Log.d("getAccessToken", "String data[refresh_token]:			" + obj.getString("refresh_token"));
                    Log.d("getAccessToken", "String data[token_type]:			" + obj.getString("token_type"));
                    Log.d("getAccessToken", "String data[stripe_publishable_key]: " + obj.getString("stripe_publishable_key"));
                    Log.d("getAccessToken", "String data[stripe_user_id]:		" + obj.getString("stripe_user_id"));
                    Log.d("getAccessToken", "String data[scope]:					" + obj.getString("scope"));

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d("ERROR in StripeUtils", ex.toString());
                }


            }
        }.start();
    }
}
