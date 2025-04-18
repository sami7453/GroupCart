package com.example.groupcart;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String SHARED_PREFS = "app_prefs";
    private static final String KEY_USER   = "key_user";   // username
    private static final String KEY_PASS   = "key_pass";   // password
    private static final String KEY_EMAIL  = "key_email";  // email

    private SharedPreferences prefs;

    private Prefs(Context ctx) {
        prefs = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public static Prefs with(Context ctx) {
        return new Prefs(ctx);
    }

    /** Sauvegarde username, password et email */
    public void saveUser(String username, String password, String email) {
        prefs.edit()
                .putString(KEY_USER, username)
                .putString(KEY_PASS, password)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    /** Vérifie username + password (email non utilisé pour l’authent) */
    public boolean checkUser(String username, String password) {
        String u = prefs.getString(KEY_USER, null);
        String p = prefs.getString(KEY_PASS, null);
        return username.equals(u) && password.equals(p);
    }

    /** Récupérer l’email stocké si jamais tu en as besoin */
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }
}
