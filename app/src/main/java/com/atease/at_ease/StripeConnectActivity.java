package com.atease.at_ease;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Map;

import static com.atease.at_ease.StripeAuth.*;

public class StripeConnectActivity extends AppCompatActivity {

    final static String TAG = "StripeConnectActivity";

    WebView mWebView;
    String redirectURL = "http://ua-atease.github.io/At-Ease/";
    ProgressBar progress;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_connect);


        //get the currentUser
        currentUser = ParseUser.getCurrentUser();

        /*if (currentUser != null) {
            currentUser.logOut();
        }

        ParseUser.logInInBackground("jesseManager", "password", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                } else {
                    Log.d("MYAPPTAG", "User Log-in Failed");
                }
            }
        });
        currentUser = ParseUser.getCurrentUser();
        */

        //temporary
        String mUrl = "https://connect.stripe.com/oauth/authorize?response_type=code&client_id=ca_79qGpRhksQeSY0fBdhGS6WE3EfJCEsjM&scope=read_write";
        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        //mWebView.setWebViewClient(new OAuthWebViewClient());
        mWebView.setWebViewClient(new stripeWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
        Log.d(TAG, "Created WebView");

        progress = (ProgressBar) findViewById(R.id.myProgressBar);
        progress.setMax(100);
        progress.setVisibility(View.GONE);
        Log.d(TAG,"Progress Bar");

        //mWebView.setLayoutParams(FILL);



    }

    public class stripeWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progress.setVisibility(View.VISIBLE);
            StripeConnectActivity.this.progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
            //ProgressBar pBar = (ProgressBar) findViewById(R.id.myProgressBar);
            //pBar.setVisibility(View.GONE);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            progress.setVisibility(View.GONE);
            StripeConnectActivity.this.progress.setProgress(100);
            super.onPageFinished(view, url);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, " WebView Redirecting Url to: " + url);

            if(url.startsWith(redirectURL)){
                Log.d(TAG, "got the url: " + url);
                String queryString = url.replace(redirectURL + "?", ""); // cut out the url part, just want params
                Map<String, String> params = StripeUtils.splitQuery(queryString);
                Intent intent = new Intent(StripeConnectActivity.this, ManagerPaymentsActivity.class);
                if(!url.contains("error")) {
                    Log.d(TAG,"got error no auth");
                    // mListener.onComplete(params);
                }
                else {
                    //This needs to be made more robust, not very well done atm *************
                    StripeUtils.getAccessToken(params.get("code"), currentUser);
                    // mListener.onError(params);
                }
                //StripeDialog.this.dismiss();
                StripeConnectActivity.this.finish();
                startActivity(intent);
                return true;

            }

            view.loadUrl(url);
            return false;

        }
    }
    /*
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, " WebView Redirecting Url to: " + url);

            if(url.startsWith(redirectURL)){
                Log.d(TAG, "got the url: " + url);
                String queryString = url.replace(redirectURL + "?", ""); // cut out the url part, just want params
                Map<String, String> params = StripeUtils.splitQuery(queryString);
                Intent intent = new Intent(StripeConnectActivity.this, StripeAuthActivity.class);
                if(!url.contains("error")) {

                    intent.putExtra("code", params.get("code"));

                   // mListener.onComplete(params);
                }
                else {
                    intent.putExtra("error",params.get("error"));
                    intent.putExtra("error_description", params.get("error_description"));
                    // mListener.onError(params);
                }
                //StripeDialog.this.dismiss();
                StripeConnectActivity.this.finish();
                startActivity(intent);
                return true;

            }

            view.loadUrl(url);
            return false;

        }
    }
    */
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
