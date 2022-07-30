package com.apps.jaywalker.kaveri_b;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class qrcodeGenerator extends AppCompatActivity { private ImageView qrCodeIV;
    private TextView succes_text;
    private EditText dataEdt;
    private static final String TAG = qrcodeGenerator.class.getName();


    private Button generateQrBtn,status;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    RequestQueue requestQueue;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Press back to exit() Payment", Toast.LENGTH_LONG).show();
       /* Intent myIntent = new Intent(this, Home.class);
        startActivity(myIntent);
        finish();*/

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        setTitle("QR Scan Payment");
        // initializing all variables.
        qrCodeIV = findViewById(R.id.idIVQrcode);
        succes_text=findViewById(R.id.succes_text);
        dataEdt = findViewById(R.id.idEdt);
        status=findViewById(R.id.getsuccess);
        generateQrBtn = findViewById(R.id.idBtnGenerateQR);

        dataEdt.setText(PaymentActivity.getValue());


        if (TextUtils.isEmpty(dataEdt.getText().toString())) {

            // if the edittext inputs are empty then execute
            // this method showing a toast message.
            Toast.makeText(qrcodeGenerator.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
        } else {
            // below line is for getting
            // the windowmanager service.
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // initializing a variable for default display.
            Display display = manager.getDefaultDisplay();

            // creating a variable for point which
            // is to be displayed in QR Code.
            Point point = new Point();
            display.getSize(point);

            // getting width and
            // height of a point
            int width = point.x;
            int height = point.y;

            // generating dimension from width and height.
            int dimen = width < height ? width : height;
            dimen = dimen * 3 / 4;

            // setting this dimensions inside our qr code
            // encoder to generate our qr code.
            qrgEncoder = new QRGEncoder(dataEdt.getText().toString(), null, QRGContents.Type.TEXT, dimen);
            try {
                // getting our qrcode in the form of bitmap.
                bitmap = qrgEncoder.encodeAsBitmap();
                // the bitmap is set inside our image
                // view using .setimagebitmap method.
                qrCodeIV.setImageBitmap(bitmap);
            } catch (WriterException e) {
                // this method is called for
                // exception handling.
                Log.e("Tag", e.toString());
            }

            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkOrderStatus(PreferenceUtils.getOrderId(getApplicationContext()), "23517");

                }
            });



            // initializing onclick listener for button.
       /* generateQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(dataEdt.getText().toString())) {

                    // if the edittext inputs are empty then execute
                    // this method showing a toast message.
                    Toast.makeText(qrcodeGenerator.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
                } else {
                    // below line is for getting
                    // the windowmanager service.
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

                    // initializing a variable for default display.
                    Display display = manager.getDefaultDisplay();

                    // creating a variable for point which
                    // is to be displayed in QR Code.
                    Point point = new Point();
                    display.getSize(point);

                    // getting width and
                    // height of a point
                    int width = point.x;
                    int height = point.y;

                    // generating dimension from width and height.
                    int dimen = width < height ? width : height;
                    dimen = dimen * 3 / 4;

                    // setting this dimensions inside our qr code
                    // encoder to generate our qr code.
                    qrgEncoder = new QRGEncoder(dataEdt.getText().toString(), null, QRGContents.Type.TEXT, dimen);
                    try {
                        // getting our qrcode in the form of bitmap.
                        bitmap = qrgEncoder.encodeAsBitmap();
                        // the bitmap is set inside our image
                        // view using .setimagebitmap method.
                        qrCodeIV.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        // this method is called for
                        // exception handling.
                        Log.e("Tag", e.toString());
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
//finish();
                        }
                    }, 5000);                }
            }
        });*/
        }
        requestQueue = Volley.newRequestQueue(qrcodeGenerator.this);

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

    /**
     * get order detail to update UI
     **/
    private void getOrderDetail(final String authHeader, JSONObject jsonRequest) {
        String url = AppConstants.BASE_URL + AppConstants.ORDER_DETAIL;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(1, url, jsonRequest,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getString("PaymentStatus").equals("1")){
               /*                 PreferenceUtils.savePaymentStatus("success",qrcodeGenerator.this);
                                succes_text.setVisibility(View.VISIBLE);
                                succes_text.setText("Payment Success");
                                succes_text.setTextColor(Color.parseColor("#28bf50"));
*/                                Toast.makeText(qrcodeGenerator.this, "Payment Success", Toast.LENGTH_LONG).show();

                                onBackPressed();




                            }
                            else{
                                PreferenceUtils.savePaymentStatus("fail",qrcodeGenerator.this);
                                succes_text.setVisibility(View.VISIBLE);
                                succes_text.setText("Payment Failed");
                                succes_text.setTextColor(Color.parseColor("#eb3c36"));
                                Toast.makeText(qrcodeGenerator.this, "Payment Not Done Please try again", Toast.LENGTH_SHORT).show();
                            }
                            //  Toast.makeText(qrcodeGenerator.this, "payment failed", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Order status server response: " + response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());

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