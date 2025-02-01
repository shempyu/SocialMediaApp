package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Model.FollowModel;
import com.example.socialmediaapp.Model.NotificationModel;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.SearchItemBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class usersAdapter extends RecyclerView.Adapter<usersAdapter.viewHolder> {
    Context context;
    ArrayList<UserModel> list;

    public usersAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        UserModel userModel = list.get(position);
        Picasso.get()
                .load(userModel.getProfilePhoto())
                .placeholder(R.drawable.boy)
                .into(holder.binding.postProfileImage);
        holder.binding.postUsername.setText(userModel.getName());
        holder.binding.postProfession.setText(userModel.getProfession());

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userModel.getUserID())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.follow.setText("Unfollow");
                        } else {
                            holder.binding.follow.setText("Follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        holder.binding.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserId = FirebaseAuth.getInstance().getUid();
                if (currentUserId == null) return;

                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(userModel.getUserID())
                        .child("followers")
                        .child(currentUserId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Unfollow the user
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(userModel.getUserID())
                                            .child("followers")
                                            .child(currentUserId)
                                            .removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    holder.binding.follow.setText("Follow");
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(userModel.getUserID())
                                                            .child("followerCount")
                                                            .setValue(userModel.getFollowerCount() - 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(context, "Unfollowed " + userModel.getName(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });
                                } else {
                                    // Follow the user
                                    FollowModel followModel = new FollowModel();
                                    followModel.setFollowedBy(currentUserId);
                                    followModel.setFollowedAt(new Date().getTime());

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(userModel.getUserID())
                                            .child("followers")
                                            .child(currentUserId)
                                            .setValue(followModel)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    holder.binding.follow.setText("Unfollow");
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(userModel.getUserID())
                                                            .child("followerCount")
                                                            .setValue(userModel.getFollowerCount() + 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(context, "Followed " + userModel.getName(), Toast.LENGTH_SHORT).show();
                                                                    NotificationModel notificationModel = new NotificationModel();
                                                                    notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notificationModel.setNotificationAt(new Date().getTime());
                                                                    notificationModel.setType("follow");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(userModel.getUserID())
                                                                            .push()
                                                                            .setValue(notificationModel);
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        SearchItemBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchItemBinding.bind(itemView);
        }
    }
}
