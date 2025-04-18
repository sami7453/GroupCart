package com.example.groupcart;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Prefs {
    private static final String SHARED_PREFS = "app_prefs";
    private static final String KEY_USER    = "key_user";
    private static final String KEY_PASS    = "key_pass";
    private static final String KEY_EMAIL   = "key_email";
    private static final String KEY_GROUPS  = "key_groups";

    private SharedPreferences prefs;
    private Gson gson = new Gson();

    private Prefs(Context ctx) {
        prefs = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public static Prefs with(Context ctx) {
        return new Prefs(ctx);
    }

    // --- Authentification ---
    public void saveUser(String username, String password, String email) {
        prefs.edit()
                .putString(KEY_USER, username)
                .putString(KEY_PASS, password)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public boolean checkUser(String username, String password) {
        String u = prefs.getString(KEY_USER, null);
        String p = prefs.getString(KEY_PASS, null);
        return username.equals(u) && password.equals(p);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    // --- Gestion des groupes ---
    public void saveGroups(List<Group> groups) {
        String json = gson.toJson(groups);
        prefs.edit().putString(KEY_GROUPS, json).apply();
    }

    public List<Group> loadGroups() {
        String json = prefs.getString(KEY_GROUPS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Group>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
