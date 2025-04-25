package com.example.groupcart.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.group.GroupsActivity;
import com.example.groupcart.product.ProductsActivity;
import com.example.groupcart.user.UserModel;
import com.example.groupcart.utils.Prefs;

import java.util.List;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ListViewHolder> {
    private Context context;
    private String groupName;
    private List<ListModel> lists;

    public ListRecyclerViewAdapter(Context context, String groupName, List<ListModel> lists) {
        this.context = context;
        this.groupName = groupName;
        this.lists = lists;
    }

    @NonNull @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ListModel list = lists.get(position);
        holder.listNameTextView.setText(list.getName());
        holder.productCountTextView.setText(list.getProducts().size() + " products");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductsActivity.class);
            intent.putExtra(ProductsActivity.EXTRA_LIST_NAME, list.getName());
            context.startActivity(intent);
        });

        holder.listNameTextView.setOnClickListener(v -> {
            EditText editText = new EditText(context);
            editText.setText(holder.listNameTextView.getText());

            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Edit Text")
                    .setView(editText)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        String newText = editText.getText().toString();
                        holder.listNameTextView.setText(newText);
                        // Prefs.with(context).saveGroups(lists);
                    })
                    .setNegativeButton("Cancel", null)
                    .create();

            dialog.show();
        });

        holder.deleteListButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete list")
                    .setMessage("Are you sure you want to delete list '" + list.getName() + "'?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Prefs prefs = Prefs.with(context);
                        String me = prefs.getCurrentUser();

                        // 1) remove from display
                        lists.remove(position);
                        notifyItemRemoved(position);

                        // 2) remove from current user's prefs
                        List<GroupModel> myGroups = prefs.loadGroupsForUser(me);
                        GroupModel currentGroup = null;
                        for (GroupModel g : myGroups) {
                            if (g.getName().equals(groupName)) {
                                g.getLists().removeIf(l -> l.getName().equals(list.getName()));
                                currentGroup = g;
                                break;
                            }
                        }
                        prefs.saveGroupsForUser(me, myGroups);

                        // 3) propagate removal to other members
                        if (currentGroup != null) {
                            for (UserModel member : currentGroup.getMembers()) {
                                if (!member.getUsername().equals(me)) {
                                    List<GroupModel> memberGroups = prefs.loadGroupsForUser(member.getUsername());
                                    if (memberGroups != null) {
                                        for (GroupModel mg : memberGroups) {
                                            if (mg.getName().equals(groupName)) {
                                                mg.getLists().removeIf(l -> l.getName().equals(list.getName()));
                                                break;
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

    @Override public int getItemCount() {
        return lists.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView listNameTextView, productCountTextView;
        ImageButton deleteListButton;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listNameTextView = itemView.findViewById(R.id.listNameTextView);
            productCountTextView = itemView.findViewById(R.id.productCountTextView);
            deleteListButton = itemView.findViewById(R.id.deleteListButton);
        }
    }
}
