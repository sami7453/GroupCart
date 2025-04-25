package com.example.groupcart.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.product.ProductsActivity;
import com.example.groupcart.user.UserModel;
import com.example.groupcart.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.GroupViewHolder> {
    private Context context;
    private List<GroupModel> groups;

    public GroupRecyclerViewAdapter(Context context, List<GroupModel> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupModel group = groups.get(position);
        holder.groupNameTextView.setText(group.getName());
        holder.groupMembersTextView.setText(
                group.getMembers().isEmpty() ? "" : group.getMembers().get(0).getUsername()
        );

        // Clic sur l'item pour afficher les listes
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductsActivity.class);
            intent.putExtra(ProductsActivity.EXTRA_GROUP, group.getName());
            context.startActivity(intent);
        });

        // Bouton supprimer ou quitter
        holder.deleteGroupButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Supprimer le groupe")
                    .setMessage("Êtes-vous sûr de vouloir quitter ou supprimer « " + group.getName() + " » ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        String me = Prefs.with(context).getCurrentUser();
                        // 1) Supprimer de la vue
                        groups.remove(position);
                        notifyItemRemoved(position);

                        // 2) Kick ou supprimer complètement
                        if (group.getMembers().size() > 1) {
                            // Kick current user
                            List<GroupModel> myGroups = Prefs.with(context)
                                    .loadGroupsForUser(me);
                            myGroups.removeIf(g -> g.getName().equals(group.getName()));
                            Prefs.with(context).saveGroupsForUser(me, myGroups);
                            // Mettre à jour les autres membres: enlever current user
                            for (UserModel member : group.getMembers()) {
                                if (!member.getUsername().equals(me)) {
                                    List<GroupModel> mg = Prefs.with(context)
                                            .loadGroupsForUser(member.getUsername());
                                    if (mg == null) mg = new ArrayList<>();
                                    for (GroupModel gm : mg) {
                                        if (gm.getName().equals(group.getName())) {
                                            gm.getMembers().removeIf(u -> u.getUsername().equals(me));
                                            break;
                                        }
                                    }
                                    Prefs.with(context)
                                            .saveGroupsForUser(member.getUsername(), mg);
                                }
                            }
                        } else {
                            // Supprimer pour current user (seul membre)
                            List<GroupModel> myGroups = Prefs.with(context)
                                    .loadGroupsForUser(me);
                            myGroups.removeIf(g -> g.getName().equals(group.getName()));
                            Prefs.with(context).saveGroupsForUser(me, myGroups);
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView, groupMembersTextView;
        ImageButton deleteGroupButton;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            groupMembersTextView = itemView.findViewById(R.id.groupMembersTextView);
            deleteGroupButton = itemView.findViewById(R.id.deleteGroupButton);
        }
    }
}
