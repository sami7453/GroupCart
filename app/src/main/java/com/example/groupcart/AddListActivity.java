package com.example.groupcart;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.product.ProductRecyclerViewAdapter;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.product.ProductListActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddListActivity extends AppCompatActivity {
    private TextInputEditText listNameEditText;
    private AutoCompleteTextView  acvProduct;
    private MaterialButton addItemButton, saveListButton;
    private RecyclerView rvItems;

    private List<String> items = new ArrayList<>();
    private ArrayAdapter<String>  suggestionAdapter;
    private ProductRecyclerViewAdapter itemsAdapter;
    private OkHttpClient client = new OkHttpClient();
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        // Récupérer le nom du groupe passé depuis ListActivity
        groupName = getIntent().getStringExtra(ProductListActivity.EXTRA_GROUP);

        // Lier les vues
        listNameEditText = findViewById(R.id.etListName);
        acvProduct = findViewById(R.id.acvProduct);
        addItemButton = findViewById(R.id.btnAddItem);
        saveListButton = findViewById(R.id.btnSaveList);
        rvItems    = findViewById(R.id.rvListItems);

        // 1) Adapter pour l'AutoCompleteTextView
        suggestionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        );
        acvProduct.setAdapter(suggestionAdapter);
        acvProduct.setThreshold(2);
        acvProduct.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchSuggestions(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 2) RecyclerView des items ajoutés
        itemsAdapter = new ProductRecyclerViewAdapter(items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(itemsAdapter);

        // 3) Bouton “Add Item”
        addItemButton.setOnClickListener(v -> {
            String prod = acvProduct.getText().toString().trim();
            if (!prod.isEmpty()) {
                items.add(prod);
                itemsAdapter.update(items);
                acvProduct.setText("");
            }
        });

        // 4) Bouton “Save List”
        saveListButton.setOnClickListener(v -> saveList());
    }

    /** Recherche les suggestions produits via OpenFoodFacts */
    private void fetchSuggestions(String query) {
        new Thread(() -> {
            String url = "https://world.openfoodfacts.org/cgi/search.pl"
                    + "?search_terms=" + query
                    + "&search_simple=1&json=1";
            Request req = new Request.Builder().url(url).build();
            try (Response res = client.newCall(req).execute()) {
                if (!res.isSuccessful() || res.body() == null) return;
                String body = res.body().string();
                JsonArray prods = JsonParser.parseString(body)
                        .getAsJsonObject()
                        .getAsJsonArray("products");
                List<String> names = new ArrayList<>();
                for (int i = 0; i < Math.min(10, prods.size()); i++) {
                    JsonObject o = prods.get(i).getAsJsonObject();
                    if (o.has("product_name")) {
                        names.add(o.get("product_name").getAsString());
                    }
                }
                runOnUiThread(() -> {
                    suggestionAdapter.clear();
                    suggestionAdapter.addAll(names);
                    suggestionAdapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /** Sauvegarde la nouvelle liste dans le groupe courant */
    private void saveList() {
        String name = listNameEditText.getText().toString().trim();
        if (name.isEmpty() || items.isEmpty()) {
            Toast.makeText(
                    this,
                    "Nom de liste et au moins un item requis",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Prefs prefs = Prefs.with(this);
        List<GroupModel> groups = prefs.loadGroups();  // groupes de l'utilisateur courant
        for (GroupModel g : groups) {
            if (g.getName().equals(groupName)) {
                g.getLists().add(
                        new ItemList(name, new ArrayList<>(items))
                );
                break;
            }
        }
        // Enregistre pour l'utilisateur courant
        prefs.saveGroups(groups);

        Toast.makeText(this, "Liste ajoutée !", Toast.LENGTH_SHORT).show();
        finish();
    }
}
