package com.apps.jaywalker.kaveri_b;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonNull;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements updater {

    LinearLayout ly1,ly2,ly3,ly4;
    Button btn;
    GridView lst;
    EditText editText;
    TextView Retry_txt,T_no,T_amt,P_nm,P_chg,S_no,S_dt,U_nm,U_lg;
    String[] array_m, array_r;
    String mobval, itemval,itemrate, URL_Serv,Usrnam,uloc,Uname;
    String logdate;
    ProgressDialog pd;
    JSONObject jsonObject;
    int billID =0;
    ImageView syncBtn,exitbtn;
    DBHelper dbHelper;//dbhelper class
    //Printer setup
    public static int MAX_LINE_CHARS = 16;//32
    ArrayList<String> printPaperStringArray = new ArrayList<>();
    public static int MAX_PAPERNAME_CHARS = 11;
    public static int MAX_QTY_CHARS = 3;
    public static int MAX_AMT_CHARS = 5;
    private static BluetoothSocket btsocket;
    private static OutputStream btoutputstream;
    private static InputStream btinputstream;
    public static final String MYpref = "Mypref";
    public static final String userLoged = "usrlog";
    public static final String userLoc = "usrloc";
    public static final String txtno = "not";
    public static final String bamt = "bamt";
    public static final String prntr = "PRINTER";
    public static final String sync_nb = "SYNCN";
    public static final String sync_dt = "SYNCD";
    public static final String FIRSTR = "FRUN";
    public static final String userName = "usrname";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializer();
        loadmenu();//Fetching Menu from server
        Listeners();


        findViewById(R.id.santarpana).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(MainActivity.this, Annasantarpana.class);
                startActivity(intent);*/

                Intent intent = new Intent(MainActivity.this, Annasantarpana.class);
                intent.putExtra("itemn", array_m);
                intent.putExtra("itemr", array_r);
                intent.putExtra("Billid", billID);
                intent.putExtra("mobval", mobval);
                intent.putExtra("uloc", uloc);
                intent.putExtra("usrname", Usrnam);
                intent.putExtra("printer", sharedPreferences.getString(prntr,"NONE"));
                MainActivity.this.startActivity(intent);
            }
        });

    }

    protected void connect() {
        if (btsocket == null) {
            if (Build.VERSION.SDK_INT < 23) {
                Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
                this.startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);
            } else {
                int permissioncheck = checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
                // permissioncheck += checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
                if (permissioncheck != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                } else {
                    Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
                    this.startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);
                }
            }
        } else {

            OutputStream opstream = null;
            InputStream Instream = null;
            try {
                opstream = btsocket.getOutputStream();
                Instream = btsocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btoutputstream = opstream;
            btinputstream = Instream;
            print_bt();
        }

    }

    private int print_bt() {
        byte[] nomalchar = new byte[]{0x1B,0x21,0X08};
        byte[] bigchar = new byte[]{0x1B,0x21,0X28};
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Date today = new Date();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat Tformatter = new SimpleDateFormat("hh:mm:ss");
            String todayStr = formatter.format(today);
            String timestr = Tformatter.format(today);
            btoutputstream = btsocket.getOutputStream();
            /*HEADING START */
            String agency_name,agency_address1,agency_address2;
            if(uloc.equals("1") && uloc.equals("3"))
            {
                 agency_name = "Tala kaveri - Reciept";
                 agency_address1 = "Bhagamandala";
                 agency_address2 = "Kodagu";
            }
            else
            {
                 agency_name = "Bhagandeshwara Temple - Reciept";
                 agency_address1 = "Bhagamandala";
                 agency_address2 = "Kodagu";
            }

            String agency_name_formatted_to_printer = format_string_to_center_for_printer(agency_name);
            String agency_address1_formatted_to_printer = format_string_to_center_for_printer(agency_address1);
            String agency_address2_formatted_to_printer = format_string_to_center_for_printer(agency_address2);
            String datedStr = "Date:" + todayStr;
            String custStr = "Mobile #: " + mobval;
            String itemns = itemval + " : Rs " +itemrate;
            String datedStr_formatted_to_printer = format_string_for_print(datedStr);
            String custStr_formatted_to_printer = format_string_for_print(custStr);
            String formated_items = format_string_for_print(itemns);
            Double totamt = Double.parseDouble("00");
            /* DATA HEADER START */
            String detailHeading = "ITEM        QTY   AMT  ";
            String detailHeading_formatted_to_printer = format_string_for_print(detailHeading);
            /* DATA HEADER END */
            /* UTIL START */
            String dashedLine_formatted_to_printer = format_dashed_line_for_printer();
            String blankLine_formatted_to_printer = format_blank_line_for_printer();
            /* UTIL END */
            //Double customerCredit = dbHandler.getCustomerCredit(Custmob);
            /*PRINT TOTAL START */
            String totalLine_formatted_to_printer = format_total_line_for_printer();
            /*PRINT TOTAL END */
            /*THANK YOU START */
            String thank_you = "Thank you!! Visit Again!!";
            String thank_you_formatted_to_printer = format_string_to_center_for_printer(thank_you);
            /*THANK YOU END */
            /*START SENDING DATA TO PRINTER*/

            btoutputstream.write(nomalchar, 0, nomalchar.length);
            //btoutputstream.write(aligncenter(),0,aligncenter().length);
            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(agency_name_formatted_to_printer.getBytes(), 0, agency_name_formatted_to_printer.getBytes().length);
            btoutputstream.write(agency_address1_formatted_to_printer.getBytes(), 0, agency_address1_formatted_to_printer.getBytes().length);
            btoutputstream.write(agency_address2_formatted_to_printer.getBytes(), 0, agency_address2_formatted_to_printer.getBytes().length);
            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);

            btoutputstream.write(custStr_formatted_to_printer.getBytes(), 0, custStr_formatted_to_printer.getBytes().length);



            btoutputstream.write(bigchar, 0, bigchar.length);
            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write(datedStr_formatted_to_printer.getBytes(), 0, datedStr_formatted_to_printer.getBytes().length);
            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write(formated_items.getBytes(), 0, formated_items.getBytes().length);
            btoutputstream.write(nomalchar, 0, nomalchar.length);
            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(thank_you_formatted_to_printer.getBytes(), 0, thank_you_formatted_to_printer.getBytes().length);
            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);

            btoutputstream.flush();

            String msg = agency_name + " - Bill successfully Generated for " + itemval + ", Bill amount - Rs" + itemrate + ". Thank you!! Visit again";
            //sendsms(mobval, msg);
            ly1.setVisibility(View.GONE);
            ly2.setVisibility(View.VISIBLE);
            //dbHelper.insertBill(billID,mobval,itemval,itemrate,todayStr,timestr,Usrnam);
            //billID = dbHelper.getBillID(Usrnam);
            // totalPrice = 0.0;
            return 1;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected String format_string_to_center_for_printer(String printLine) {
        int lineCharCount = printLine.length();
        int leftOver = MAX_LINE_CHARS - lineCharCount;

        String retVal = "";
        if (leftOver > 0) {
            int startPadding = leftOver / 2;
            int endPadding = MAX_LINE_CHARS - startPadding - lineCharCount;
            for (int i = 0; i < startPadding; i++) {
                retVal += " ";
            }
            retVal += printLine;
            for (int j = 0; j < endPadding; j++) {
                retVal += " ";
            }
            return retVal;
        } else {
            retVal += printLine;

            return retVal;
        }

    }

    protected String format_string_for_print(String stringToPrint) {
        String retVal = "";
        int printCount = stringToPrint.length();
        if (printCount > MAX_LINE_CHARS) {
            double divider = Math.ceil(printCount / MAX_LINE_CHARS);
            int divInt = (int) divider;
            for (int i = 0; i < divInt; i++) {
                for (int j = 0; j < MAX_LINE_CHARS; j++) {
                    retVal += stringToPrint.charAt((MAX_LINE_CHARS * i) + j);
                }
            }
        } else {
            int leftOver = MAX_LINE_CHARS - printCount;
            retVal += stringToPrint;
            for (int i = 0; i < leftOver; i++) {
                retVal += " ";
            }
        }

        return retVal;
    }

    protected String format_dashed_line_for_printer() {
        String retVal = "";
        for (int i = 0; i < MAX_LINE_CHARS; i++) {
            retVal += "-";
        }

        return retVal;
    }

    protected String format_blank_line_for_printer() {
        String retVal = "";
        for (int i = 0; i < MAX_LINE_CHARS; i++) {
            retVal += " ";
        }

        return retVal;
    }

    protected String format_total_line_for_printer() {
        String retVal = "";

        String totalString = "TOTAL";
        String totalPrc = itemrate;

        int totalStringLength = totalString.length();
        int totalPrcLength = totalPrc.length();

        int leftOver = MAX_LINE_CHARS - totalStringLength - totalPrcLength;

        retVal += totalString;
        for (int i = 0; i < leftOver; i++) {
            retVal += " ";
        }
        retVal += totalPrc;
        return retVal;
    }

    protected String format_cash_revd_line_for_printer(Double cash) {
        String retVal = "";

        String cashString = "Cash Received";
        String cashPrc = cash.toString();

        int cashStringLength = cashString.length();
        int cashPrcLength = cashPrc.length();

        int leftOver = MAX_LINE_CHARS - cashStringLength - cashPrcLength;

        retVal += cashString;
        for (int i = 0; i < leftOver; i++) {
            retVal += " ";
        }
        retVal += cashPrc;

        return retVal;
    }

    protected String format_bal_line_for_printer(Double bal) {
        String retVal = "";

        String balString = "Balance";
        String balPrc = bal.toString();

        int balStringLength = balString.length();
        int balPrcLength = balPrc.length();

        int leftOver = MAX_LINE_CHARS - balStringLength - balPrcLength;

        retVal += balString;
        for (int i = 0; i < leftOver; i++) {
            retVal += " ";
        }
        retVal += balPrc;

        return retVal;
    }

    protected String format_credit_string_for_printer(String paperName, String Qty, Double Amt) {
        String retVal = "";

        int paperNameCharCount = paperName.length();

        int qtyStringCharCount = Qty.length();

        String amtStr = Amt.toString();
        int amtStringCharCount = amtStr.length();


        if (paperNameCharCount > MAX_PAPERNAME_CHARS) {
            int charCount;
            String sub1 = paperName.substring(0, MAX_PAPERNAME_CHARS);
            String sub2 = paperName.substring(MAX_PAPERNAME_CHARS, paperNameCharCount);
            retVal += sub1;
            retVal += "  ";
            int leftOver2 = MAX_QTY_CHARS - qtyStringCharCount;
            for (int i = 0; i < leftOver2; i++) {
                retVal += " ";
            }
            retVal += Qty;
            retVal += "  ";
            int leftOver3 = MAX_AMT_CHARS + 1 - amtStringCharCount;
            for (int i = 0; i < leftOver3; i++) {
                retVal += " ";
            }
            retVal += amtStr;

            charCount = retVal.length();
            if (charCount < MAX_LINE_CHARS) {
                int leftChars = MAX_LINE_CHARS - charCount;
                for (int i = 0; i < leftChars; i++) {
                    retVal += " ";
                }
            }
            retVal += sub2;
            int leftOverPaper = MAX_LINE_CHARS - sub2.length();
            for (int i = 0; i < leftOverPaper; i++) {
                retVal += " ";
            }
        } else {
            int leftOver = MAX_PAPERNAME_CHARS - paperNameCharCount;
            retVal += paperName;
            for (int i = 0; i < leftOver; i++) {
                retVal += " ";
            }
            retVal += "  ";
            int leftOver2 = MAX_QTY_CHARS - qtyStringCharCount;
            for (int i = 0; i < leftOver2; i++) {
                retVal += " ";
            }
            retVal += Qty;

            retVal += "  ";
            int leftOver3 = MAX_AMT_CHARS + 1 - amtStringCharCount;
            for (int i = 0; i < leftOver3; i++) {
                retVal += " ";
            }
            retVal += amtStr;
        }
        return retVal;
    }

    private void sendsms(final String mob, String messg) {
        String msg = messg;
        String mobilenb = mob;
        String url = "http://103.1.114.60/api.php?username=viswas&password=963779&sender=KDAPPS&sendto=" + mobilenb + "&message=" + msg + "";
        try {
            String Eurl = URLEncoder.encode(url, "UTF-8");
            AsyncHttpClient client = new AsyncHttpClient();
            //OTPalert(mob);
            client.get(Eurl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(MainActivity.this, "Sms sent successfully", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(MainActivity.this, "Something went wrong!!", Toast.LENGTH_LONG).show();
                    if (statusCode == 404) {
                        Log.e("Msg:", "Requested resource not found");
                    } else if (statusCode == 500) {
                        Log.e("Msg:", "internal error");
                    } else {
                        Log.e("Msg:", "Device not found " + statusCode);
                    }

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (Exception e)
        {

        }
       // finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BTDeviceList.REQUEST_CONNECT_BT) {
            try {
                btsocket = BTDeviceList.getSocket();
                if (btsocket != null) {
                    print_bt();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == 222 && resultCode == RESULT_OK)
        {
            int billcount=0,billamt=0, newbillamt=0;
            billcount = Integer.valueOf(sharedPreferences.getString(txtno,"0"));
            billamt = Integer.valueOf(sharedPreferences.getString(bamt,"0"));
            newbillamt =Integer.valueOf(data.getStringExtra("billamt"));
            billID =data.getIntExtra("billid",0);
            billcount =billcount + data.getIntExtra("bcount",0);;
            billamt = billamt + newbillamt;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(txtno,String.valueOf(billcount));
            editor.putString(bamt,String.valueOf(billamt));
            editor.commit();
            T_no.setText(sharedPreferences.getString(txtno,"0"));
            T_amt.setText(" | " + getResources().getString(R.string.Rup) + " " + sharedPreferences.getString(bamt, "0"));
            editText.getText().clear();
        }
        else if(requestCode == 222  && resultCode == RESULT_CANCELED)
        {
            Toast.makeText(MainActivity.this,"Cancelled by the user",Toast.LENGTH_LONG).show();
        }
    }

    public void loadmenu() {
        class getmenu extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("KD APPS _ BILLING POS");
                pd.setMessage("Fetching Info");
                pd.setCancelable(false);
                pd.setIndeterminate(false);
                pd.show();

            }

            @Override
            protected JSONObject doInBackground(String... strings) {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("uloc", uloc));
                jsonObject = JPARSER.makeRequest(URL_Serv + "getc.php", "POST", params1);
                return jsonObject;
            }

            protected void onPostExecute(JSONObject val) {
                JSONArray name_JA,rate_JA;

                try {
                    name_JA = val.getJSONArray("INM");
                    rate_JA = val.getJSONArray("IRT");
                    array_m = new String[name_JA.length()];
                    array_r = new String[rate_JA.length()];
                    for (int i = 0; i < name_JA.length(); i++) {
                        Log.e("length", ":" + name_JA.length());
                        String titletxt = name_JA.getJSONObject(i).getString("Inm");
                        String infotxt = rate_JA.getJSONObject(i).getString("Irt");
                        array_m[i] = titletxt.toString();
                        array_r[i] = infotxt.toString();
                        //lst.setAdapter(new Menu_adapter(MainActivity.this, array_m,array_r));
                    }
                    billID = dbHelper.getBillID(Usrnam);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(FIRSTR,"1");
                    ly4.setVisibility(View.VISIBLE);
                    ly3.setVisibility(View.GONE);
                }
                catch (Exception ex)
                {
                    ex.getMessage();
                    //Toast.makeText(MainActivity.this,"No internet connectivity!!Try again!!",Toast.LENGTH_LONG).show();
                    ly4.setVisibility(View.VISIBLE);
                    ly3.setVisibility(View.GONE);
                }
              /*  catch (NullPointerException  ex)
                {
                    ex.getMessage();
                    ly4.setVisibility(View.GONE);
                    ly3.setVisibility(View.VISIBLE);
                }*/
                pd.dismiss();
            }
        }
        if(sharedPreferences.getString(FIRSTR,"0").equals("0")) {
            getmenu gt = new getmenu();
            gt.execute();
        }
    }
    public void Sync() {
       Newjingbile newjingbile = new Newjingbile(MainActivity.this,Usrnam,URL_Serv,uloc,MainActivity.this);
       newjingbile.syncSQLiteMySQLDB();
    }
    private int Dprint_bt() {

        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Date today = new Date();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat Tformatter = new SimpleDateFormat("hh/mm/ss");
            String todayStr = formatter.format(today);
            String timestr = Tformatter.format(today);

            //sendsms(mobval, msg);
            ly1.setVisibility(View.GONE);
            ly2.setVisibility(View.VISIBLE);
            //dbHelper.insertBill(billID,mobval,itemval,itemrate,todayStr,timestr,Usrnam);
            billID = dbHelper.getBillID(Usrnam);
            // totalPrice = 0.0;
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public void sync(View v)
    {
        Sync();
    }
    public void logout(View v)
    {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("KD APPS - Logging out");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_ly);
        Button ybtn, cbtn;
        TextView msg;
        msg = dialog.findViewById(R.id.msg);
        msg.setText("Tap on yes to logout!");
        ybtn = dialog.findViewById(R.id.kbtn);
        cbtn = dialog.findViewById(R.id.Nbtn);
        dialog.show();
        ybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(userLoged,"NONE");
                editor.putString(userLoc,"NONE");
                editor.putString(txtno,"0");
                editor.putString(bamt, "0");
                editor.putString(prntr, "NONE");
                editor.putString(sync_nb, "0");
                editor.putString(sync_dt, "Unavailable");
                editor.putString(FIRSTR,"0");
                editor.commit();
               finish();

            }
        });
        cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int gr : grantResults) {
                        // Check if request is granted or not
                        if (gr != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }

                    Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
                    this.startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);

                }
                break;
            default:
                return;
        }
    }
    private byte[] aligncenter()
    {
        byte[] formatter = new byte[]{0X1B,0X61,0X01};
        return formatter;
    }
    private void initializer()
    {
        ly1 = (LinearLayout) findViewById(R.id.ly_menu);
        ly2 = (LinearLayout) findViewById(R.id.imglay);
        ly3 = (LinearLayout) findViewById(R.id.no_internet);
        ly4 = (LinearLayout) findViewById(R.id.main_ly);
        btn = (Button) findViewById(R.id.nxtbtn);
        lst = (GridView) findViewById(R.id.Mlist);
        editText = (EditText) findViewById(R.id.mobnbr);
        Retry_txt = (TextView) findViewById(R.id.retry_txt);
        Uname = "NONE | ";
        Intent intent = getIntent();
        Usrnam = intent.getStringExtra("uname");
        uloc = intent.getStringExtra("Loc");
        Uname = intent.getStringExtra("usr");
        //URL_Serv = "http://kd-apps.biz/kpos1/";//Server
        URL_Serv="http://templeapi.obbe.in/";
        sharedPreferences = getSharedPreferences(MYpref, Context.MODE_PRIVATE);
        ly1.setVisibility(View.GONE);
        dbHelper = new DBHelper(this);
        T_no = findViewById(R.id.B_No);
        T_amt = findViewById(R.id.B_Amt);
        P_nm = findViewById(R.id.pnt_nm);
        P_chg = findViewById(R.id.pnt_chg);
        S_no = findViewById(R.id.sync_no);
        S_dt = findViewById(R.id.sync_dt);
        U_nm = findViewById(R.id.lg_nm);
        //Inital values
        T_no.setText(sharedPreferences.getString(txtno,"0"));
        T_amt.setText(" | " + getResources().getString(R.string.Rup) + " " + sharedPreferences.getString(bamt, "0"));
        P_nm.setText(sharedPreferences.getString(prntr,"NONE") + " | ");
        S_no.setText(sharedPreferences.getString(sync_nb,"0"));
        S_dt.setText(" | " + sharedPreferences.getString(sync_dt,"Unavailable"));
        U_nm.setText(sharedPreferences.getString(userName,"Unavailable") + " | ");
    }
    private void Listeners()
    {

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(!sharedPreferences.getString(prntr,"NONE").equals("NONE")) {
                        mobval = editText.getText().toString();
                   /* ly1.setVisibility(View.VISIBLE);
                    ly2.setVisibility(View.GONE);
                    View view1 = MainActivity.this.getCurrentFocus();
                    if ((view1 != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }*/
                        Intent intent = new Intent(MainActivity.this, Bill_act.class);
                        intent.putExtra("itemn", array_m);
                        intent.putExtra("itemr", array_r);
                        intent.putExtra("Billid", billID);
                        intent.putExtra("mobval", mobval);
                        intent.putExtra("uloc", uloc);
                        intent.putExtra("usrname", Usrnam);
                        intent.putExtra("printer", sharedPreferences.getString(prntr,"NONE"));
                        MainActivity.this.startActivityForResult(intent, 222);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Please select a printer",Toast.LENGTH_LONG).show();
                    }
                }

        });
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemval = array_m[i];
                itemrate = array_r[i];
                if (itemval.isEmpty() && mobval.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please provide a valid mobile number and Select an item", Toast.LENGTH_LONG).show();
                } else {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setTitle("KD APPS - Generating bill - " + billID);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_ly);
                    Button ybtn, cbtn;
                    ybtn = dialog.findViewById(R.id.kbtn);
                    cbtn = dialog.findViewById(R.id.Nbtn);
                    dialog.show();
                    ybtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            connect();
                            //Dprint_bt();
                            editText.setText("");
                            dialog.dismiss();
                        }
                    });
                    cbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        Retry_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadmenu();
            }
        });
    }
    public void print_sel(View v)
    {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("KD APPS - Printer setup");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.printr_sel);
        final Button ybtn, cbtn;
        TextView msg;
        msg = dialog.findViewById(R.id.Pmsg);
        msg.setText("Select the printer!");
        ybtn = dialog.findViewById(R.id.Pkbtn);
        cbtn = dialog.findViewById(R.id.PNbtn);
        dialog.show();
        ybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(prntr, ybtn.getText().toString());
                editor.commit();
                P_nm.setText(sharedPreferences.getString(prntr,"NONE | ") + " | ");
                dialog.dismiss();
            }
        });
        cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(prntr, cbtn.getText().toString());
                editor.commit();
                P_nm.setText(sharedPreferences.getString(prntr,"NONE | ") + " | ");
                dialog.dismiss();
            }
        });
    }

    @Override
    public void updatedsyncstatus(String Sno, String Sdat) {
        int syncbill =0;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String val = sharedPreferences.getString(userName,"NULL");
        String amt = sharedPreferences.getString(bamt,"0");
        syncbill = Integer.valueOf(sharedPreferences.getString(sync_nb,"0"));
        syncbill =  syncbill + Integer.valueOf(Sno);
        editor.putString(sync_nb,String.valueOf(syncbill));
        editor.putString(sync_dt,Sdat);
        editor.putString(txtno,"0");
        editor.putString(bamt, "0");
        editor.commit();
        S_no.setText(sharedPreferences.getString(sync_nb,"0"));
        S_dt.setText(" | " + sharedPreferences.getString(sync_dt,"Unavailable"));
        T_no.setText(sharedPreferences.getString(txtno,"0"));
        T_amt.setText(" | " + getResources().getString(R.string.Rup) + " " + sharedPreferences.getString(bamt, "0"));
        String msg = Sno + " bills of amount " + amt  + " was synced by " + val + " on " + Sdat;
        String mob = "919980000260,918197233378";
        // mob = mob + "918197277956,919902627968";
        sendsms(mob,msg);
    }
}
