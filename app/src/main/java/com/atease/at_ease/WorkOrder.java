package com.atease.at_ease;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayInputStream;
import java.util.Date;

/**
 * Created by Mark on 10/4/2015.
 */
public class WorkOrder {
    private String _name;
    private String _number;
    private String _date;
    private String _subject;
    private String _text;
    private Drawable _pic1;
    private Drawable _pic2;
    private Drawable _pic3;

    public WorkOrder(ParseObject parseObject, ParseUser user) {
        Log.i("AT-EASE", "Creating work order with parse object");
        String fname = user.getString("firstName");
        String lname = user.getString("lastName");
        if (fname != null && lname != null) {
            _name = fname + " " + lname;
        }

        _number = user.getString("phone");

        Date parseDate = parseObject.getCreatedAt();
        if (parseDate != null) {
            _date = parseDate.toString();
        }

        String subject = parseObject.getString("subject");
        if (subject != null) {
            _subject = subject;
        }

        ParseFile textFile = (ParseFile) parseObject.get("text");
        if (textFile != null) {
            textFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        _text = bytes.toString();
                    } else {
                        Log.d("At-Ease", "Error retrieving text: " + e.getMessage());
                    }
                }
            });
        }

        ParseFile pic1File = (ParseFile) parseObject.get("pic1");
        if (pic1File != null) {
            pic1File.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                        _pic1 = Drawable.createFromStream(is, null);
                    } else {
                        Log.d("At-Ease", "Error retrieving pic1: " + e.getMessage());
                    }
                }
            });
        }

        ParseFile pic2File = (ParseFile) parseObject.get("pic2");
        if (pic2File != null) {
            pic2File.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                        _pic2 = Drawable.createFromStream(is, null);
                    } else {
                        Log.d("At-Ease", "Error retrieving pic2: " + e.getMessage());
                    }
                }
            });
        }

        ParseFile pic3File = (ParseFile) parseObject.get("pic3");
        if (pic3File != null) {
            pic3File.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                        _pic3 = Drawable.createFromStream(is, null);
                    } else {
                        Log.d("At-Ease", "Error retrieving pic3: " + e.getMessage());
                    }
                }
            });
        }
        Log.d("At-Ease", "Returning from work order constructer");
    }

    public WorkOrder(String _name, String _number) {
        this._name = _name;
        this._number = _number;
    }

    public Drawable get_pic3() {
        return _pic3;
    }

    public void set_pic3(Drawable _pic3) {
        this._pic3 = _pic3;
    }

    public Drawable get_pic2() {
        return _pic2;
    }

    public void set_pic2(Drawable _pic2) {
        this._pic2 = _pic2;
    }

    public Drawable get_pic1() {
        return _pic1;
    }

    public void set_pic1(Drawable _pic1) {
        this._pic1 = _pic1;
    }

    public String get_text() {
        return _text;
    }

    public void set_text(String _text) {
        this._text = _text;
    }

    public String get_number() {
        return _number;
    }

    public void set_number(String _number) {
        this._number = _number;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_subject() {
        return _subject;
    }

    public void set_subject(String _subject) {
        this._subject = _subject;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }
}
