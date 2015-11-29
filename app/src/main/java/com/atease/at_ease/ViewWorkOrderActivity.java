package com.atease.at_ease;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.software.shell.fab.ActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mark on 10/23/2015.
 */
public class ViewWorkOrderActivity extends Activity {
    SliderLayout sliderShow;

    ArrayList<String> image_description =new ArrayList<String>();
    ArrayList<String> image_list=new ArrayList<String>();
    ArrayList<Drawable> image_drawable=new ArrayList<Drawable>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_work_order);

        String objID = getIntent().getStringExtra("workOrder");
        Log.d("At-Ease", ":" + objID);
        WorkOrder workOrder = ParseObject.createWithoutData(WorkOrder.class, objID);
        try {
            workOrder.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        ParseQuery<WorkOrder> query = ParseQuery.getQuery(WorkOrder.class);
//        query.whereEqualTo("objectId", objID);
//        WorkOrder workOrder = null;
//        try {
//            workOrder = query.find().get(0);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        sliderShow = (SliderLayout) findViewById(R.id.slider);
        sliderShow.setPresetTransformer(SliderLayout.Transformer.FlipPage);
        sliderShow.stopAutoCycle();

        ActionButton actionButton = (ActionButton) findViewById(R.id.view_work_order_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ParseUser currentUser = workOrder.getTenant();
        if (currentUser != null) {
            // do stuff with the user
            TextView tenantTextViewName = (TextView) findViewById(R.id.new_work_order_tenant_name);
            TextView tenantTextViewEmail = (TextView) findViewById(R.id.new_work_order_tenant_email);
            TextView tenantTextViewNumber = (TextView) findViewById(R.id.new_work_order_tenant_phone);
            TextView tenantTextViewAddress = (TextView) findViewById(R.id.new_work_order_tenant_address);
            TextView tenantTextViewLocation = (TextView) findViewById(R.id.new_work_order_tenant_location);

            ParseObject lives = null;
            ParseObject address = null;
            String addressString = null;
            ParseObject building = null;
            ParseObject complex = null;
            String locString = "N/A";
            try {
                lives = currentUser.getParseObject("liveAt");
                if (lives != null) {
                    lives.fetchIfNeeded();
                    locString = lives.getString("name");
                    address = lives.getParseObject("address");
                    if (address != null) {
                        address.fetchIfNeeded();
                        addressString = address.getString("street") + ", " +
                                address.getString("city") + ", " +
                                address.getString("state") + " " +
                                address.getString("zipcode");
                    } else {
                        addressString = "N/A";
                    }
                    building = lives.getParseObject("building");
                    if (building != null) {
                        building.fetchIfNeeded();
                        locString = building.getString("name") + "-" + locString;
                        complex = building.getParseObject("complex");
                        if (complex != null) {
                            complex.fetchIfNeeded();
                            locString = complex.getString("name") + " - " + locString;
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tenantTextViewName.setText(currentUser.getString("firstName") + " " + currentUser.getString("lastName"));
            tenantTextViewEmail.setText(currentUser.getString("email"));
            String phoneString = currentUser.getString("phone");
            tenantTextViewNumber.setText(phoneString.substring(0,3) + "-"
                    + phoneString.substring(3, 6) + "-"
                    + phoneString.substring(6, 10));

            tenantTextViewAddress.setText(addressString);

            tenantTextViewLocation.setText(locString);

            Log.d("At-Ease", currentUser.toString());

            ParseUser manager = null;
            TextView managerTextViewName = (TextView) findViewById(R.id.new_work_order_manager_name);
            TextView managerTextViewEmail = (TextView) findViewById(R.id.new_work_order_manager_email);
            TextView managerTextViewNumber = (TextView) findViewById(R.id.new_work_order_manager_phone);
            try {
                manager = lives.getParseUser("owner");
                if (manager != null) {
                    manager.fetchIfNeeded();
                    Log.d("At-Ease", manager.getUsername());
                    managerTextViewName.setText(manager.getString("firstName") + " " + manager.getString("lastName"));
                    managerTextViewEmail.setText(manager.getString("email"));
                    String phoneStringM = manager.getString("phone");
                    managerTextViewNumber.setText(phoneStringM.substring(0, 3) + "-"
                            + phoneStringM.substring(3, 6) + "-"
                            + phoneStringM.substring(6, 10));
                } else {
                    managerTextViewName.setText("N/A");
                    managerTextViewEmail.setText("N/A");
                    managerTextViewNumber.setText("N/A");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            // show the signup or login screen
            Log.d("MYAPPTAG", "No Current User");
        }

        TextView subject = (TextView) findViewById(R.id.view_work_order_subject);
        subject.setText(workOrder.getSubject());
        Log.d("At-Ease", workOrder.getSubject());
        TextView text = (TextView) findViewById(R.id.view_work_order_text);
        try {
            text.setText(workOrder.getTextAsString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            Drawable pic1 = workOrder.getPic1AsDrawable();
            Drawable pic2 = workOrder.getPic2AsDrawable();
            Drawable pic3 = workOrder.getPic3AsDrawable();
            File mypath1 = new File(directory,"pic1.png");
            File mypath2 = new File(directory,"pic2.png");
            File mypath3 = new File(directory,"pic3.png");
            if (pic1 != null)
            {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(mypath1);
                    ((BitmapDrawable) pic1).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                TextSliderView textSliderView1 = new TextSliderView(this);
                textSliderView1
                        .description("Picture 1")
                        .image(mypath1);

                sliderShow.addSlider(textSliderView1);
            }
            if (pic2 != null)
            {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(mypath2);
                    ((BitmapDrawable) pic2).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                TextSliderView textSliderView2 = new TextSliderView(this);
                textSliderView2
                        .description("Picture 2")
                        .image(mypath2);

                sliderShow.addSlider(textSliderView2);
            }
            if (pic3 != null)
            {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(mypath3);
                    ((BitmapDrawable) pic3).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                TextSliderView textSliderView3 = new TextSliderView(this);
                textSliderView3
                        .description("Picture 3")
                        .image(mypath3);

                sliderShow.addSlider(textSliderView3);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

}
