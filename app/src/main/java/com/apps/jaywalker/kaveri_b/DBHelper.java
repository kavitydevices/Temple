package com.apps.jaywalker.kaveri_b;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Home on 4/5/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Kaveri.db";
    public static final String UsrAc_TABLE_NAME = "UsrAC";
    public static final String UsrAC_COLUMN_ID = "Userid";
    public static final String UsrAC_COLUMN_Uname = "username";
    public static final String UsrAC_COLUMN_PWD = "password";
    public static final String UsrAC_COLUMN_Role = "role";
    public static final String UsrAC_COLUMN_Status = "status";
    public static final String Bill_TABLE_NAME = "Bill";
    public static final String Bill_COLUMN_ID = "Bill_ID";
    public static final String Bill_COLUMN_Cmob = "Cust_no";
    public static final String Bill_COLUMN_Ptype = "Pname_type";
    public static final String Bill_COLUMN_Pqty = "Pqty";
    public static final String Bill_COLUMN_Price = "Price";
    public static final String Bill_COLUMN_TPrice = "TPrice";
    public static final String Bill_COLUMN_Date = "Pdate";
    public static final String Bill_COLUMN_Time = "PTime";
    public static final String Bill_COLUMN_Userid = "Userid";
    public static final String Park_COLUMN_userd = "userID";
    public static final String Autog_COLUMN_TB = "TBname";
    public static final String Autog_COLUMN_RC = "Rcount";
    public static final String Autog_COLUMN_USR = "userID";
    public Context cont;
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        cont = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table UsrAC " +
                "(Userid integer primary key, username text,password text,role text," +
                "status text)");
        db.execSQL("create table " + Bill_TABLE_NAME +
                "(" + Bill_COLUMN_ID+ " INTERGER," + Bill_COLUMN_Cmob + " Text," + Bill_COLUMN_Ptype + " Text," + Bill_COLUMN_Pqty + " Text,"
                + Bill_COLUMN_Price + " Text," +Bill_COLUMN_TPrice + " Text," + Bill_COLUMN_Date + " Text," + Bill_COLUMN_Time + " Text," + Bill_COLUMN_Userid + " Text)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //db.execSQL("DROP TABLE IF EXISTS UsrAC");
        //db.execSQL("DROP TABLE IF EXISTS parking");
        //onCreate(db);
    }

    public boolean insertBill(int Bill_ID, String Cst_mob, String Ptype,String qty,String Price,String Tamt, String Pdate, String Ptime,String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Bill_COLUMN_ID, Bill_ID);
        contentValues.put(Bill_COLUMN_Cmob, Cst_mob);
        contentValues.put(Bill_COLUMN_Ptype, Ptype);
        contentValues.put(Bill_COLUMN_Price, Price);
        contentValues.put(Bill_COLUMN_Pqty, qty);
        contentValues.put(Bill_COLUMN_TPrice,Tamt);
        contentValues.put(Bill_COLUMN_Date, Pdate);
        contentValues.put(Bill_COLUMN_Time, Ptime);
        contentValues.put(Bill_COLUMN_Userid, userid);
        db.insert(Bill_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(String userid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + Bill_TABLE_NAME+" where " + Bill_COLUMN_Userid + "='" + userid
                + "'", null);
        return res;
    }
    public int getBillID(String userid)
    {
        int val = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor res = database.rawQuery("select max(" + Bill_COLUMN_ID +") from " + Bill_TABLE_NAME+" where " + Bill_COLUMN_Userid + "='" + userid.toLowerCase()
                + "'", null);
        res.moveToFirst();
        val = res.getInt(0);
        return val+ 1;
    }
    public void deluser(String usernm)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Bill_TABLE_NAME,null,null);
    }

}


