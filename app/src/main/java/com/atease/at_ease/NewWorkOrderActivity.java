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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Mark on 10/2/2015.
 */
public class NewWorkOrderActivity extends Activity{
    TableLayout image_table=null;
    LinearLayout layout;
    int numberOfPictures = 0;

    ArrayList<String> image_list=new ArrayList<String>();
    ArrayList<Drawable> image_drawable=new ArrayList<Drawable>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_work_order);
        layout = (LinearLayout) findViewById(R.id.linear);
        ParseUser.logInInBackground("gusguyman", "drowssap", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                } else {
                    Log.d("MYAPPTAG", "User Log-in Failed");
                }
            }
        });
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            TextView textViewName = (TextView) findViewById(R.id.new_work_order_name);
            TextView textViewNumber = (TextView) findViewById(R.id.new_work_order_number);

            textViewName.setText(currentUser.getUsername());
            textViewNumber.setText((currentUser.get("phone").toString()));
        } else {
            // show the signup or login screen
            Log.d("MYAPPTAG", "No Current User");
        }

        Button attachPicture= (Button) findViewById(R.id.new_work_order_picture);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
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
        }

    }

    public void updateImageTable()
    {

        layout.removeAllViews();

        for(int i=0; i<image_drawable.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setPadding(2, 2, 2, 2);
//            imageView.setImageBitmap(BitmapFactory.decodeResource(
//                    getResources(), R.drawable.ic_launcher));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundDrawable(image_drawable.get(i));
            layout.addView(imageView);
        }


    }

    public class GetImages extends AsyncTask<Void, Void, Void>
    {
        public ProgressDialog progDialog=null;

        protected void onPreExecute()
        {
            progDialog=ProgressDialog.show(NewWorkOrderActivity.this, "", "Loading...",true);
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
            Bitmap bitmap = ((BitmapDrawable) image_drawable.get(0)).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] pic1 = stream.toByteArray();
            workOrder.put("pic1", pic1);
            workOrder.put("tenant", currentUser);
            workOrder.saveInBackground();
        } else {
            // show the signup or login screen
            Log.d("MYAPPTAG", "No Current User");
        }
    }

}
