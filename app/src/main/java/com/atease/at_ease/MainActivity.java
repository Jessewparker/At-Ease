package com.atease.at_ease;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.logOut();
        }

        ((AtEaseApplication) getApplicationContext()).getNewDrawerBuilder().withActivity(this).build();

        Button signin = (Button) findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ParseSIgnIn.class);
                startActivity(intent);
            }
        });

        Button newWorkOrder = (Button) findViewById(R.id.new_work_order);
        newWorkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewWorkOrderExpandableActivity.class);
                startActivity(intent);
            }
        });

        Button inbox = (Button) findViewById(R.id.inbox);
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WorkOrderInboxActivity.class);
                startActivity(intent);
            }
        });

        Button manny = (Button) findViewById(R.id.manny);
        manny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                ParseUser.logInInBackground("mannyger", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i("At-Ease", "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d("MYAPPTAG", "User Log-in Failed");
                        }
                    }
                });
            }
        });

        Button mark = (Button) findViewById(R.id.mark);
        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                ParseUser.logInInBackground("gusguyman", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i("At-Ease", "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d("MYAPPTAG", "User Log-in Failed");
                        }
                    }
                });
            }
        });

        Button ggm = (Button) findViewById(R.id.ggm);
        ggm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                ParseUser.logInInBackground("gusguymanager", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i("At-Ease", "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d("MYAPPTAG", "User Log-in Failed");
                        }
                    }
                });
            }
        });

        Button derek = (Button) findViewById(R.id.derek);
        derek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                ParseUser.logInInBackground("dwreck", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i("At-Ease", "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d("MYAPPTAG", "User Log-in Failed");
                        }
                    }
                });
            }
        });

        Button syd = (Button) findViewById(R.id.syd);
        syd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                ParseUser.logInInBackground("sydkid", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i("At-Ease", "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d("MYAPPTAG", "User Log-in Failed");
                        }
                    }
                });
            }
        });

        Button jes = (Button) findViewById(R.id.jes);
        jes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                ParseUser.logInInBackground("angryjes", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i("At-Ease", "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d("MYAPPTAG", "User Log-in Failed");
                        }
                    }
                });
            }
        });

        Button hannah = (Button) findViewById(R.id.hannah);
        hannah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                ParseUser.logInInBackground("hungryhannah", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i("At-Ease", "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d("MYAPPTAG", "User Log-in Failed");
                        }
                    }
                });
            }
        });

        Button justin = (Button) findViewById(R.id.justin);
        justin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                ParseUser.logInInBackground("justintime", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i("At-Ease", "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d("MYAPPTAG", "User Log-in Failed");
                        }
                    }
                });
            }
        });

        Button tom = (Button) findViewById(R.id.tom);
        tom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                ParseUser.logInInBackground("creepytom", "drowssap", new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i("At-Ease", "User " + user.getUsername() + " Logged in");
                        } else {
                            Log.d("MYAPPTAG", "User Log-in Failed");
                        }
                    }
                });
            }
        });
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
