package com.example.groupcart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemberAdapter
        extends RecyclerView.Adapter<MemberAdapter.VH> {

    public interface OnDeleteListener {
        void onDelete(String username);
    }

    private List<String> members;
    private OnDeleteListener deleteListener;

    public MemberAdapter(List<String> members, OnDeleteListener listener) {
        this.members = members;
        this.deleteListener = listener;
    }

    public void update(List<String> newList) {
        this.members = newList;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String username = members.get(position);
        holder.tvUsername.setText(username);
        holder.btnRemove.setOnClickListener(v ->
                deleteListener.onDelete(username)
        );
    }

    @Override public int getItemCount() {
        return members.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView    tvUsername;
        ImageButton btnRemove;

        VH(@NonNull View itemView) {
            super(itemView);
            // IDs from item_member.xml
            tvUsername = itemView.findViewById(R.id.tvMemberUsername);
            btnRemove  = itemView.findViewById(R.id.btnRemoveMember);
        }
    }
}
