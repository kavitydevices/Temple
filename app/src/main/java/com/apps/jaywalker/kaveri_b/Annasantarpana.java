package com.apps.jaywalker.kaveri_b;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Annasantarpana extends Activity {

    Dialog firstdialog;
    String id;
    String payment_type;
    byte FONT_TYPE;
    EditText name,address,mobile,amount;
    RequestQueue requestQueue;
    int billID=0;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;
    private static OutputStream btoutputstream;
    private static InputStream btinputstream;
    String array_m[],array_r[],mobval,uloc,Usrnam,printernm;
    public static int LSBMAX_LINE_CHARS = 32;
    public static int MBMAX_LINE_CHARS = 16;
    public static int LSMAX_LINE_CHARS = 32;
    List<String> Sname,Sqty,Spr,Tamt;
    DBHelper dbHelper;






    @Override
    protected void onRestart() {
        super.onRestart();
        checkOrderStatus(PreferenceUtils.getOrderId(getApplicationContext()), "23517");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annasantarpana);
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


        dbHelper = new DBHelper(Annasantarpana.this);


        TextView title=findViewById(R.id.title);
        title.setText("ANNA SANTAHRPANA  NIDHI");

        amount=findViewById(R.id.amounttobepaid);
        name= findViewById(R.id.user_name_tv);
        address=findViewById(R.id.address);
        mobile=findViewById(R.id.user_mobile_tv);



        findViewById(R.id.cashpayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(name.getText().toString().isEmpty())
                {
                    Toast.makeText(Annasantarpana.this, "please enter name", Toast.LENGTH_SHORT).show();

                }else {
                    if (address.getText().toString().isEmpty()) {
                        Toast.makeText(Annasantarpana.this, "please enter address", Toast.LENGTH_SHORT).show();

                    }else {
                        if(mobile.getText().toString().isEmpty()) {
                            Toast.makeText(Annasantarpana.this, "please enter mobile number", Toast.LENGTH_SHORT).show();

                        }else{
                            if (amount.getText().toString().isEmpty()) {
                                Toast.makeText(Annasantarpana.this, "please enter amount", Toast.LENGTH_SHORT).show();

                            }else{
                              //  paymentdone("cash","");
                                firstdialog = new Dialog(Annasantarpana.this);
                                firstdialog.setTitle("KD APPS - Donation - " + billID);
                                firstdialog.setCancelable(false);
                                firstdialog.setContentView(R.layout.dialog_ly);
                                Button ybtn, cbtn;
                                ybtn = firstdialog.findViewById(R.id.kbtn);
                                cbtn = firstdialog.findViewById(R.id.Nbtn);
                                firstdialog.show();
                                ybtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        payment_type = "cash";
                                        Log.e("first dialog","one");
                                        connect(Sname,Sqty,Spr,Tamt);

                                        //Dprint_bt();
                                        Log.e("first dialog","three");

                                        firstdialog.dismiss();
                                        Log.e("first dialog","four");

                                    }
                                });
                                cbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //  saveDB("cash");

                                        firstdialog.dismiss();

                                    }
                                });

                            }
                        }
                    }
                }

            }
        });

        findViewById(R.id.sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Annasantarpana.this, "Server Not Found", Toast.LENGTH_SHORT).show();

               /* if(PreferenceUtils.getPaymentStatus(Annasantarpana.this).equals("success")){
                    String msg ="JANAPRIYA-PAYMENT SUCCESSFUL. Bill amount - " ;//format need to be set w.r.to pending bill
                    //sendsms(mob,msg);
                    new Annasantarpana.sms_send(Annasantarpana.this,new String[]{mobile.getText().toString(),msg}).execute();
                }*/
            }
        });

        findViewById(R.id.onlinePayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().toString().isEmpty())
                {
                    Toast.makeText(Annasantarpana.this, "please enter name", Toast.LENGTH_SHORT).show();

                }else {
                    if (address.getText().toString().isEmpty()) {
                        Toast.makeText(Annasantarpana.this, "please enter address", Toast.LENGTH_SHORT).show();

                    }else {
                        if(mobile.getText().toString().isEmpty()) {
                            Toast.makeText(Annasantarpana.this, "please enter mobile number", Toast.LENGTH_SHORT).show();

                        }else{
                            if (amount.getText().toString().isEmpty()) {
                                Toast.makeText(Annasantarpana.this, "please enter amount", Toast.LENGTH_SHORT).show();

                            }else{


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
                                payment_type = "online";
                                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                                intent.putExtra(AppConstants.EXTRAS_IS_NEW_ORDER, true); // passing true due to new order
                                intent.putExtra(AppConstants.EXTRAS_MERCHANT_ID, "23517");
                                intent.putExtra(AppConstants.EXTRAS_AUTH_KEY, "db2c1d786e15428da10904c6e38edb18");
                                intent.putExtra(AppConstants.EXTRAS_AUTH_TOKEN, "af4be48674174a379f761abee53f1e5d");
                                intent.putExtra(AppConstants.EXTRAS_ORDER_AMOUNT, Integer.parseInt(amount.getText().toString()));
                                intent.putExtra(AppConstants.EXTRAS_CUSTOMER_MOBILE_NO, "");
                                intent.putExtra(AppConstants.EXTRAS_REDIRECT_URL, ""); // when payment finish will redirect to specific url
                                intent.putExtra(AppConstants.EXTRAS_WALLET_TYPE, "");
                                intent.putExtra(AppConstants.EXTRAS_WITH_UPI_INTENT, "");
                                intent.putExtra(AppConstants.EXTRAS_APPLICATION_INFO, applicationInformation);
                                startActivity(intent);

                            }
                        }
                    }
                }

               /* if(!amount.getText().toString().isEmpty()) {

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
                    intent.putExtra(AppConstants.EXTRAS_ORDER_AMOUNT, Integer.parseInt(amount.getText().toString()));
                    intent.putExtra(AppConstants.EXTRAS_CUSTOMER_MOBILE_NO, "");
                    intent.putExtra(AppConstants.EXTRAS_REDIRECT_URL, ""); // when payment finish will redirect to specific url
                    intent.putExtra(AppConstants.EXTRAS_WALLET_TYPE, "");
                    intent.putExtra(AppConstants.EXTRAS_WITH_UPI_INTENT, "");
                    intent.putExtra(AppConstants.EXTRAS_APPLICATION_INFO, applicationInformation);
                    startActivity(intent);
                }else{
                    Toast.makeText(Annasantarpana.this, "Please Enter the amount", Toast.LENGTH_SHORT).show();
                }*/
            }
        });


        requestQueue = Volley.newRequestQueue(Annasantarpana.this);

    }


    public final void paymentdone(String paymenttype,String paymentid) {


        if(paymenttype.equals("cash")) paymentid="";
        else paymentid= PreferenceUtils.getOrderId(getApplicationContext());

        View contextView = findViewById(android.R.id.content);

        if (!DetectConnection.checkInternetConnection(this)) {
           /* Snackbar snackbar =Snackbar.make(contextView, "Sorry, No Internet", Snackbar.LENGTH_LONG);
            snackbar.setAction("Retry", new TryAgainListener());

            snackbar.show();*/
        } else {
            Call<DonationResponse> userloginResponseCall = ApiClient.getUserService().
                    donation(name.getText().toString(),address.getText().toString(),mobile.getText().toString(),
                            paymenttype,paymentid,amount.getText().toString());
            try {

                userloginResponseCall.enqueue(new Callback<DonationResponse>() {
                    @Override
                    public void onResponse(Call<DonationResponse> call, Response<DonationResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(Annasantarpana.this, "Donation Received. Thank You", Toast.LENGTH_SHORT).show();

                            id=response.body().getId();
                           /* final Dialog dialog = new Dialog(Annasantarpana.this);
                            dialog.setTitle("KD APPS - Donation - " + billID);
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
                                  //  saveDB("cash");
                                    dialog.dismiss();

                                }
                            });*/


                        }
                        }


                    @Override
                    public void onFailure(Call<DonationResponse> call, Throwable t) {
                        t.printStackTrace();
                        t.getLocalizedMessage();
                        System.out.println(t.getLocalizedMessage());
                        Toast.makeText(Annasantarpana.this, "Server Error", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class TryAgainListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Code to undo the user's last action
            View contextView = findViewById(android.R.id.content);
            // Make and display Snackbar
       /*     if (!DetectConnection.checkInternetConnection(Annasantarpana.this)) {
                Snackbar snackbar =Snackbar.make(contextView, "Sorry, you're offline", Snackbar.LENGTH_LONG);
                snackbar.setAction("Retry", new TryAgainListener());
                snackbar.show();
            }
            else {

                Snackbar.make(contextView, "Internet Connected", Snackbar.LENGTH_SHORT)
                        .show();
            }
*/
        }
    }

//calling sms gateway

    public class sms_send extends AsyncTask<String,Void,Integer> {
        private Context context;
        String[] params;
        private HttpURLConnection urlConnection = null;
        private ProgressDialog mProgressDialog;
        //flag 0 means get and 1 means post.(By default it is get.)
        public sms_send(Context context,String[] params) {
            this.context = context;
            this.params = params;
        }

        protected void onPreExecute(){
            mProgressDialog = new ProgressDialog(Annasantarpana.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle("Transaction processing");
            mProgressDialog.setMessage("Sending sms and syncing with the server");
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... param) {


            Integer result = 0;
            try {
                /* forming th java.net.URL object */
                String data = "";
                data += "username=" + URLEncoder.encode("viswas", "UTF-8");
                data += "&password=" +URLEncoder.encode("vts@123", "UTF-8");
                /*data += "&password=" + URLEncoder.encode("vts@123", "UTF-8");*/
                data += "&sender=KDAPPS";
                data += "&sendto=" + URLEncoder.encode(params[0], "UTF-8");
                data += "&message=" + URLEncoder.encode(params[1], "UTF-8");


                //String url_str = "http://103.1.114.60/api.php?username=viswas&password=963779&sender=KDAPPS&sendto=91"+ params[0] + "&message=" + params[1] + "";
                String url_str = "http://103.1.114.60/api.php?";
                //String loadURL = "http://kd-apps.biz/pscripts/SyncBills.php";
                //JSONObject postObj = dbHandler.getSyncBillsFrom(LAST_BILLID_FROM_SERVER,CURRENT_LOGGED_IN_USER_ID);
                URL url = new URL(url_str + data);
                Log.d("url string: ", url_str + data);

                urlConnection = (HttpURLConnection) url.openConnection();


                urlConnection.setDoOutput(true);
                //TO read the logID response
               /* InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(inputStream);
                String text = "";
                String msg_res="";
                while ((text = br.readLine()) != null) {
                    msg_res += text;
                }*/
                int statusCode =urlConnection.getResponseCode();
                if(statusCode==200)
                {
                    result=1;
                }
                Log.d("STATUS RES : ",String.valueOf(statusCode));
            } catch (Exception e) {
                Log.d("SMS ", e.getLocalizedMessage());
                //Home.this.finish();
                e.printStackTrace();
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result){
            mProgressDialog.dismiss();
            if(result == 1)
            {
                //
                //  saveToDB(Double.parseDouble(mTotalValue.getText().toString()));
                Toast.makeText(Annasantarpana.this,"SMS sent successfully",Toast.LENGTH_LONG).show();
            }
            else if(result ==0)
            {
                Toast.makeText(Annasantarpana.this,"Bill generation failed!!Try again",Toast.LENGTH_LONG).show();
            }
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
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


                                Toast.makeText(Annasantarpana.this, "payment success", Toast.LENGTH_SHORT).show();

                                paymentdone("online", PreferenceUtils.getOrderId(Annasantarpana.this));

                                PreferenceUtils.saveOrderId(null,getApplicationContext());

                            }
                            else {
                              //  Toast.makeText(Annasantarpana.this, "Payment Failed Try Again", Toast.LENGTH_SHORT).show();
                              //  paymentdone("online", PreferenceUtils.getOrderId(Annasantarpana.this));

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
          /*  bill_act. print_bt(name.getText().toString(),amount.getText().toString(),mobile.getText().toString(),
                    address.getText().toString(), Integer.parseInt(PreferenceUtils.getOrderId(Annasantarpana.this)));
        */
            Log.e("payment_type",payment_type);
            System.out.println("payment type"+payment_type);

            printBill();
              /*    for(int i=0;i<itemnm.size();i++)
            {
                String itemntoprint = itemnm.get(i) + " ( Rs " + itemrt.get(i) + ")" + " X " + itemqt.get(i);

                print_bt(itemntoprint,Trt.get(i),itemqt.get(i),itemrt.get(i),i);
                itemrate = itemrate + Integer.valueOf(Trt.get(i));
            }*/

        }

    }

    private void print_bt() {
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            btoutputstream = btsocket.getOutputStream();

            byte[] printformat = {
                    0x1B,
                    0* 21,
                    FONT_TYPE
            };
            btoutputstream.write(printformat);
            String msg = "need to be printed";
            System.out.println(msg);
            btoutputstream.write(msg.getBytes());
            btoutputstream.write(0x0D);
            btoutputstream.write(0x0D);
            btoutputstream.write(0x0D);
            btoutputstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      /*  try {
            if (btsocket != null) {
                btoutputstream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = BTDeviceList.getSocket();
            if(btsocket != null){
                // printText(message.getText().toString());
                Log.e("payment_type",payment_type);
System.out.println("payment type"+payment_type);
                printBill();
            }
          //  reconf();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void printBill() {
        System.out.println("payment type"+payment_type);

        Log.e("payment_type",payment_type);

        if(btsocket == null){
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        }
        else{
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputStream = btsocket.getOutputStream();
                byte[] printformat = new byte[]{0x1B,0x21,0x03};
                byte[] bb2 = new byte[]{0x1B,0x21,0x20};
                outputStream.write(printformat);

                Toast.makeText(this, "Print Successful", Toast.LENGTH_SHORT).show();
                printCustom("SRI BHAGANDESHWARA-TALAKAVERI    TEMPLE, BHAGAMANDALA, KODAGU",3,1);
                printNewLine();
                printNewLine();


                printCustom("ANNA SANTHARPANE NIDHI DONATION",0,1);
                printNewLine();

                /*printCustom("Receipt Number - "+id,0,1);
                System.out.println(id);*/
                // printPhoto(R.drawable.ic_icon_pos);
                printCustom("Name :-"+name.getText().toString(),0,1);
                printNewLine();

                printCustom("Place :-"+address.getText().toString(),0,1);
                printNewLine();

                printCustom("Mobile. No :-"+mobile.getText().toString(),0,1);
                printNewLine();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                //String dateTime[] = getDateTime();
                printCustom(currentDate+"   "+currentTime,0,1);


                printCustom(new String(new char[32]).replace("\0", "."),0,1);
                printCustom( "Donation Amount - Rs:"+amount.getText().toString(),2,1);
             //   printText(leftRightAlign("Donated" , "Rs:"+amount.getText().toString()));
                printNewLine();

                printNewLine();
                printCustom("Thank you for Donating",0,1);
                printNewLine();

                printCustom("Please visit again",0,1);
                printNewLine();
                printNewLine();

                printNewLine();

                Log.e("first dialog","second");

                outputStream.flush();

                reconf();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{27,33,0}; // 3- bold with large text

        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print text
    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String leftRightAlign(String str1, String str2) {
        String ans = str1 +str2;
        if(ans.length() <31){
            int n = (31 - str1.length() + str2.length());
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
        }
        return ans;
    }


    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }

    private void print_bt(String itemval,String itemrate,String qty,String amt,int ival) {

        if(printernm.equals("LS"))
        {
            Toast.makeText(this, "Selected ls printer", Toast.LENGTH_SHORT).show();
            printLS(itemval,itemrate,qty,amt,ival);
        }else if (printernm.equals("NGX"))
        {
            Toast.makeText(this, "Selected NGX printer", Toast.LENGTH_SHORT).show();

            printNGX(itemval,itemrate,qty,amt,ival);
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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

            Toast.makeText(this, "inside ls printer", Toast.LENGTH_SHORT).show();

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
          /*  if(Sname.get(ival).equals("PINDAPRADHANA"))
            {
                thank_you = "Thank you!!";
            }
            else
            {*/
                thank_you = "Thank you!! Visit Again!!";
           // }

            String thank_you_formatted_to_printer = format_string_to_center_for_printer(thank_you,Smax);
            /*THANK YOU END */
            /*START SENDING DATA TO PRINTER*/
            Toast.makeText(this, "inside ls printer 1", Toast.LENGTH_SHORT).show();

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

            Toast.makeText(this, "inside ls printer 2", Toast.LENGTH_SHORT).show();

            btoutputstream.flush();
            Toast.makeText(this, "inside ls printer 3", Toast.LENGTH_SHORT).show();

            String msg = agency_name + " - Bill successfully Generated for " + itemval + ", Bill amount - Rs" + itemrate + ". Thank you!! Visit again";
            //sendsms(mobval, msg); Deactivated the code as per Client req


            // totalPrice = 0.0;


        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

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

            Toast.makeText(this, "inside NGX printer", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, "inside NGX printer 1", Toast.LENGTH_SHORT).show();

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

            Toast.makeText(this, "inside NGX printer 2", Toast.LENGTH_SHORT).show();

            btoutputstream.flush();
            Toast.makeText(this, "inside NGX printer 3", Toast.LENGTH_SHORT).show();

            String msg = agency_name + " - Bill successfully Generated for " + itemval + ", Bill amount - Rs" + itemrate + ". Thank you!! Visit again";
            //sendsms(mobval, msg); Deactivated the code as per Client req

            //dbHelper.insertBill(billID,mobval,Sname.get(ival),qty,amt,itemrate,todayStr,timestr,Usrnam);
            billID = dbHelper.getBillID(Usrnam);
            // totalPrice = 0.0;


        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            e.printStackTrace();
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

    private void reconf()
    {                Log.e("second dialog","first");
                 Log.e("payment_type",payment_type);

        paymentdone(payment_type,id);
        final Dialog dialog = new Dialog(Annasantarpana.this);
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
                // saveDB("cash");
                dialog.dismiss();
                saveDB("cash");

                finish();
            }
        });
    }

    private void saveDB(String paymentmethod)
    {
        final ProgressDialog progressDoalog;

        progressDoalog = new ProgressDialog(Annasantarpana.this);
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


}