package com.example.groupcart.group;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.product.ProductListActivity;
import com.example.groupcart.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;

    public GroupAdapter(List<Group> groups) {
        this.groupList = groups;
    }

    public void update(List<Group> groups) {
        this.groupList = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.tvGroupName.setText(group.getName());

        // ← Ajout du listener pour naviguer vers ListActivity
        holder.itemView.setOnClickListener(v -> {
            Context ctx = v.getContext();
            Intent intent = new Intent(ctx, ProductListActivity.class);
            intent.putExtra(ProductListActivity.EXTRA_GROUP, group.getName());
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.textViewGroupName);
        }
    }
}
