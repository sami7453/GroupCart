package com.example.groupcart.product;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.list.ListModel;
import com.example.groupcart.user.UserModel;
import com.example.groupcart.utils.Prefs;

import java.util.List;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductViewHolder> {
    private Context context;
    private ListModel list;
    private List<ProductModel> products;

    public ProductRecyclerViewAdapter(Context context, ListModel list, List<ProductModel> products) {
        this.context = context;
        this.list = list;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = products.get(position);
        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText("$" + Float.toString(product.getPrice()));

        setBackgroundColor(holder.itemView, product.getStatus());

        holder.itemView.setOnClickListener(v -> {
            int newState = (product.getStatus() + 1) % 3;
            product.setStatus(newState);
            setBackgroundColor(v, newState);
        });

        holder.deleteProductButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete product")
                    .setMessage("Are you sure you want to delete product '" + product.getName() + "'?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Prefs prefs = Prefs.with(context);
                        String me = prefs.getCurrentUser();

                        // 1) Remove from current list and notify
                        list.getProducts().remove(product);
                        notifyItemRemoved(holder.getAdapterPosition());

                        // 2) Update current user's prefs
                        List<GroupModel> myGroups = prefs.loadGroupsForUser(me);
                        GroupModel currentGroup = null;

                        for (GroupModel g : myGroups) {
                            for (ListModel l : g.getLists()) {
                                if (l.getName().equals(list.getName())) {
                                    l.getProducts().removeIf(p -> p.getName().equals(product.getName()));
                                    currentGroup = g;
                                }
                            }
                        }

                        prefs.saveGroupsForUser(me, myGroups);

                        // 3) Update other group members
                        if (currentGroup != null) {
                            for (UserModel member : currentGroup.getMembers()) {
                                if (!member.getUsername().equals(me)) {
                                    List<GroupModel> memberGroups = prefs.loadGroupsForUser(member.getUsername());
                                    if (memberGroups != null) {
                                        for (GroupModel mg : memberGroups) {
                                            for (ListModel ml : mg.getLists()) {
                                                if (ml.getName().equals(list.getName())) {
                                                    ml.getProducts().removeIf(p -> p.getName().equals(product.getName()));
                                                }
                                            }
                                        }
                                        prefs.saveGroupsForUser(member.getUsername(), memberGroups);
                                    }
                                }
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void setBackgroundColor(View view, int state) {
        int color;
        switch (state) {
            case 1: color = Color.parseColor("#FFC107"); break; // orange
            case 2: color = Color.parseColor("#66BB6A"); break; // green
            default: color = Color.parseColor("#E9E4DF"); break; // white
        }
        view.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView, productPriceTextView;
        ImageButton deleteProductButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            deleteProductButton = itemView.findViewById(R.id.deleteProductButton);
        }
    }
}
