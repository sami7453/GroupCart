package com.example.groupcart.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.user.UserModel;
import com.example.groupcart.utils.Prefs;

import java.util.List;

public class ListRecyclerViewAdapter
        extends RecyclerView.Adapter<ListRecyclerViewAdapter.ListViewHolder> {

    private Context context;
    private String groupName;
    private List<ListModel> lists;

    public ListRecyclerViewAdapter(
            Context context,
            String groupName,
            List<ListModel> lists
    ) {
        this.context   = context;
        this.groupName = groupName;
        this.lists     = lists;
    }

    @NonNull @Override
    public ListViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ListViewHolder holder,
            int position
    ) {
        ListModel list = lists.get(position);
        holder.listNameTextView
                .setText(list.getName());
        holder.productCountTextView
                .setText(String.valueOf(
                        list.getProducts().size()
                ));

        holder.deleteListButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Supprimer la liste")
                    .setMessage("Êtes-vous sûr de vouloir supprimer « "
                            + list.getName() + " » ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        Prefs prefs = Prefs.with(context);
                        String me = prefs.getCurrentUser();

                        // 1) remove from display
                        lists.remove(position);
                        notifyItemRemoved(position);

                        // 2) remove from current user's prefs
                        List<GroupModel> myGroups =
                                prefs.loadGroupsForUser(me);
                        GroupModel currentGroup = null;
                        for (GroupModel g : myGroups) {
                            if (g.getName().equals(groupName)) {
                                g.getLists().removeIf(
                                        l -> l.getName().equals(
                                                list.getName()
                                        )
                                );
                                currentGroup = g;
                                break;
                            }
                        }
                        prefs.saveGroupsForUser(me, myGroups);

                        // 3) propagate removal to other members
                        if (currentGroup != null) {
                            for (UserModel member :
                                    currentGroup.getMembers()) {
                                if (!member.getUsername().equals(me)) {
                                    List<GroupModel> memberGroups =
                                            prefs.loadGroupsForUser(
                                                    member.getUsername()
                                            );
                                    if (memberGroups != null) {
                                        for (GroupModel mg : memberGroups) {
                                            if (mg.getName().equals(
                                                    groupName
                                            )) {
                                                mg.getLists().removeIf(
                                                        l -> l.getName().equals(
                                                                list.getName()
                                                        )
                                                );
                                                break;
                                            }
                                        }
                                        prefs.saveGroupsForUser(
                                                member.getUsername(),
                                                memberGroups
                                        );
                                    }
                                }
                            }
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });
    }

    @Override public int getItemCount() {
        return lists.size();
    }

    /** Permet de mettre à jour le dataset sans recréer l'adaptateur */
    public void updateData(List<ListModel> newLists) {
        this.lists = newLists;
        notifyDataSetChanged();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView listNameTextView, productCountTextView;
        ImageButton deleteListButton;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listNameTextView      =
                    itemView.findViewById(R.id.listNameTextView);
            productCountTextView  =
                    itemView.findViewById(R.id.productCountTextView);
            deleteListButton      =
                    itemView.findViewById(R.id.deleteListButton);
        }
    }
}
