package com.example.kindnestapp2;

import android.content.Context;
import android.content.SharedPreferences;

public class AdminSessionManager {
    private static final String PREF_NAME = "AdminSession";
    private static final String KEY_IS_ADMIN_LOGGED_IN = "isAdminLoggedIn";
    private static final String KEY_ADMIN_EMAIL = "adminEmail";
    private static final String KEY_ADMIN_PASSWORD = "adminPassword";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public AdminSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createAdminLoginSession(String email, String password) {
        editor.putBoolean(KEY_IS_ADMIN_LOGGED_IN, true);
        editor.putString(KEY_ADMIN_EMAIL, email);
        editor.putString(KEY_ADMIN_PASSWORD, password);
        editor.commit();
    }

    public boolean isAdminLoggedIn() {
        return pref.getBoolean(KEY_IS_ADMIN_LOGGED_IN, false);
    }

    public String getAdminEmail() {
        return pref.getString(KEY_ADMIN_EMAIL, "");
    }

    public String getAdminPassword() {
        return pref.getString(KEY_ADMIN_PASSWORD, "");
    }

    public void logoutAdmin() {
        editor.clear();
        editor.commit();
    }
}