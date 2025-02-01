package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.CommentActivity;
import com.example.socialmediaapp.Model.NotificationModel;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.Notification2itemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {
    ArrayList<NotificationModel> list;
    Context context;

    public NotificationAdapter(ArrayList<NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification2item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel notificationModel = list.get(position);
        String type = notificationModel.getType();
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(notificationModel.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(userModel.getProfilePhoto())
                                .placeholder(R.drawable.profile)
                                .into(holder.binding.profileimage2);
                      if(type.equals("like")){
                          holder.binding.NotificationText.setText(Html.fromHtml("<b>" + userModel.getName() +"</b>" + "  liked your post"));

                      }
                      else if(type.equals("comment")){
                          holder.binding.NotificationText.setText(Html.fromHtml("<b>" + userModel.getName() +"</b>"  + "  Commented on your post"));
                      }
                      else {
                          holder.binding.NotificationText.setText(Html.fromHtml("<b>" + userModel.getName() +"</b>" + "  started following you"));
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!type.equals("follow")){
                    FirebaseDatabase.getInstance().getReference()
                                    .child("notification")
                                            .child(notificationModel.getPostedBy())
                                                    .child(notificationModel.getNotificationID())
                                                            .child("checkOpen")
                                                                    .setValue(true);
                    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", notificationModel.getPostID());
                    intent.putExtra("postedBy", notificationModel.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });
Boolean checkOpen = notificationModel.isCheckOpen();
if (checkOpen == true){
    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
}
else{

}

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        Notification2itemBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = Notification2itemBinding.bind(itemView);
        }
    }
}
