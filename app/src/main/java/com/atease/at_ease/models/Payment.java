package com.atease.at_ease.models;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jesse on 10/22/2015.
 */
@ParseClassName("Payment")
public class Payment extends ParseObject{

    public Payment(){}


    public ParseUser getManager(){
        return getParseUser("manager");
    }

    public String getManagerName(){
        ParseUser man = getParseUser("manager");
        try{
            man = man.fetchIfNeeded();
        }catch(ParseException e){
            Log.d("Payment", e.toString());
        }

        return man.getString("firstName") + " " + man.getString("lastName");
    }

    public String getTenantName(){
        ParseUser user = getParseUser("tenant");
        try{
            user = user.fetchIfNeeded();
        }catch(ParseException e){
            Log.d("Payment", e.toString());
        }

        return user.getString("firstName") + " " +user.getString("lastName");

    }

    public void setManager(ParseUser manager){
        put("manager",manager);
    }

    public ParseUser getTenant(){
        return getParseUser("tenant");
    }

    public void setTenant(ParseUser tenant){
        put("tenant", tenant);
    }

    public Property getProperty(){
        return (Property) getParseObject("property");
    }

    public void setProperty(Property property){
        put("property", property );
    }

    public String getAmount(){
        return Double.toString(getDouble("amount"));
    }

    public void setAmount(int val){
        put("amount", val);
    }

    public String getDate(){
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");
        return df.format(getCreatedAt());
        //return "July 10, 2015";
    }

}
