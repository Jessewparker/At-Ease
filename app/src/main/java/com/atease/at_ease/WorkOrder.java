package com.atease.at_ease;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Mark on 10/4/2015.
 */
@ParseClassName("WorkOrder")
public class WorkOrder extends ParseObject implements Parcelable{
    public WorkOrder() {
    }

    protected WorkOrder(Parcel in) {
        setSubject(in.readString());
        byte[] bText = new byte[in.readInt()];
        in.readByteArray(bText);
        setText(new ParseFile("text.txt", bText));

    }

    public static final Creator<WorkOrder> CREATOR = new Creator<WorkOrder>() {
        @Override
        public WorkOrder createFromParcel(Parcel in) {
            return new WorkOrder(in);
        }

        @Override
        public WorkOrder[] newArray(int size) {
            return new WorkOrder[size];
        }
    };

    public String getSubject() {
        return getString("subject");
    }

    public void setSubject(String inSubject) {
        put("subject", inSubject);
    }

    public ParseFile getPic1() {
        return getParseFile("pic1");
    }

    public Drawable getPic1AsDrawable() throws ParseException {
        ParseFile pic1File = getPic1();
        if (pic1File != null) {
            byte[] b = pic1File.getData();
            ByteArrayInputStream is = new ByteArrayInputStream(b);
            Drawable pic1 = Drawable.createFromStream(is, "pic1.jpg");
            return pic1;
        }
        else {
            return null;
        }
    }

    public void setPic1(ParseFile inPic1) {
        put("pic1", inPic1);
    }

    public void setPic1FromDrawable(Drawable inPic1) {
            Bitmap bitmap = ((BitmapDrawable) inPic1).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] pic1 = stream.toByteArray();
            ParseFile pic1File = new ParseFile("pic1.jpg", pic1);
            setPic1(pic1File);
    }

    public ParseFile getPic2() {
        return getParseFile("pic2");
    }

    public Drawable getPic2AsDrawable() throws ParseException {
        ParseFile pic2File = getPic2();
        if (pic2File != null) {
            byte[] b = pic2File.getData();
            ByteArrayInputStream is = new ByteArrayInputStream(b);
            Drawable pic2 = Drawable.createFromStream(is, "pic2.jpg");
            return pic2;
        }
        else {
            return null;
        }
    }

    public void setPic2(ParseFile inPic2) {
        put("pic2", inPic2);
    }

    public void setPic2FromDrawable(Drawable inPic2) {
        Bitmap bitmap = ((BitmapDrawable) inPic2).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] pic2 = stream.toByteArray();
        ParseFile pic2File = new ParseFile("pic2.jpg", pic2);
        setPic2(pic2File);
    }

    public ParseFile getPic3() {
        return getParseFile("pic3");
    }

    public Drawable getPic3AsDrawable() throws ParseException {
        ParseFile pic3File = getPic3();
        if (pic3File != null) {
            byte[] b = pic3File.getData();
            ByteArrayInputStream is = new ByteArrayInputStream(b);
            Drawable pic3 = Drawable.createFromStream(is, "pic3.jpg");
            return pic3;
        }
        else {
            return null;
        }
    }

    public void setPic3(ParseFile inPic3) {
        put("pic3", inPic3);
    }

    public void setPic3FromDrawable(Drawable inPic3) {
        Bitmap bitmap = ((BitmapDrawable) inPic3).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] pic3 = stream.toByteArray();
        ParseFile pic3File = new ParseFile("pic3.jpg", pic3);
        setPic3(pic3File);
    }

    public ParseFile getText() {
        return getParseFile("text");
    }

    public String getTextAsString() throws ParseException {
        ParseFile textFile = getText();
        byte[] textBytes = textFile.getData();
        String text = new String(textBytes);
        Log.d("AT-EASE", "text after parse: " + text);
        return text;
    }

    public void setText(ParseFile inText) {
        put("text", inText);
    }

    public void setTextFromString(String inText) {
        Log.d("AT-EASE", "InText is: " + inText);
        byte[] b = inText.getBytes();
        String text = new String(b);
        Log.d("AT-EASE", "after conversion, text is: " + text);
        ParseFile textFile = new ParseFile("text.txt", b);
        setText(textFile);
    }

    public ParseUser getManager() {
        return getParseUser("manager");
    }

    public void setManager(ParseUser inManager) {
        put("manager", inManager);
    }

    public ParseUser getTenant() {
        return getParseUser("tenant");
    }

    public void setTenant(ParseUser inTenant) {
        put("tenant", inTenant);
    }

    public boolean isDeleted(){
        return getBoolean("isDeleted");
    }

    public void setDeleted(boolean inDeleted) {
        put("isDeleted", inDeleted);
    }

    public boolean isManagerDone(){
        return getBoolean("isManagerDone");
    }

    public void setManagerDone(boolean inManagerDone) {
        put("isManagerDone", inManagerDone);
    }

    public boolean isTenantDone(){
        return getBoolean("isTenantDone");
    }

    public void setTenantDone(boolean inTenantDone) {
        put("isTenantDone", inTenantDone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getSubject());
        try {
            byte[] bText = getText().getData();
            dest.writeInt(bText.length);
            dest.writeByteArray(bText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}