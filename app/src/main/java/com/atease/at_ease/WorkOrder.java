package com.atease.at_ease;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by Mark on 10/4/2015.
 */
@ParseClassName("WorkOrder")
public class WorkOrder extends ParseObject implements Parcelable{
    public WorkOrder() {
    }

    protected WorkOrder(Parcel in) {
        SetSubject(in.readString());
        byte[] bText = new byte[in.readInt()];
        in.readByteArray(bText);
        SetText(new ParseFile("text.txt", bText));

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

    public String GetSubject() {
        return getString("subject");
    }

    public void SetSubject(String inSubject) {
        put("subject", inSubject);
    }

    public ParseFile GetPic1() {
        return getParseFile("pic1");
    }

    public Drawable GetPic1AsDrawable() throws ParseException {
        byte[] b = GetPic1().getData();
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        Drawable pic1 = Drawable.createFromStream(is, "pic1.jpg");
        return pic1;
    }

    public void SetPic1(ParseFile inPic1) {
        put("pic1", inPic1);
    }

    public void SetPic1FromDrawable(Drawable inPic1) {
            Bitmap bitmap = ((BitmapDrawable) inPic1).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] pic1 = stream.toByteArray();
            ParseFile pic1File = new ParseFile("pic1.jpg", pic1);
            SetPic1(pic1File);
    }

    public ParseFile GetPic2() {
        return getParseFile("pic2");
    }

    public Drawable GetPic2AsDrawable() throws ParseException {
        ParseFile pic2File = GetPic2();
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

    public void SetPic2(ParseFile inPic2) {
        put("pic2", inPic2);
    }

    public void SetPic2FromDrawable(Drawable inPic2) {
        Bitmap bitmap = ((BitmapDrawable) inPic2).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] pic2 = stream.toByteArray();
        ParseFile pic2File = new ParseFile("pic2.jpg", pic2);
        SetPic2(pic2File);
    }

    public ParseFile GetPic3() {
        return getParseFile("pic3");
    }

    public Drawable GetPic3AsDrawable() throws ParseException {
        ParseFile pic3File = GetPic3();
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

    public void SetPic3(ParseFile inPic3) {
        put("pic3", inPic3);
    }

    public void SetPic3FromDrawable(Drawable inPic3) {
        Bitmap bitmap = ((BitmapDrawable) inPic3).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] pic3 = stream.toByteArray();
        ParseFile pic3File = new ParseFile("pic3.jpg", pic3);
        SetPic3(pic3File);
    }

    public ParseFile GetText() {
        return getParseFile("text");
    }

    public String GetTextAsString() throws ParseException {
        ParseFile textFile = GetText();
        String text = textFile.getData().toString();
        return text;
    }

    public void SetText(ParseFile inText) {
        put("text", inText);
    }

    public void SetTextFromString(String inText) {
        byte[] b = inText.getBytes();
        ParseFile textFile = new ParseFile("text.txt", b);
        SetText(textFile);
    }

    public ParseUser GetManager() {
        return getParseUser("manager");
    }

    public void SetManager(ParseUser inManager) {
        put("manager", inManager);
    }

    public ParseUser GetTenant() {
        return getParseUser("tenant");
    }

    public void SetTenant(ParseUser inTenant) {
        put("tenant", inTenant);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(GetSubject());
        try {
            byte[] bText = GetText().getData();
            dest.writeInt(bText.length);
            dest.writeByteArray(bText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
//public class WorkOrder {
//    private String _name;
//    private String _number;
//    private String _date;
//    private String _subject;
//    private String _text;
//    private Drawable _pic1;
//    private Drawable _pic2;
//    private Drawable _pic3;
//
//    public WorkOrder(ParseObject parseObject, ParseUser user) {
//        Log.i("AT-EASE", "Creating work order with parse object");
//        String fname = user.getString("firstName");
//        String lname = user.getString("lastName");
//        if (fname != null && lname != null) {
//            _name = fname + " " + lname;
//        }
//
//        _number = user.getString("phone");
//
//        Date parseDate = parseObject.getCreatedAt();
//        if (parseDate != null) {
//            _date = parseDate.toString();
//        }
//
//        String subject = parseObject.getString("subject");
//        if (subject != null) {
//            _subject = subject;
//        }
//
//        ParseFile textFile = (ParseFile) parseObject.get("text");
//        if (textFile != null) {
//            textFile.getDataInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] bytes, ParseException e) {
//                    if (e == null) {
//                        _text = bytes.toString();
//                    } else {
//                        Log.d("At-Ease", "Error retrieving text: " + e.getMessage());
//                    }
//                }
//            });
//        }
//
//        ParseFile pic1File = (ParseFile) parseObject.get("pic1");
//        if (pic1File != null) {
//            pic1File.getDataInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] bytes, ParseException e) {
//                    if (e == null) {
//                        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
//                        _pic1 = Drawable.createFromStream(is, null);
//                    } else {
//                        Log.d("At-Ease", "Error retrieving pic1: " + e.getMessage());
//                    }
//                }
//            });
//        }
//
//        ParseFile pic2File = (ParseFile) parseObject.get("pic2");
//        if (pic2File != null) {
//            pic2File.getDataInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] bytes, ParseException e) {
//                    if (e == null) {
//                        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
//                        _pic2 = Drawable.createFromStream(is, null);
//                    } else {
//                        Log.d("At-Ease", "Error retrieving pic2: " + e.getMessage());
//                    }
//                }
//            });
//        }
//
//        ParseFile pic3File = (ParseFile) parseObject.get("pic3");
//        if (pic3File != null) {
//            pic3File.getDataInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] bytes, ParseException e) {
//                    if (e == null) {
//                        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
//                        _pic3 = Drawable.createFromStream(is, null);
//                    } else {
//                        Log.d("At-Ease", "Error retrieving pic3: " + e.getMessage());
//                    }
//                }
//            });
//        }
//        Log.d("At-Ease", "Returning from work order constructer");
//    }
//
//    public WorkOrder(String _name, String _number) {
//        this._name = _name;
//        this._number = _number;
//    }
//
//    public Drawable get_pic3() {
//        return _pic3;
//    }
//
//    public void set_pic3(Drawable _pic3) {
//        this._pic3 = _pic3;
//    }
//
//    public Drawable get_pic2() {
//        return _pic2;
//    }
//
//    public void set_pic2(Drawable _pic2) {
//        this._pic2 = _pic2;
//    }
//
//    public Drawable get_pic1() {
//        return _pic1;
//    }
//
//    public void set_pic1(Drawable _pic1) {
//        this._pic1 = _pic1;
//    }
//
//    public String get_text() {
//        return _text;
//    }
//
//    public void set_text(String _text) {
//        this._text = _text;
//    }
//
//    public String get_number() {
//        return _number;
//    }
//
//    public void set_number(String _number) {
//        this._number = _number;
//    }
//
//    public String get_name() {
//        return _name;
//    }
//
//    public void set_name(String _name) {
//        this._name = _name;
//    }
//
//    public String get_subject() {
//        return _subject;
//    }
//
//    public void set_subject(String _subject) {
//        this._subject = _subject;
//    }
//
//    public String get_date() {
//        return _date;
//    }
//
//    public void set_date(String _date) {
//        this._date = _date;
//    }
//}
