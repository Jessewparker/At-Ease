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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mark on 10/4/2015.
 */
public class NewWorkOrderExpandableActivity extends Activity {
    TableLayout image_table=null;
    LinearLayout layout;
    int numberOfPictures = 0;
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
            TextView textViewName = (TextView) findViewById(R.id.new_work_order_tenant_name);
            TextView textViewNumber = (TextView) findViewById(R.id.new_work_order_tenant_phone);

            textViewName.setText(currentUser.getUsername());
            textViewNumber.setText((currentUser.get("phone").toString()));
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

    public void submitWorkOrder(View view)
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            ParseObject workOrder = new ParseObject("WorkOrder");
            byte[] data = ((EditText) findViewById(R.id.new_work_order_text)).getText().toString().getBytes();
            ParseFile textFile = new ParseFile("text.txt", data);
            workOrder.put("text", textFile);

            String subject = ((EditText) findViewById(R.id.new_work_order_subject)).getText().toString();
            Log.d("At-Ease", "Subject: " + subject);
            workOrder.put("subject", subject);
//            Bitmap bitmap = ((BitmapDrawable) image_drawable.get(0)).getBitmap();
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            byte[] pic1 = stream.toByteArray();
//            ParseFile pic1File = new ParseFile("pic1.jpg", pic1);
//            workOrder.put("pic1", pic1File);
            ParseUser manager = currentUser.getParseUser("manager");
            if (manager != null) {
                workOrder.put("manager", manager);
            }
            workOrder.put("tenant", currentUser);
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
