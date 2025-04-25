package com.example.groupcart.list;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.product.ProductModel;
import com.example.groupcart.user.UserModel;
import com.example.groupcart.utils.Prefs;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateListActivity extends AppCompatActivity {
    private TextInputEditText listNameEditText;
    private AutoCompleteTextView acvProduct;
    private MaterialButton addItemButton, saveListButton;
    private RecyclerView itemsRecyclerView;
    private ItemsAdapter itemsAdapter;
    private ArrayAdapter<String> suggestionAdapter;
    private List<String> items = new ArrayList<>();
    private String groupName;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        // Topbar
        MaterialToolbar toolbar = findViewById(R.id.topBar);
        toolbar.setTitle("Create a list");
        toolbar.setNavigationOnClickListener(v -> finish());

        // Récupérer le nom du groupe
        groupName = getIntent().getStringExtra(ListsActivity.EXTRA_GROUP);

        // Initialisation des vues
        listNameEditText = findViewById(R.id.etListName);
        acvProduct = findViewById(R.id.acvProduct);
        addItemButton = findViewById(R.id.addProductButton);
        saveListButton = findViewById(R.id.saveListButton);
        itemsRecyclerView = findViewById(R.id.productRecyclerView);

        // Suggestion adapter pour AutoCompleteTextView
        suggestionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        );
        acvProduct.setAdapter(suggestionAdapter);
        acvProduct.setThreshold(2);
        acvProduct.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                fetchSuggestions(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Product
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsAdapter = new ItemsAdapter(items);
        itemsRecyclerView.setAdapter(itemsAdapter);

        addItemButton.setOnClickListener(v -> {
            String prod = acvProduct.getText().toString().trim();
            if (!prod.isEmpty()) {
                items.add(prod);
                itemsAdapter.notifyItemInserted(items.size() - 1);
                acvProduct.setText("");
            }
        });

        saveListButton.setOnClickListener(v -> onSaveList());
    }

    private void fetchSuggestions(String query) {
        new Thread(() -> {
            String url = "https://world.openfoodfacts.org/cgi/search.pl"
                    + "?search_terms=" + query
                    + "&search_simple=1&json=1";
            Request req = new Request.Builder().url(url).build();
            try (Response res = client.newCall(req).execute()) {
                if (!res.isSuccessful() || res.body() == null) return;
                JsonArray prods = JsonParser.parseString(res.body().string())
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
            } catch (IOException ignored) {}
        }).start();
    }

    private void onSaveList() {
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
        String me = prefs.getCurrentUser();

        // Charger les groupes et trouver le mien
        List<GroupModel> groups = prefs.loadGroupsForUser(me);
        GroupModel myGroup = null;
        for (GroupModel g : groups) {
            if (g.getName().equals(groupName)) {
                myGroup = g;
                break;
            }
        }
        if (myGroup == null) return;

        // Créer et ajouter la nouvelle liste
        ListModel newList = new ListModel(name);
        for (String prod : items) {
            newList.getProducts().add(
                    new ProductModel(prod, 0, new ArrayList<>())
            );
        }
        myGroup.getLists().add(newList);

        // Sauvegarde pour l'utilisateur courant
        prefs.saveGroupsForUser(me, groups);

        // Propagation aux autres membres
        for (UserModel member : myGroup.getMembers()) {
            if (!member.getUsername().equals(me)) {
                List<GroupModel> memberGroups =
                        prefs.loadGroupsForUser(member.getUsername());
                if (memberGroups != null) {
                    for (GroupModel gm : memberGroups) {
                        if (gm.getName().equals(groupName)) {
                            gm.getLists().add(newList);
                            break;
                        }
                    }
                    prefs.saveGroupsForUser(member.getUsername(), memberGroups);
                }
            }
        }

        Toast.makeText(this, "Liste ajoutée !", Toast.LENGTH_SHORT).show();

        // Retour à ProductsActivity
        Intent i = new Intent(this, ListsActivity.class);
        i.putExtra(ListsActivity.EXTRA_GROUP, groupName);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    /** Adapter interne avec suppression immédiate */
    private class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.VH> {
        private final List<String> data;
        ItemsAdapter(List<String> items) { this.data = items; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            String text = data.get(pos);
            holder.text.setText(text);
            holder.deleteBtn.setOnClickListener(v -> {
                data.remove(pos);
                notifyItemRemoved(pos);
            });
        }

        @Override public int getItemCount() { return data.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView text;
            ImageButton deleteBtn;
            VH(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.productNameTextView);
                deleteBtn = itemView.findViewById(R.id.deleteProductButton);
            }
        }
    }
}
