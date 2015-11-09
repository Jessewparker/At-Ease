package com.atease.at_ease;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


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
import com.parse.ParseObject;



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

    public DrawerBuilder getNewDrawerBuilder() {
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

        PrimaryDrawerItem newWorkOrder = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_add_box)
                .withName("New Work Order");
        PrimaryDrawerItem newMessage = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_add_box)
                .withName("New Message");
        PrimaryDrawerItem payRent = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_add_box)
                .withName("Pay Rent");

        return new DrawerBuilder()
                .withTranslucentNavigationBar(false)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName("Inboxes")
                                .withSelectable(false),
                        workOrderInbox,
                        messageInbox,
                        rentInbox,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName("Actions")
                                .withSelectable(false),
                        newWorkOrder,
                        newMessage,
                        payRent
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Log.d("At-Ease", "Pos: " + position);
                        return true;
                    }
                });
    }
}
