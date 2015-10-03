package com.atease.at_ease;

import android.app.Activity;
import android.os.Bundle;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Mark on 10/2/2015.
 */
public class ParseSIgnIn extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser user = new ParseUser();
        user.setUsername("gusguymanager");
        user.setPassword("drowssap");
        user.setEmail("email1@example.com");

// other fields can be set just like with ParseObject
        user.put("phone", "650-253-0001");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

}
