package com.example.groupcart.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.groupcart.group.GroupModel;
import com.example.groupcart.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Prefs {
    private static final String SHARED_PREFS      = "app_prefs";
    private static final String KEY_USERS         = "key_users";           // JSON List<User>
    private static final String KEY_CURRENT_USER  = "key_current_user";    // username
    private static final String KEY_GROUPS_PREFIX = "key_groups_";         // suffixe + username

    private SharedPreferences prefs;
    private Gson gson = new Gson();

    private Prefs(Context ctx) {
        prefs = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    /** Récupère une instance liée au contexte */
    public static Prefs with(Context ctx) {
        return new Prefs(ctx);
    }

    // -------------------
    // GESTION DES UTILISATEURS
    // -------------------

    /** Charge tous les utilisateurs enregistrés */
    public List<UserModel> loadUsers() {
        String json = prefs.getString(KEY_USERS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<UserModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /** Sauvegarde la liste complète des utilisateurs */
    public void saveUsers(List<UserModel> users) {
        String json = gson.toJson(users);
        prefs.edit()
                .putString(KEY_USERS, json)
                .apply();
    }

    /**
     * Ajoute un nouvel utilisateur si le username n'existe pas déjà.
     * @return true si ajouté, false si username déjà pris
     */
    public boolean addUser(UserModel u) {
        List<UserModel> users = loadUsers();
        for (UserModel x : users) {
            if (x.getUsername().equals(u.getUsername())) {
                return false;
            }
        }
        users.add(u);
        saveUsers(users);
        return true;
    }

    /**
     * Vérifie credentials, et si OK, enregistre l'utilisateur courant.
     * @return true si authentifié
     */
    public boolean checkCredentials(String username, String password) {
        for (UserModel u : loadUsers()) {
            if (u.getUsername().equals(username)
                    && u.getPassword().equals(password)) {
                setCurrentUser(username);
                return true;
            }
        }
        return false;
    }

    /** Définit l'utilisateur actuellement connecté */
    public void setCurrentUser(String username) {
        prefs.edit()
                .putString(KEY_CURRENT_USER, username)
                .apply();
    }

    /** Récupère l'utilisateur actuellement connecté, ou null */
    public String getCurrentUser() {
        return prefs.getString(KEY_CURRENT_USER, null);
    }

    /** Déconnecte l'utilisateur courant */
    public void clearCurrentUser() {
        prefs.edit()
                .remove(KEY_CURRENT_USER)
                .apply();
    }

    // -------------------
    // GESTION DES GROUPES PAR UTILISATEUR
    // -------------------

    /** Clé de stockage des groupes pour un username donné */
    private String keyGroupsFor(String username) {
        return KEY_GROUPS_PREFIX + username;
    }

    /**
     * Charge la liste de groupes d'un utilisateur arbitraire.
     * @param username le nom d'utilisateur cible
     */
    public List<GroupModel> loadGroupsForUser(String username) {
        String json = prefs.getString(keyGroupsFor(username), null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<GroupModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Sauvegarde la liste de groupes d'un utilisateur arbitraire.
     * @param username le nom d'utilisateur cible
     * @param groups   liste de Group à stocker
     */
    public void saveGroupsForUser(String username, List<GroupModel> groups) {
        String json = gson.toJson(groups);
        prefs.edit()
                .putString(keyGroupsFor(username), json)
                .apply();
    }

    /**
     * Charge la liste de groupes de l'utilisateur courant.
     */
    public List<GroupModel> loadGroups() {
        String me = getCurrentUser();
        if (me == null) {
            return new ArrayList<>();
        }
        return loadGroupsForUser(me);
    }

    /**
     * Sauvegarde la liste de groupes de l'utilisateur courant.
     */
    public void saveGroups(List<GroupModel> groups) {
        String me = getCurrentUser();
        if (me != null) {
            saveGroupsForUser(me, groups);
        }
    }
}
