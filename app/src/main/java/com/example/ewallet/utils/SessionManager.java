package com.example.ewallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "EWalletSessionPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PHONE = "userPhone";
    private static final String KEY_FULL_NAME = "userFullName";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveLoginSession(int userId, String phone, String fullName) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getPhone() {
        return pref.getString(KEY_PHONE, "");
    }

    public String getFullName() {
        return pref.getString(KEY_FULL_NAME, "");
    }

    public void updateFullName(String fullName) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
