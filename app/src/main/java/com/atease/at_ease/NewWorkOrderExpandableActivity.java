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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.gc.materialdesign.views.ButtonFlat;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 10/4/2015.
 */
public class NewWorkOrderExpandableActivity extends Activity {
    SliderLayout sliderShow;

    ArrayList<String> image_description =new ArrayList<String>();
    ArrayList<String> image_list=new ArrayList<String>();
    ArrayList<Drawable> image_drawable=new ArrayList<Drawable>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_work_order_expandable);

        sliderShow = (SliderLayout) findViewById(R.id.slider);
        sliderShow.setPresetTransformer(SliderLayout.Transformer.FlipPage);
        sliderShow.stopAutoCycle();

        ParseUser currentUser = ParseUser.getCurrentUser();
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
            try {
                lives = currentUser.getParseObject("liveAt").fetchIfNeeded();
                address = lives.getParseObject("address").fetchIfNeeded();
                addressString = address.getString("street") + ", " +
                        address.getString("city") + ", " +
                        address.getString("state") + " " +
                        address.getString("zipcode");

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
            ParseObject building = null;
            ParseObject complex = null;
            try {
                building = lives.getParseObject("building").fetchIfNeeded();
                complex = building.getParseObject("complex").fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String locString = complex.getString("name") + " - " +
                    building.getString("name") + "-" +
                    lives.getString("name");
            tenantTextViewLocation.setText(locString);

            Log.d("At-Ease", currentUser.toString());
//            Log.d("At-Ease", currentUser.get("manager").toString());
            ParseUser manager = null;
            try {
                manager = lives.getParseUser("owner").fetchIfNeeded();
                Log.d("At-Ease", manager.getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            ParseQuery<ParseUser> query = ParseUser.getQuery();
//            query.whereEqualTo("objectId", currentUser.get("manager"));
//            query.findInBackground(new FindCallback<ParseUser>() {
//                public void done(List<ParseUser> objects, ParseException e) {
//                    if (e == null) {
//                        ParseUser manager = objects.get(0);
                        TextView managerTextViewName = (TextView) findViewById(R.id.new_work_order_manager_name);
                        TextView managerTextViewEmail = (TextView) findViewById(R.id.new_work_order_manager_email);
                        TextView managerTextViewNumber = (TextView) findViewById(R.id.new_work_order_manager_phone);
//                        TextView managerTextViewLocation = (TextView) findViewById(R.id.new_work_order_manager_location);

                        managerTextViewName.setText(manager.getString("firstName") + " " + manager.getString("lastName"));
                        managerTextViewEmail.setText(manager.getString("email"));
                        String phoneStringM = manager.getString("phone");
                        managerTextViewNumber.setText(phoneStringM.substring(0, 3) + "-"
                                + phoneStringM.substring(3, 6) + "-"
                                + phoneStringM.substring(6, 10));
//                        String addressStringM = manager.getString("address") + ", " +
//                                manager.getString("city") + ", " +
//                                manager.getString("state") + " " +
//                                manager.getString("zip");
//                        managerTextViewAddress.setText(addressStringM);
//                        managerTextViewLocation.setText(manager.getString("building") +
//                                " - " + manager.getString("room"));
//                    } else {
//                        Log.d("At-Ease", "Manager not found: " + e.getMessage());
//                    }
//                }
//            });
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

            String text = ((EditText) findViewById(R.id.new_work_order_text)).getText().toString();
            String subject = ((EditText) findViewById(R.id.new_work_order_subject)).getText().toString();
            workOrder.SetTextFromString(text);
            workOrder.SetSubject(subject);

            switch (image_drawable.size()) {
                case 3: workOrder.SetPic3FromDrawable(image_drawable.get(2));
                    Log.d("At-Ease", "Setting Pic 3");
                case 2: workOrder.SetPic2FromDrawable(image_drawable.get(1));
                    Log.d("At-Ease", "Setting Pic 2");
                case 1: workOrder.SetPic1FromDrawable(image_drawable.get(0));
                    Log.d("At-Ease", "Setting Pic 1");
                    break;
                default: break;
            }

            ParseObject liveAt = currentUser.getParseObject("liveAt").fetchIfNeeded();
            ParseUser manager = liveAt.getParseUser("owner").fetchIfNeeded();

            if (manager != null) {
                workOrder.SetManager(manager);
            }

            workOrder.SetTenant(currentUser);

            Log.d("MYAPPTAG", "Saving Parse Object from NewWorkOrderActivity");
            workOrder.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("Activity", "Callback Called 1");
                }
            });
            finish();
        } else {
            // show the signup or login screen
            Log.d("MYAPPTAG", "No Current User");
        }
    }

}
