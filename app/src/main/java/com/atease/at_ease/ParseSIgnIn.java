package com.atease.at_ease;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
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

    }
        /*
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", "gusguymanager");
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    ParseUser manager = objects.get(0);

                    ParseObject system = new ParseObject("System");
                    system.put("name", "University of Alabama");
                    system.put("owner", manager);
                    system.saveInBackground();

                    ParseObject complex = new ParseObject("Complex");
                    complex.put("system", system);
                    complex.put("name", "Bryce Lawn");
                    complex.put("owner", manager);
                    complex.saveInBackground();

                    ParseObject complex1 = new ParseObject("Complex");
                    complex1.put("system", system);
                    complex1.put("name", "Riverside");
                    complex1.put("owner", manager);
                    complex1.saveInBackground();

                    ParseObject building1 = new ParseObject("Building");
                    building1.put("complex", complex1);
                    building1.put("name", "North");
                    building1.put("owner", manager);
                    building1.saveInBackground();

                    ParseObject building2 = new ParseObject("Building");
                    building2.put("complex", complex1);
                    building2.put("name", "East");
                    building2.put("owner", manager);
                    building2.saveInBackground();

                    ParseObject address = new ParseObject("Address");
                    address.put("city", "Tuscaloosa");
                    address.put("country", "US");
                    address.put("state", "AL");
                    address.put("street", "601 Bryce Lawn Drive");
                    address.put("zipcode", "35401");
                    try {
                        address.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                    ParseObject building3 = new ParseObject("Building");
                    building3.put("complex", complex1);
                    building3.put("name", "West");
                    building3.put("owner", manager);
                    building3.saveInBackground();

                    ParseObject building4 = new ParseObject("Building");
                    building4.put("complex", complex);
                    building4.put("name", "601");
                    building4.put("owner", manager);
                    building4.saveInBackground();

                    ParseObject building5 = new ParseObject("Building");
                    building5.put("complex", complex);
                    building5.put("name", "607");
                    building5.put("owner", manager);
                    building5.saveInBackground();


                    ParseObject address2 = new ParseObject("Address");
                    address2.put("city", "Tuscaloosa");
                    address2.put("country", "US");
                    address2.put("state", "AL");
                    address2.put("street", "607 Bryce Lawn Drive");
                    address2.put("zipcode", "35401");
                    address2.saveInBackground();

                    ParseObject address3 = new ParseObject("Address");
                    address3.put("city", "Tuscaloosa");
                    address3.put("country", "US");
                    address3.put("state", "AL");
                    address3.put("street", "985 Riverside Lane");
                    address3.put("zipcode", "35487");
                    address3.saveInBackground();

                    ParseObject property = new ParseObject("Property");
                    property.put("name", "101A");
                    property.put("building", building4);
                    property.put("owner", manager);
                    property.put("address", address);
                    property.saveInBackground();

                    ParseObject property2 = new ParseObject("Property");
                    property2.put("name", "101B");
                    property2.put("building", building4);
                    property2.put("owner", manager);
                    property2.put("address", address);
                    property2.saveInBackground();

                    ParseObject property3 = new ParseObject("Property");
                    property3.put("name", "101C");
                    property3.put("building", building4);
                    property3.put("owner", manager);
                    property3.put("address", address);
                    property3.saveInBackground();

                    ParseObject property4 = new ParseObject("Property");
                    property4.put("name", "202A");
                    property4.put("building", building5);
                    property4.put("owner", manager);
                    property4.put("address", address2);
                    property4.saveInBackground();

                    ParseObject property5 = new ParseObject("Property");
                    property5.put("name", "202B");
                    property5.put("building", building5);
                    property5.put("owner", manager);
                    property5.put("address", address2);
                    property5.saveInBackground();

                    ParseObject property6 = new ParseObject("Property");
                    property6.put("name", "202C");
                    property6.put("building", building5);
                    property6.put("owner", manager);
                    property6.put("address", address2);
                    property6.saveInBackground();

                }
            }
        });
    }

//        finish();
//
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.whereEqualTo("username", "gusguymanager");
//        query.findInBackground(new FindCallback<ParseUser>() {
//            public void done(List<ParseUser> objects, ParseException e) {
//                if (e == null) {
//                    // The query was successful.
//                    ParseUser manager = objects.get(0);
//
//                    ParseUser user = new ParseUser();
//                    user.setUsername("creepytom");
//                    user.setPassword("drowssap");
//                    user.setEmail("freak@notreal.foh");
//
//// other fields can be set just like with ParseObject
//                    user.put("phone", "1111110004");
//                    user.put("address", "1 7 mile rd");
//                    user.put("city", "Gary");
//                    user.put("state", "IN");
//                    user.put("zip", "54321");
//                    user.put("isManager", false);
//                    user.put("isTenant", true);
//                    user.put("firstName", "Tom");
//                    user.put("lastName", "Duffbeer");
//                    user.put("manager", manager);
//
//                    user.put("building", "Bryce Lawn 607");
//                    user.put("room", "202B");
//
//                    user.signUpInBackground(new SignUpCallback() {
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                // Hooray! Let them use the app now.
//                                Log.d("At-Ease", "User added");
//                                ParseUser currentUser = ParseUser.getCurrentUser();
//                                currentUser.logOut();
//                                finish();
//                            } else {
//                                // Sign up didn't succeed. Look at the ParseException
//                                // to figure out what went wrong
//                                Log.d("At-Ease", "User add error: " + e.getMessage());
//                            }
//                        }
//                    });
//
//                } else {
//                    // Something went wrong.
//                    Log.d("At-Ease", "Manger search error: " + e.getMessage());
//                }
//            }
//        });
//
//
//    }
*/
}
