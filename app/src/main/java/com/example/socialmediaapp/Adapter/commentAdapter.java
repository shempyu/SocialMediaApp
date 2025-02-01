package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Model.CommentModel;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.CommentRvItemBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.viewHolder>{
    Context context;
    ArrayList<CommentModel> commentModel;

    public commentAdapter(Context context, ArrayList<CommentModel> commentModel) {
        this.context = context;
        this.commentModel = commentModel;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_rv_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentModel comment = commentModel.get(position);
//        holder.binding.commentedText.setText(comment.getCommenttext());
        try {
            long timeInMillis = Long.parseLong(String.valueOf(comment.getCommentedAt()+""));
            String totalTime = TimeAgo.using(timeInMillis);
            holder.binding.commentedTime.setText(totalTime);
        } catch (Exception e) {
            holder.binding.commentedTime.setText("");
        }
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(comment.getCommentedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfilePhoto())
                                .placeholder(R.drawable.profile)
                                .into(holder.binding.commentPostedUserImage);
                        holder.binding.commentPostedName.setText(Html.fromHtml("<b>" + user.getName()+ "</b> "+ "  " + comment.getCommenttext()));



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {

        return commentModel.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        CommentRvItemBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentRvItemBinding.bind(itemView);
        }
    }
}
