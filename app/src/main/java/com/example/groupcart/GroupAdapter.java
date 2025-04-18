package com.example.groupcart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;

    public GroupAdapter(List<Group> groups) {
        this.groupList = groups;
    }

    /** Met à jour la liste et notifie le RecyclerView */
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
            Intent intent = new Intent(ctx, ListActivity.class);
            intent.putExtra(ListActivity.EXTRA_GROUP, group.getName());
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
            // Assure-toi que dans item_group.xml, le TextView a bien l’ID tvListName ou tvGroupName
            tvGroupName = itemView.findViewById(R.id.textViewGroupName);
        }
    }
}
