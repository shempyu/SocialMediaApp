package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.CommentActivity;
import com.example.socialmediaapp.Model.LikeModel;
import com.example.socialmediaapp.Model.NotificationModel;
import com.example.socialmediaapp.Model.PostModel;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.HomePostRvItemBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.viewHolder> {
    ArrayList<PostModel> list;
    Context context;

    public HomePostAdapter(ArrayList<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_post_rv_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostModel postmodel = list.get(position);
        Picasso.get()
                .load(postmodel.getPostImage())
                .placeholder(R.drawable.profile)
                .into(holder.binding.postImage);
        holder.binding.captionpost.setText(postmodel.getPostDescription());

        // Update comment count with real-time data
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(postmodel.getPostId())
                .child("commentCount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            long commentCount = (long) snapshot.getValue();
                            holder.binding.commentCountTextView.setText(String.valueOf(commentCount));
                        } else {
                            holder.binding.commentCountTextView.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        try {
            long timeInMillis = Long.parseLong(String.valueOf(postmodel.getPostedAt()));
            String totalTime = TimeAgo.using(timeInMillis);
            holder.binding.posttime.setText(totalTime);
        } catch (Exception e) {
            holder.binding.posttime.setText("");
        }

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(postmodel.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel usermodel = snapshot.getValue(UserModel.class);
                        if (usermodel != null) {
                            Picasso.get()
                                    .load(usermodel.getProfilePhoto())
                                    .placeholder(R.drawable.profile).into(holder.binding.profileimage2);
                            holder.binding.postuserfullname.setText(usermodel.getName());
                            holder.binding.userInfo.setText(usermodel.getProfession());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        holder.binding.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", postmodel.getPostId());
                intent.putExtra("postedBy", postmodel.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference()
                .child("Like")
                .child(postmodel.getPostId())
                .child("likedby")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.likeButton.setImageResource(R.drawable.like);
                        } else {
                            holder.binding.likeButton.setImageResource(R.drawable.heart);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        // Fetch and set like count
        FirebaseDatabase.getInstance().getReference()
                .child("Like")
                .child(postmodel.getPostId())
                .child("likeCount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            long likeCount = (long) snapshot.getValue();
                            holder.binding.likeCountTextView.setText(String.valueOf(likeCount));
                        } else {
                            holder.binding.likeCountTextView.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        holder.binding.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getUid() == null) return;
                FirebaseDatabase.getInstance().getReference()
                        .child("Like")
                        .child(postmodel.getPostId())
                        .child("likedby")
                        .child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Unlike and decrement like count
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Like")
                                            .child(postmodel.getPostId())
                                            .child("likedby")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Like")
                                                            .child(postmodel.getPostId())
                                                            .child("likeCount")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    long likeCount = snapshot.exists() ? (long) snapshot.getValue() : 0;
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Like")
                                                                            .child(postmodel.getPostId())
                                                                            .child("likeCount")
                                                                            .setValue(likeCount - 1)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    holder.binding.likeCountTextView.setText(String.valueOf(likeCount - 1));
                                                                                    holder.binding.likeButton.setImageResource(R.drawable.heart);
                                                                                }
                                                                            });
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                }
                                                            });
                                                }
                                            });
                                } else {
                                    LikeModel likeModel = new LikeModel();
                                    likeModel.setLikedBy(FirebaseAuth.getInstance().getUid());
                                    likeModel.setLikedAt(new Date().getTime());
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Like")
                                            .child(postmodel.getPostId())
                                            .child("likedby")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(likeModel)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Like")
                                                            .child(postmodel.getPostId())
                                                            .child("likeCount")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    long likeCount = snapshot.exists() ? (long) snapshot.getValue() : 0;
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Like")
                                                                            .child(postmodel.getPostId())
                                                                            .child("likeCount")
                                                                            .setValue(likeCount + 1)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    holder.binding.likeCountTextView.setText(String.valueOf(likeCount + 1));
                                                                                    holder.binding.likeButton.setImageResource(R.drawable.like);
                                                                                    Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                                                                                    // Send notification
                                                                                    NotificationModel notificationModel = new NotificationModel();
                                                                                    notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                    notificationModel.setNotificationAt(new Date().getTime());
                                                                                    notificationModel.setPostedBy(postmodel.getPostedBy());
                                                                                    notificationModel.setPostID(postmodel.getPostId());
                                                                                    notificationModel.setType("like");

                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child("notification")
                                                                                            .child(postmodel.getPostedBy())
                                                                                            .push()
                                                                                            .setValue(notificationModel);
                                                                                }
                                                                            });
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
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
        HomePostRvItemBinding binding;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomePostRvItemBinding.bind(itemView);

        }
    }


}
