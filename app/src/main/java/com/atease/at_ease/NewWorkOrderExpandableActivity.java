package com.atease.at_ease;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.gc.materialdesign.views.ButtonFlat;
import com.mikepenz.iconics.view.IconicsTextView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mark on 10/4/2015.
 */
public class NewWorkOrderExpandableActivity extends AppCompatActivity {
    SliderLayout sliderShow;
    Toolbar toolbar;
    ArrayList<String> image_description =new ArrayList<String>();
    ArrayList<String> image_list=new ArrayList<String>();
    ArrayList<Drawable> image_drawable=new ArrayList<Drawable>();
    ParseUser currentUser;
    ParseObject lives;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_work_order_expandable);

        //get the currentUser
        currentUser = ParseUser.getCurrentUser();
        AtEaseApplication application = (AtEaseApplication) getApplicationContext();
        final Drawer myDrawer = application.getNewDrawerBuilder(currentUser.getBoolean("isManager"),this).withActivity(this).build();

        //Toolbar stuff
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        title.setText("New Work Order");
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


        sliderShow = (SliderLayout) findViewById(R.id.slider);
        sliderShow.setPresetTransformer(SliderLayout.Transformer.FlipPage);
        sliderShow.stopAutoCycle();


        if (currentUser != null) {
            // do stuff with the user
            TextView tenantTextViewName = (TextView) findViewById(R.id.new_work_order_tenant_name);
            TextView tenantTextViewEmail = (TextView) findViewById(R.id.new_work_order_tenant_email);
            TextView tenantTextViewNumber = (TextView) findViewById(R.id.new_work_order_tenant_phone);
            TextView tenantTextViewAddress = (TextView) findViewById(R.id.new_work_order_tenant_address);
            TextView tenantTextViewLocation = (TextView) findViewById(R.id.new_work_order_tenant_location);

            lives = null;
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
                } else {
                    // User does not live anywhere currently, can not file a work order!
                    new MaterialDialog.Builder(this)
                            .title("User Access Error")
                            .content("Current user has no registered property. Therefore, a work order can not be filed!")
                            .positiveText("Back")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            })
                            .show();
                    finish();
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
//            Log.d("At-Ease", currentUser.get("manager").toString());
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

        ButtonFlat attachPicture= (ButtonFlat) findViewById(R.id.new_work_order_picture);
        attachPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            final Uri photoUri = data.getData();
            if (photoUri != null) {
                boolean wrapInScrollView = true;
                new MaterialDialog.Builder(this)
                        .title("Add Image")
                        .customView(R.layout.dialog_new_work_order, wrapInScrollView)
                        .positiveText("Add Picture")
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                return;
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View view = dialog.getCustomView();
                                image_description.add(((EditText) view.findViewById(R.id.new_work_order_picture_description)).getText().toString());
                                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                                Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String filePath = cursor.getString(columnIndex);
                                cursor.close();
                                Log.v("Load Image", "Gallery File Path=====>>>" + filePath);
                                image_list.add(filePath);
                                Log.v("Load Image", "Image List Size=====>>>" + image_list.size());

                                updateImageTable();
                                new GetImages().execute();
                            }
                        })
                        .show();
            }
        }

    }

    public void updateImageTable()
    {
        sliderShow.removeAllSliders();

        for(int i=0; i<image_drawable.size(); i++) {
            TextSliderView textSliderView = new TextSliderView(this);
            File file = new File(image_list.get(i));
            textSliderView.description(image_description.get(i))
                    .image(file);
            sliderShow.addSlider(textSliderView);
        }


    }

    public class GetImages extends AsyncTask<Void, Void, Void>
    {
        public ProgressDialog progDialog=null;

        protected void onPreExecute()
        {
            progDialog=ProgressDialog.show(NewWorkOrderExpandableActivity.this, "", "Loading...",true);
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            image_drawable.clear();
            for(int i=0; i<image_list.size(); i++)
            {
                Bitmap bitmap = BitmapFactory.decodeFile(image_list.get(i).toString().trim());
                bitmap = Bitmap.createScaledBitmap(bitmap,1500, 1500, true);
                Drawable d=loadImagefromurl(bitmap);

                image_drawable.add(d);
            }
            return null;
        }

        protected void onPostExecute(Void result)
        {
            if(progDialog.isShowing())
            {
                progDialog.dismiss();
            }
            updateImageTable();
        }
    }

    public Drawable loadImagefromurl(Bitmap icon)
    {
        Drawable d=new BitmapDrawable(icon);
        return d;
    }

    public void submitWorkOrder(View view) throws ParseException {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            WorkOrder workOrder = new WorkOrder();
            workOrder.setDeleted(false);
            workOrder.setManagerDone(false);
            workOrder.setTenantDone(false);

            String text = ((EditText) findViewById(R.id.new_work_order_text)).getText().toString();
            String subject = ((EditText) findViewById(R.id.new_work_order_subject)).getText().toString();
            workOrder.setTextFromString(text);
            workOrder.setSubject(subject);
            workOrder.put("propId", lives.getObjectId());

            switch (image_drawable.size()) {
                case 3: workOrder.setPic3FromDrawable(image_drawable.get(2));
                    Log.d("At-Ease", "Setting Pic 3");
                case 2: workOrder.setPic2FromDrawable(image_drawable.get(1));
                    Log.d("At-Ease", "Setting Pic 2");
                case 1: workOrder.setPic1FromDrawable(image_drawable.get(0));
                    Log.d("At-Ease", "Setting Pic 1");
                    break;
                default: break;
            }

            ParseObject liveAt = currentUser.getParseObject("liveAt").fetchIfNeeded();
            ParseUser manager = liveAt.getParseUser("owner");

            if (manager != null) {
                manager.fetchIfNeeded();
                workOrder.setManager(manager);
            }

            workOrder.setTenant(currentUser);

            Log.d("MYAPPTAG", "Saving Parse Object from NewWorkOrderActivity");
            workOrder.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    finish();
                    Log.d("Activity", "Callback Called 1");
                }
            });
            //finish();
        } else {
            // show the signup or login screen
            Log.d("MYAPPTAG", "No Current User");
        }
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
