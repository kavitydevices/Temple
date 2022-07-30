package com.apps.jaywalker.kaveri_b;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by PARTHI on 27-04-2016.
 */
public class Newjingbile {
    private Context mcont;
    SharedPreferences sharedPreferences;
    public static final String Samount = "Samt";
    private ProgressDialog pdialog;
    static String usrname = "",Url_s,Uloc;
    private DBHelper mydb;
    updater updt = null;

    //Constructor
    public Newjingbile(Context context, String usr,String Url_svr,String uloc,updater updt) {
        this.mcont = context;
        usrname = usr;
        Uloc =uloc;
        Url_s = Url_svr;
        mydb = new DBHelper(context);
        this.updt =updt;
    }

    //Syncing
    public void syncSQLiteMySQLDB() {
        final int res=0;
        //Create AsycHttpClient object
        pdialog = new ProgressDialog(mcont);
        pdialog.setMessage("Syncing with server. Please wait..");
        pdialog.setIndeterminate(false);
        pdialog.setCancelable(false);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        final Cursor c = mydb.getData(usrname);
        if(c.getCount()> 0) {
            pdialog.show();
            params.put("parmtr", getdatasql());
            //String url_submit1 = "http://192.168.0.14/Android_App/Parking/";
            //Main server
            //client.post("http://kd-apps.biz/epark/loaddata.php", params, new AsyncHttpResponseHandler(){
            //Demo server 1
            client.post("http://templeapi.obbe.in/loaddata.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(mcont,"Tickets Sync completed!", Toast.LENGTH_LONG).show();
                        mydb.deluser(usrname);
                        String syndate="Unvailabe";
                        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
                        syndate = dt.format(new Date());
                        updt.updatedsyncstatus(String.valueOf(c.getCount()),syndate);
                         pdialog.dismiss();

                    /*} catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(mcont, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }*/
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    pdialog.hide();
                    if(statusCode == 404){
                        Toast.makeText(mcont, "Tickets Sync:Requested resource not found", Toast.LENGTH_LONG).show();
                    }else if(statusCode == 500){
                        Toast.makeText(mcont, "Tickets Sync:Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(mcont, "Tickets Sync:Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        else{
            Toast.makeText(mcont, "Nothing to sync", Toast.LENGTH_LONG).show();
        }
    }


    public String getdatasql() {
        ArrayList<HashMap<String, String>> Argmets;
        String agrment = "";
        Cursor c = mydb.getData(usrname);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        if (c.getCount() > 0) {
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++)
            {
                //Log.e("Loop", Integer.toString(i));
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Usernm", c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_Userid)));
                map.put("Uloc", Uloc);
                map.put("Payment_method","online");
                map.put("Bill_Id", c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_ID)));
                map.put("Cmob", c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_Cmob)));
                map.put("Item", c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_Ptype)));
                map.put("Itemp", c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_Price)));
                map.put("Itemq", c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_Pqty)));
                map.put("ItemT", c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_TPrice)));
                map.put("Btime", c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_Time)));
                DateFormat formt = new SimpleDateFormat("dd/MM/yy");
                DateFormat newformt = new SimpleDateFormat("yy/MM/dd");
                DateFormat Tformatter = new SimpleDateFormat("hh:mm:ss");
                String datm = c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_Date));
                map.put("dnt", c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_Date))) ;
                try {
                    Date dd = formt.parse(datm);
                    //Date Dt = Tformatter.parse(c.getString(c.getColumnIndex(DBHelper.Bill_COLUMN_Time)));
                    String newdd= newformt.format(dd);
                    //String newdt= Tformatter.format(Dt);
                    map.put("dnt", newdd);
                    //map.put("Btime", newdt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wordList.add(map);
                c.moveToNext();
            }
            Gson gson = new GsonBuilder().create();
            //Use GSON to serialize Array List to JSON
            agrment = gson.toJson(wordList);
        }
        return agrment;
    }
}

