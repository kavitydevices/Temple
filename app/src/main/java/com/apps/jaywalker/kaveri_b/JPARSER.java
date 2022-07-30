package com.apps.jaywalker.kaveri_b;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;


public class JPARSER {

    static InputStream IS = null;
    static JSONObject Jobj = null;
    static String Json="";

    //CONSTRUCTOR
    public JPARSER()
    {}


    //POST AND GET METHOD
    //POST AND GET METHOD
    public static JSONObject makeRequest(String url, String method, List<NameValuePair> params) {
        try {
            if (method == "POST") {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpclient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.e("Connected:","Successfully");
                IS = httpEntity.getContent();
            } else if (method == "GET") {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                IS = httpEntity.getContent();
                Log.e("Connected:","Successfully");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("Error:", e.toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e("Error:", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error:", e.toString());
        }
        catch(Exception e)
        {
            Log.e("Error:", e.toString());
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(IS, "UTF-8"),8);
            StringBuilder SB = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                SB.append(line + "\n");
            }
            IS.close();
            Json = SB.toString();
            Log.e("jdata:", Json.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("Buffer Error", "Error converting response" + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            Log.e("Buffer error:", e.toString());
        }
        try
        {
            Jobj = new JSONObject(Json);
            //Log.e("data :" ,  Jobj.getString("result") );

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON Parser", "JSON parsing data " + e.toString());

        }
        return Jobj;
    }


}
