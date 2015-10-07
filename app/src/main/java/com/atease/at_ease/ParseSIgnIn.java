package com.atease.at_ease;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

/**
 * Created by Mark on 10/2/2015.
 */
public class ParseSIgnIn extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        finish();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", "gusguymanager");
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    ParseUser manager = objects.get(0);

                    ParseUser user = new ParseUser();
                    user.setUsername("creepytom");
                    user.setPassword("drowssap");
                    user.setEmail("freak@notreal.foh");

// other fields can be set just like with ParseObject
                    user.put("phone", "1111110004");
                    user.put("address", "1 7 mile rd");
                    user.put("city", "Gary");
                    user.put("state", "IN");
                    user.put("zip", "54321");
                    user.put("isManager", false);
                    user.put("isTenant", true);
                    user.put("firstName", "Tom");
                    user.put("lastName", "Duffbeer");
                    user.put("manager", manager);

                    user.put("building", "Bryce Lawn 607");
                    user.put("room", "202B");

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                Log.d("At-Ease", "User added");
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.logOut();
                                finish();
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                Log.d("At-Ease", "User add error: " + e.getMessage());
                            }
                        }
                    });

                } else {
                    // Something went wrong.
                    Log.d("At-Ease", "Manger search error: " + e.getMessage());
                }
            }
        });


    }

}
