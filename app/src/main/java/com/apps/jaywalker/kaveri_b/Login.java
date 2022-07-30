package com.apps.jaywalker.kaveri_b;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    private EditText utxt,ptxt;
    private Button btn,btn1;
    JSONObject jsonObject;

    ProgressDialog pd;
    String URL_Serv,usrstate,usrloc;
    SharedPreferences sharedPreferences;
    public static final String MYpref = "Mypref";
    public static final String userLoged = "usrlog";
    public static final String userLoc = "usrloc";
    public static final String userName = "usrname";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        utxt = (EditText)findViewById(R.id.usrnm);
        ptxt = (EditText)findViewById(R.id.pwd);
        btn = (Button) findViewById(R.id.Loginbtn);
        //URL_Serv =  "http://192.168.43.64/kpos/Login_op.php";
     //   URL_Serv =  "http://kd-apps.biz/KPOS1/Login_op.php";
       URL_Serv = "http://templeapi.obbe.in/Login_op.php";

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(utxt.getText().toString().isEmpty() && ptxt.getText().toString().isEmpty())
                {
                    Toast.makeText(Login.this,"PLease provide the credentials",Toast.LENGTH_LONG).show();
                }
                else{
                    loadmenu();
                }
            }
        });
        sharedPreferences = getSharedPreferences(MYpref, Context.MODE_PRIVATE);
        usrstate = sharedPreferences.getString(userLoged,"NONE");
        usrloc = sharedPreferences.getString(userLoc,"NONE");
        if(!usrstate.toLowerCase().equals("none")) {
            Intent intent =new Intent(Login.this,MainActivity.class);
            intent.putExtra("Loc",usrloc);
            intent.putExtra("uname",usrstate);
            startActivity(intent);
        }
    }
    public void loadmenu() {
        class getmenu extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(Login.this);
                pd.setTitle("KD APPS _ BILLING POS");
                pd.setMessage("Please wait");
                pd.setCancelable(false);
                pd.setIndeterminate(false);
                pd.show();

            }

            @Override
            protected JSONObject doInBackground(String... strings) {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("umob", utxt.getText().toString()));
                params1.add(new BasicNameValuePair("usrpwd", ptxt.getText().toString()));
                jsonObject = JPARSER.makeRequest(URL_Serv , "POST", params1);
                return jsonObject;
            }

            protected void onPostExecute(JSONObject val) {
                try {
                   String res = val.getString("success");

                   if(res.equals("1"))
                   {
                       String uloc = val.getString("message");
                       String unm = val.getString("uid");
//                       SharedPreferences.Editor editor = sharedPreferences.edit();
//                       editor.putString(userLoged,unm);
//                       editor.putString(userLoc,uloc);
//                       editor.putString(userName,utxt.getText().toString());
//                       editor.commit();
                       Intent intent =new Intent(Login.this,MainActivity.class);
                       intent.putExtra("Loc",uloc);
                       intent.putExtra("uname",unm);
                       startActivity(intent);
                   }
                   else
                   {
                       Toast.makeText(Login.this,"Incorrect credentials or Something went wrong",Toast.LENGTH_LONG).show();
                   }

                }
                catch (JSONException ex)
                {

                }
                catch (NullPointerException ex)
                {
                    Toast.makeText(Login.this,"No internet connectivity!!Try again!!",Toast.LENGTH_LONG).show();

                }
                pd.dismiss();
            }
        }
        getmenu gt = new getmenu();
        gt.execute();
    }

}
