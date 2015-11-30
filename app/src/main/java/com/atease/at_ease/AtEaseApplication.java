package com.atease.at_ease;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import com.atease.at_ease.models.ManagerSettings;
import com.atease.at_ease.models.Payment;
import com.atease.at_ease.models.Property;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import net.danlew.android.joda.JodaTimeAndroid;


/**
 * Created by Mark on 10/8/2015.
 */
public class AtEaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(WorkOrder.class);
        ParseObject.registerSubclass(Payment.class);
        ParseObject.registerSubclass(ManagerSettings.class);
        ParseObject.registerSubclass(Property.class);
        Parse.initialize(this, "RWYMOqEP4OgQ4oZIqFjyqHGxG7uYzbaPDWuzvZPq", "WJgweZXzSzoCgsdwgv5h5VzHwryAiAV1FvTFrZyF");
        JodaTimeAndroid.init(this);
    }

    public DrawerBuilder getNewDrawerBuilder(Boolean isManager, Activity mContext) {
        if(isManager == null){
            //freaking.... send to login screen? this shouldn't happen
        }

        if(isManager){
            return getNewDrawerBuilderManager(mContext);
        }
        else{
            return getNewDrawerBuilderTenant(mContext);
        }
    }



    public DrawerBuilder getNewDrawerBuilderTenant(final Activity myActivity) {
        PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_home)
                .withName("Home");
        PrimaryDrawerItem workOrderInbox = new PrimaryDrawerItem()
                .withIcon(FontAwesome.Icon.faw_wrench)
                .withName("Work Orders");
        PrimaryDrawerItem messageInbox = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_forum)
                .withName("Messages");
        PrimaryDrawerItem rentInbox = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_attach_money)
                .withName("Rent");

        final PrimaryDrawerItem logout = new PrimaryDrawerItem()
                .withIcon(FontAwesome.Icon.faw_sign_out)
                .withName("Logout");
        final PrimaryDrawerItem leaveProperty = new PrimaryDrawerItem()
                .withIcon(FontAwesome.Icon.faw_truck)
                .withName("Leave Property");



        return new DrawerBuilder()
                .withTranslucentNavigationBar(false)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName("Manage Property")
                                .withSelectable(false),
                        workOrderInbox,
                        messageInbox,
                        rentInbox,
                        new DividerDrawerItem(),
                        leaveProperty,
                        new DividerDrawerItem(),
                        logout

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Log.d("At-Ease", "Pos: " + position);
                        if(drawerItem ==  logout){
                            new MaterialDialog.Builder(view.getContext())
                                    .title("Are you Sure you want to Logout?")
                                    .positiveText("Logout")
                                    .negativeText("Cancel")
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    }).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {

                                    dialog.dismiss(); //done, so dismiss the dialog
                                    Intent intent = new Intent(myActivity, LoginActivity.class);
                                    stopService(new Intent(getApplicationContext(), MessageService.class));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("EXIT", true);
                                    getApplicationContext().startActivity(intent);
                                    String username = ParseUser.getCurrentUser().getUsername();
                                    ParseUser.logOut();
                                    myActivity.finish();
                                    Toast.makeText(getApplicationContext(),
                                            username + " Logged out!(wait a few seconds)",
                                            Toast.LENGTH_LONG).show();

                                }


                        }).show();
                        }
                        else if(drawerItem == leaveProperty){
                            new MaterialDialog.Builder(view.getContext())
                                    .title("Confirm Leaving Property")
                                    .content("Are you sure you want to leave this property? This action can not be undone!")
                                    .positiveText("Leave")
                                    .negativeText("Cancel")
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(final MaterialDialog dialog, DialogAction which) {
                                            ParseUser currentUser = ParseUser.getCurrentUser();
                                            Log.i("At-ease", "Leave was clicked");

                                            currentUser.remove("liveAt");
                                            currentUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Log.i("AT-EASE", "User has successfully left property");
                                                        Intent intent = new Intent(myActivity, MainTenantActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intent.putExtra("EXIT", true);
                                                        getApplicationContext().startActivity(intent);
                                                        myActivity.finish();
                                                        dialog.dismiss();
                                                    } else {
                                                        Log.d("AT-EASE", "User has not left the property. Error: " + e.toString());
                                                        dialog.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    }).show();
                        }
                        return true;
                    }
                });
    }
    public DrawerBuilder getNewDrawerBuilderManager(final Activity myActivity) {
        PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_home)
                .withName("Home");
        PrimaryDrawerItem workOrderInbox = new PrimaryDrawerItem()
                .withIcon(FontAwesome.Icon.faw_wrench)
                .withName("Work Orders");
        PrimaryDrawerItem messageInbox = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_forum)
                .withName("Messages");
        PrimaryDrawerItem rentInbox = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_attach_money)
                .withName("Rent");

        final PrimaryDrawerItem logout = new PrimaryDrawerItem()
                .withIcon(FontAwesome.Icon.faw_sign_out)
                .withName("Logout");



        return new DrawerBuilder()
                .withTranslucentNavigationBar(false)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName("Manage Property")
                                .withSelectable(false),
                        workOrderInbox,
                        messageInbox,
                        rentInbox,
                        new DividerDrawerItem(),
                        logout

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Log.d("At-Ease", "Pos: " + position);
                        if(drawerItem ==  logout){
                            new MaterialDialog.Builder(view.getContext())
                                    .title("Are you Sure you want to Logout?")
                                    .positiveText("Logout")
                                    .negativeText("Cancel")
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    }).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {

                                    dialog.dismiss(); //done, so dismiss the dialog
                                    Intent intent = new Intent(myActivity, LoginActivity.class);
                                    stopService(new Intent(getApplicationContext(), MessageService.class));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("EXIT", true);
                                    getApplicationContext().startActivity(intent);
                                    String username = ParseUser.getCurrentUser().getUsername();
                                    ParseUser.logOut();
                                    myActivity.finish();
                                    Toast.makeText(getApplicationContext(),
                                            username + " Logged out!(wait a few seconds)",
                                            Toast.LENGTH_LONG).show();

                                }
                            }).show();
                        }
                        return true;
                    }
                });
    }
}
