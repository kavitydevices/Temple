package com.apps.jaywalker.kaveri_b;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class Bill_act extends AppCompatActivity {
    GridView menu_LV;
    ListView lst;
    RequestQueue requestQueue;
    int price=0;
    //static int val=0;
    String array_m[],array_r[],mobval,uloc,Usrnam,printernm;
    int billID=0;
    List<String> Sname,Sqty,Spr,Tamt;
    TextView txt,printtxt,cantxt,noitems;
    //LS MAX CHAR FOR BIG N SMALL FONTS
    public static int LSBMAX_LINE_CHARS = 32;
    public static int MBMAX_LINE_CHARS = 16;
    public static int LSMAX_LINE_CHARS = 32;
    //NGX MAX CHAR FOR BIG N SMALL FONTS
    public static int NBMAX_LINE_CHARS = 33;
    public static int NMAX_LINE_CHARS = 33;
    public static int MGBMAX_LINE_CHARS = 33;
    ArrayList<String> printPaperStringArray = new ArrayList<>();
    public static int MAX_PAPERNAME_CHARS = 11;
    public static int MAX_QTY_CHARS = 3;
    public static int MAX_AMT_CHARS = 5;
    private static BluetoothSocket btsocket;
    private static OutputStream btoutputstream;
    private static InputStream btinputstream;
    DBHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_act);
        Intent intent =getIntent();
        Sname = new ArrayList<>();
        Sqty = new ArrayList<>();
        Spr = new ArrayList<>();
        Tamt = new ArrayList<>();
        array_m = intent.getStringArrayExtra("itemn");
        array_r = intent.getStringArrayExtra("itemr");
        billID = intent.getIntExtra("Billid",0);
        mobval = intent.getStringExtra("mobval");
        uloc = intent.getStringExtra("uloc");
        Usrnam = intent.getStringExtra("usrname");
        printernm = intent.getStringExtra("printer");
        dbHelper = new DBHelper(Bill_act.this);
        menu_LV= findViewById(R.id.BMlist);
        lst = findViewById(R.id.addedm);
        txt = findViewById(R.id.TotalB);
        printtxt = findViewById(R.id.Print_btn);
        cantxt = findViewById(R.id.cancel_btn);
        noitems = findViewById(R.id.noitems);
        final seva_adapter seva_ad = new seva_adapter(Bill_act.this,0,Sname,Sqty,Spr,Tamt);
        lst.setAdapter(seva_ad);
        menu_LV.setAdapter(new Menu_adapter(Bill_act.this,array_m,array_r));
        menu_LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                noitems.setVisibility(View.GONE);
                lst.setVisibility(View.VISIBLE);
                int inflag=1;
                for(int j=0;j<Sname.size();j++)
                {
                    if(Sname.get(j).equals(array_m[i]))
                    {
                        inflag =0;
                        break;
                    }

                }
                if (inflag ==1)
                {
                        Sname.add(array_m[i]);
                        Sqty.add("1");
                        Spr.add(array_r[i]);
                        Tamt.add(array_r[i]);
                        seva_ad.notifyDataSetChanged();
                         inflag =0;

                }
                else
                {
                    inflag =0;
                    Toast.makeText(Bill_act.this, "Item already exists", Toast.LENGTH_LONG).show();
                }


            }
        });
        seva_ad.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                int val = 0;
                if(Tamt.size() <= 0)
                {
                    noitems.setVisibility(View.VISIBLE);
                    lst.setVisibility(View.GONE);
                }
                else
                {
                    noitems.setVisibility(View.GONE);
                    lst.setVisibility(View.VISIBLE);
                    for(int i=0;i<Tamt.size();i++)
                    {
                        val = val +Integer.valueOf(Tamt.get(i));
                        txt.setText(String.valueOf(getResources().getString(R.string.Rup) + " " + val));
                    }
                }
                price= val;


            }
        });



        findViewById(R.id.Print_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(price!=0){
                    paymentDialog forgotPasswordDialog=new paymentDialog();
                    forgotPasswordDialog.showDialog(Bill_act.this,"");

                }else Toast.makeText(Bill_act.this, "Add a seva ", Toast.LENGTH_SHORT).show();
                     }
        });
     /*   printtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Sname.size() < 0 && mobval.isEmpty()) {
                    Toast.makeText(Bill_act.this, "Add a seva to print a bill!", Toast.LENGTH_LONG).show();
                } else {
                    final Dialog dialog = new Dialog(Bill_act.this);
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
                            connect(Sname,Sqty,Spr,Tamt);
                            //Dprint_bt();
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
        });*/
        cantxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


        requestQueue = Volley.newRequestQueue(Bill_act.this);

    }
    protected void connect(List<String> itemnm,List<String> itemqt,List<String> itemrt,List<String> Trt) {
        int itemrate = 0;
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
            for(int i=0;i<itemnm.size();i++)
            {
                String itemntoprint = itemnm.get(i) + " ( Rs " + itemrt.get(i) + ")" + " X " + itemqt.get(i);
                print_bt(itemntoprint,Trt.get(i),itemqt.get(i),itemrt.get(i),i);
                itemrate = itemrate + Integer.valueOf(Trt.get(i));
            }
            reconf();

        }

    }

    private void print_bt(String itemval,String itemrate,String qty,String amt,int ival) {

        if(printernm.equals("LS"))
        {
            printLS(itemval,itemrate,qty,amt,ival);
        }else if (printernm.equals("NGX"))
        {
            printNGX(itemval,itemrate,qty,amt,ival);
        }

    }

    protected String format_string_to_center_for_printer(String printLine,int MAX_LINE_CHARS) {
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

    protected String format_string_for_print(String stringToPrint,int MAX_LINE_CHARS) {
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

    protected String format_dashed_line_for_printer(int MAX_LINE_CHARS) {
        String retVal = "";
        for (int i = 0; i < MAX_LINE_CHARS; i++) {
            retVal += "-";
        }

        return retVal;
    }

    protected String format_blank_line_for_printer(int MAX_LINE_CHARS) {
        String retVal = "";
        for (int i = 0; i < MAX_LINE_CHARS; i++) {
            retVal += " ";
        }

        return retVal;
    }

    protected String format_total_line_for_printer(String itemrate,int MAX_LINE_CHARS) {
        String retVal = "";

        String totalString = "TOTAL";
        String totalPrc = "Rs " + itemrate;

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

    private void sendsms(final String mob, String messg) {
        String msg = messg;
        String mobilenb = mob;
        String url = "http://smsbusiness.in/api.php?username=viswas&password=380047&sender=KDAPPS&sendto=91" + mobilenb + "&message=" + msg + "";
        try {
            String Eurl = URLEncoder.encode(url, "UTF-8");
            AsyncHttpClient client = new AsyncHttpClient();
            //OTPalert(mob);
            client.get(Eurl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(Bill_act.this, "Sms sent successfully", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(Bill_act.this, "Something went wrong!!", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onRestart() {
        super.onRestart();
        checkOrderStatus(PreferenceUtils.getOrderId(getApplicationContext()), "23517");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BTDeviceList.REQUEST_CONNECT_BT) {
            try {
                int itemrate=0;
                btsocket = BTDeviceList.getSocket();
                if (btsocket != null) {
                    for(int i=0;i<Sname.size();i++)
                    {
                        String itemntoprint = Sname.get(i) + " ( Rs " + Spr.get(i) + ")" + " X " + Sqty.get(i);
                        print_bt(itemntoprint,Tamt.get(i),Sqty.get(i),Spr.get(i),i);
                        itemrate = itemrate + Integer.valueOf(Tamt.get(i));
                    }
                    reconf();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private void printLS(String itemval,String itemrate,String qty,String amt,int ival)
    {
        int BMax=0,Smax=0,Imax=0;
        byte[] nomalchar;
        byte[] bigchar ;
        byte[] headr;
        byte[] cent;
        byte[] righ;
        String thank_you ="";
        BMax = LSBMAX_LINE_CHARS;
        Imax = MBMAX_LINE_CHARS;
        Smax= LSMAX_LINE_CHARS;
        nomalchar = new byte[]{0x1B,0x21,0X08};
        bigchar = new byte[]{0x1B,0x21,0X20};
        headr =new byte[]{0x1B,0x21,0X10};
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
            String agency_name="",agency_address1="",agency_address2="";
            if(uloc.equals("1"))
            {
                agency_name = "Tala kaveri - Reciept";
                agency_address1 = "Bhagamandala";
                agency_address2 = "Kodagu";
            }
            else if(uloc.equals("2") || uloc.equals("3") )
            {
                agency_name = "Bhagandeshwara Temple - Reciept";
                agency_address1 = "Bhagamandala";
                agency_address2 = "Kodagu";
            }

            Toast.makeText(this, "Print Successful", Toast.LENGTH_SHORT).show();

            String agency_name_formatted_to_printer = format_string_to_center_for_printer(agency_name,BMax);
            String agency_address1_formatted_to_printer = format_string_to_center_for_printer(agency_address1,BMax);
            String agency_address2_formatted_to_printer = format_string_to_center_for_printer(agency_address2,BMax);
            String datedStr = "Date:" + todayStr;
            String custStr = "Bill #: " + String.valueOf(billID);
            String itemns = itemval;
            String billamt = itemrate;
            String datedStr_formatted_to_printer = format_string_for_print(datedStr,BMax);
            String custStr_formatted_to_printer = format_string_for_print(custStr,Smax);
            String formated_items = format_string_for_print(itemns,BMax);
            String formated_billamt = format_total_line_for_printer(billamt,Imax);
            Double totamt = Double.parseDouble("00");
            /* DATA HEADER START */
            String detailHeading = "ITEM        QTY   AMT  ";
            //String detailHeading_formatted_to_printer = format_string_for_print(detailHeading);
            /* DATA HEADER END */
            /* UTIL START */
            String dashedLine_formatted_to_printer = format_dashed_line_for_printer(Smax);
            String blankLine_formatted_to_printer = format_blank_line_for_printer(Imax);
            if(Sname.get(ival).equals("PINDAPRADHANA"))
            {
                 thank_you = "Thank you!!";
            }
            else
            {
                thank_you = "Thank you!! Visit Again!!";
            }

            String thank_you_formatted_to_printer = format_string_to_center_for_printer(thank_you,Smax);
            /*THANK YOU END */
            /*START SENDING DATA TO PRINTER*/

            btoutputstream.write(nomalchar, 0, nomalchar.length);
            // Bill Header Printer
            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(headr, 0, headr.length);
            btoutputstream.write(agency_name_formatted_to_printer.getBytes(), 0, agency_name_formatted_to_printer.getBytes().length);
            btoutputstream.write(agency_address1_formatted_to_printer.getBytes(), 0, agency_address1_formatted_to_printer.getBytes().length);
            btoutputstream.write(agency_address2_formatted_to_printer.getBytes(), 0, agency_address2_formatted_to_printer.getBytes().length);
            btoutputstream.write(nomalchar, 0, nomalchar.length);
            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);


            btoutputstream.write(custStr_formatted_to_printer.getBytes(), 0, custStr_formatted_to_printer.getBytes().length);
            //Printing Date n items
            btoutputstream.write(bigchar, 0, bigchar.length);

            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write(datedStr_formatted_to_printer.getBytes(), 0, datedStr_formatted_to_printer.getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);

            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write(formated_items.getBytes(), 0, formated_items.getBytes().length);

            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(formated_billamt.getBytes(), 0, formated_billamt.getBytes().length);
            btoutputstream.write(nomalchar, 0, nomalchar.length);
            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(thank_you_formatted_to_printer.getBytes(), 0, thank_you_formatted_to_printer.getBytes().length);
            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write("\n".getBytes(), 0, "\n".getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);

            btoutputstream.flush();

            String msg = agency_name + " - Bill successfully Generated for " + itemval + ", Bill amount - Rs" + itemrate + ". Thank you!! Visit again";
            //sendsms(mobval, msg); Deactivated the code as per Client req


            // totalPrice = 0.0;


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void printNGX(String itemval,String itemrate,String qty,String amt,int ival)
    {
        int BMax=0,Smax=0,Imax=0;
        byte[] nomalchar;
        byte[] bigchar;
        byte[] headr;
        byte[] cent;
        byte[] righ;
        byte[] left;
        byte[] newln;
        String thank_you ="";
        nomalchar = new byte[]{27, 33, 0};
        bigchar = new byte[]{27, 33, -16};
        cent = new byte[]{27,16,1};
        left = new byte[]{27, 16, 0};
        righ = new byte[]{27, 16, 2};
        newln= new byte[]{10};
        headr = new byte[]{27, 33, -1};
        BMax = 24;
        Smax= 24;
        Imax = 18;
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
            if(uloc.equals("1"))
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

            Toast.makeText(this, "Print Successful", Toast.LENGTH_SHORT).show();

            String datedStr = "Date:" + todayStr;
            String custStr = "Bill #: " + String.valueOf(billID);
            String itemns = itemval;
            String billamt = itemrate;
            String datedStr_formatted_to_printer = format_string_for_print(datedStr,BMax);
            String custStr_formatted_to_printer = format_string_for_print(custStr,Smax);
            String formated_items = format_string_for_print(itemns,BMax);
            String formated_billamt = format_total_line_for_printer(billamt,Imax);
            Double totamt = Double.parseDouble("00");
            /* DATA HEADER START */
            String detailHeading = "ITEM        QTY   AMT  ";
            //String detailHeading_formatted_to_printer = format_string_for_print(detailHeading);
            /* DATA HEADER END */
            /* UTIL START */
            String dashedLine_formatted_to_printer = format_dashed_line_for_printer(Smax);
            String blankLine_formatted_to_printer = format_blank_line_for_printer(Imax);
            if(Sname.get(ival).equals("PINDAPRADHANA"))
            {
                thank_you = "Thank you!!";
            }
            else
            {
                thank_you = "Thank you!! Visit Again!!";
            }
            String thank_you_formatted_to_printer = format_string_to_center_for_printer(thank_you,Smax);
            /*THANK YOU END */
            /*START SENDING DATA TO PRINTER*/

            btoutputstream.write(nomalchar, 0, nomalchar.length);
            // Bill Header Printer
            //btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(newln, 0, newln.length);
            btoutputstream.write(cent, 0, cent.length);
            btoutputstream.write(bigchar, 0, bigchar.length);
            btoutputstream.write(agency_name.getBytes(), 0, agency_name.getBytes().length);
            btoutputstream.write(newln, 0, newln.length);

            btoutputstream.write(agency_address1.getBytes(), 0, agency_address1.getBytes().length);
            btoutputstream.write(newln, 0, newln.length);

            btoutputstream.write(agency_address2.getBytes(), 0, agency_address2.getBytes().length);
            btoutputstream.write(newln, 0, newln.length);

            btoutputstream.write(left, 0, left.length);
            btoutputstream.write(newln, 0, newln.length);

            btoutputstream.write(bigchar, 0, bigchar.length);
            btoutputstream.write(newln, 0, newln.length);

           // btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length)



            btoutputstream.write(custStr_formatted_to_printer.getBytes(), 0, custStr_formatted_to_printer.getBytes().length);
            //Printing Date n items
            btoutputstream.write(newln, 0, newln.length);

            btoutputstream.write(bigchar, 0, bigchar.length);

            btoutputstream.write(newln, 0, newln.length);

            btoutputstream.write(datedStr_formatted_to_printer.getBytes(), 0, datedStr_formatted_to_printer.getBytes().length);
            //btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(newln, 0, newln.length);

            btoutputstream.write(formated_items.getBytes(), 0, formated_items.getBytes().length);
            btoutputstream.write(newln, 0, newln.length);

            //btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
           // btoutputstream.write(righ, 0, righ.length);
            btoutputstream.write(left, 0, left.length);
            btoutputstream.write("TOTAL".getBytes(),0,"TOTAL".getBytes().length);
            btoutputstream.write(righ, 0, righ.length);
            btoutputstream.write(billamt.getBytes(), 0, billamt.getBytes().length);
            btoutputstream.write(newln, 0, newln.length);
           // btoutputstream.write(bigchar, 0, bigchar.length);
            btoutputstream.write(cent, 0, cent.length);

            btoutputstream.write(newln, 0, newln.length);

            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(thank_you_formatted_to_printer.getBytes(), 0, thank_you_formatted_to_printer.getBytes().length);
            btoutputstream.write(dashedLine_formatted_to_printer.getBytes(), 0, dashedLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(newln, 0, newln.length);

            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);
            btoutputstream.write(blankLine_formatted_to_printer.getBytes(), 0, blankLine_formatted_to_printer.getBytes().length);

            btoutputstream.flush();

            String msg = agency_name + " - Bill successfully Generated for " + itemval + ", Bill amount - Rs" + itemrate + ". Thank you!! Visit again";
            //sendsms(mobval, msg); Deactivated the code as per Client req

            //dbHelper.insertBill(billID,mobval,Sname.get(ival),qty,amt,itemrate,todayStr,timestr,Usrnam);
            billID = dbHelper.getBillID(Usrnam);
            // totalPrice = 0.0;


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveDB(String paymentmethod)
    {
        final ProgressDialog progressDoalog;

        progressDoalog = new ProgressDialog(Bill_act.this);
      try{


          progressDoalog.setMessage("loading ...");
          progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
          progressDoalog.show();
          Date today = new Date();
          DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
          DateFormat Tformatter = new SimpleDateFormat("hh:mm:ss");
          String todayStr = formatter.format(today);
          String timestr = Tformatter.format(today);
          int itemrate=0;
          for(int i =0;i<Sname.size();i++)
          {
              dbHelper.insertBill(billID,mobval,Sname.get(i),Sqty.get(i),Tamt.get(i),Spr.get(i),todayStr,timestr,Usrnam);
              itemrate = itemrate + Integer.valueOf(Tamt.get(i));
          }

          billID = dbHelper.getBillID(Usrnam);
          Intent intent =new Intent();
          intent.putExtra("billid",billID);
          intent.putExtra("billamt",String.valueOf(itemrate));
          intent.putExtra("bcount",Sname.size());
          setResult(RESULT_OK,intent);
          progressDoalog.dismiss();
          finish();
      }catch (Exception e){
          progressDoalog.dismiss();
          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
      }
    }
    private void reconf()
    {
        final Dialog dialog = new Dialog(Bill_act.this);
        dialog.setTitle("KD APPS - Bill generation - " + billID);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_ly);
        Button ybtn, cbtn;
        TextView msg = dialog.findViewById(R.id.msg);
        ybtn = dialog.findViewById(R.id.kbtn);
        cbtn = dialog.findViewById(R.id.Nbtn);
        msg.setText("Print another Copy??");
        ybtn.setText("REPRINT");
        cbtn.setText("CLOSE");
        dialog.show();
        ybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btsocket =null;

                connect(Sname,Sqty,Spr,Tamt);
                //Dprint_bt();
                dialog.dismiss();
            }
        });
        cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDB("cash");
                dialog.dismiss();
            }
        });
    }

    public class paymentDialog {

        public void showDialog(Activity activity, String msg){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

//            dialog.setCancelable(false);
            dialog.setContentView(R.layout.password_dialog_layout);
            Button buttonOk=(Button) dialog.findViewById(R.id.cash);
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   Toast.makeText(Bill_act.this, "cash", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if (Sname.size() < 0 && mobval.isEmpty()) {
                        Toast.makeText(Bill_act.this, "Add a seva to print a bill!", Toast.LENGTH_LONG).show();
                    } else {


                        final Dialog dialog = new Dialog(Bill_act.this);
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
                                //saveDB("cash");

                                connect(Sname,Sqty,Spr,Tamt);
                                //Dprint_bt();
                                dialog.dismiss();
                            }
                        });
                        cbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //saveDB("cash");

                                dialog.dismiss();

                            }
                        });
                    }

                }
            });

            Button online=(Button) dialog.findViewById(R.id.online);
            online.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Bill_act.this, "online", Toast.LENGTH_SHORT).show();

                    if(price!=0) {

                        //gotoCreateNewOrderActivity(true);

                        final PackageManager pckManager = getApplicationContext().getPackageManager();
                        ApplicationInfo applicationInformation = null;
                        try {
                            applicationInformation = pckManager.getApplicationInfo("com.example.paygpayment", PackageManager.GET_META_DATA);
                            String packageName = applicationInformation.packageName;
                            Log.d("NewOrderActivity", "package name: " + packageName);
                        } catch (Exception ex) {
                            Log.d("NewOrderActivity", "ex: " + ex.toString());
                        }

                        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                        intent.putExtra(AppConstants.EXTRAS_IS_NEW_ORDER, true); // passing true due to new order
                        intent.putExtra(AppConstants.EXTRAS_MERCHANT_ID, "23517");
                        intent.putExtra(AppConstants.EXTRAS_AUTH_KEY, "db2c1d786e15428da10904c6e38edb18");
                        intent.putExtra(AppConstants.EXTRAS_AUTH_TOKEN, "af4be48674174a379f761abee53f1e5d");
                        intent.putExtra(AppConstants.EXTRAS_ORDER_AMOUNT, price);
                        intent.putExtra(AppConstants.EXTRAS_CUSTOMER_MOBILE_NO, "");
                        intent.putExtra(AppConstants.EXTRAS_REDIRECT_URL, ""); // when payment finish will redirect to specific url
                        intent.putExtra(AppConstants.EXTRAS_WALLET_TYPE, "");
                        intent.putExtra(AppConstants.EXTRAS_WITH_UPI_INTENT, "");
                        intent.putExtra(AppConstants.EXTRAS_APPLICATION_INFO, applicationInformation);
                        startActivity(intent);
                        dialog.dismiss();

                    }else{
                        Toast.makeText(Bill_act.this, "Please Select Seva", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                }
            });


            dialog.show();


        }
    }

    private void checkOrderStatus(String orderKeyId, String merchantId){
        JSONObject jsonRequest = Utility.generateOrderStatusRequest(orderKeyId, merchantId);
        Logger.logDebug("TAG", "request:" + jsonRequest.toString());
        String hash = Utility.getEncodedBase64Hash("db2c1d786e15428da10904c6e38edb18", "af4be48674174a379f761abee53f1e5d", merchantId);
        // call api
        String authHeader = "basic " + hash;
        Log.d("AuthHeader", authHeader);
        // hit request to server to check status
        getOrderDetail(authHeader, jsonRequest);
    }

    private void getOrderDetail(final String authHeader, JSONObject jsonRequest) {
        String url = AppConstants.BASE_URL + AppConstants.ORDER_DETAIL;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(1, url, jsonRequest,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getString("PaymentStatus").equals("1")){


                                Toast.makeText(Bill_act.this, "payment success", Toast.LENGTH_SHORT).show();
                                if (Sname.size() < 0 && mobval.isEmpty()) {
                                    Toast.makeText(Bill_act.this, "Add a seva to print a bill!", Toast.LENGTH_LONG).show();
                                } else {
                                    final Dialog dialog = new Dialog(Bill_act.this);
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
                                            saveDB("online");

                                            connect(Sname,Sqty,Spr,Tamt);
                                            //Dprint_bt();
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
                            else {
                          /*      Toast.makeText(Bill_act.this, "payment failed", Toast.LENGTH_SHORT).show();
                                if (Sname.size() < 0 && mobval.isEmpty()) {
                                    Toast.makeText(Bill_act.this, "Add a seva to print a bill!", Toast.LENGTH_LONG).show();
                                } else {
                                    final Dialog dialog = new Dialog(Bill_act.this);
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
                                            connect(Sname,Sqty,Spr,Tamt);
                                            //Dprint_bt();
                                            dialog.dismiss();
                                        }
                                    });
                                    cbtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });
                                }*/
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Log.e(TAG, "Error:" + error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", authHeader);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                AppConstants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }


}
