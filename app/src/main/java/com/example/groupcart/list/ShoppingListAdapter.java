package com.example.groupcart.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.item.ItemList;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.VH> {

    private List<ItemList> shoppingLists;

    public ShoppingListAdapter(List<ItemList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public void update(List<ItemList> newLists) {
        this.shoppingLists = newLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ItemList list = shoppingLists.get(position);
        holder.tvListName.setText(list.getName());
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvListName;
        // TextView tvItemCount; // si tu veux montrer le count

        VH(@NonNull View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tvListName);
            // tvItemCount = itemView.findViewById(R.id.tvItemCount);
        }
    }
}
