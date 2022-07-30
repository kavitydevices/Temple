package com.apps.jaywalker.kaveri_b;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    public PreferenceUtils(){
    }
    public static boolean saveOrderId(String email, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(AppConstants.EXTRAS_ORDER_ID, email);


        prefsEditor.apply();
        return true;
    }
    public static String getOrderId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(AppConstants.EXTRAS_ORDER_ID, null);
    }

    public static boolean savePaymentStatus(String email, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(AppConstants.PAYMENT_STATUS, email);

        prefsEditor.apply();
        return true;
    }
    public static String getPaymentStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(AppConstants.PAYMENT_STATUS, null);
    }


}
