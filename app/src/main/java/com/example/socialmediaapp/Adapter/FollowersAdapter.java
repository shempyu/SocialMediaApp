package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Model.FollowModel;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.FreindsRvItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.viewHolder> {
    Context context;
    ArrayList<FollowModel> followlist;

    public FollowersAdapter(Context context, ArrayList<FollowModel> followlist) {
        this.context = context;
        this.followlist = followlist;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.freinds_rv_item,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FollowModel follow = followlist.get(position);
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(follow.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(userModel.getProfilePhoto())
                                .placeholder(R.drawable.profile)
                                .into(holder.binding.freindsProfileImage);
                                holder.binding.friendUsername.setText(userModel.getName());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return followlist.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        FreindsRvItemBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
         binding = FreindsRvItemBinding.bind(itemView);
        }
    }
}
