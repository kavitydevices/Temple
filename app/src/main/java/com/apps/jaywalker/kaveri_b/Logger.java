package com.apps.jaywalker.kaveri_b;

import android.util.Log;

public class Logger{


        private static final String TAG = "'Logger";

        public static void logDebug(String tag, String msg) {
            Log.d(tag, msg);
        }
}