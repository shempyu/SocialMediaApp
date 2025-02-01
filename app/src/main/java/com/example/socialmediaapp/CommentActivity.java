package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.socialmediaapp.Adapter.HomePostAdapter;
import com.example.socialmediaapp.Adapter.commentAdapter;
import com.example.socialmediaapp.Model.CommentModel;
import com.example.socialmediaapp.Model.NotificationModel;
import com.example.socialmediaapp.Model.PostModel;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.databinding.ActivityCommentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding binding;
    Intent intent;
    String postId;
    String postedBy;



    private HomePostAdapter adapter;
    FirebaseDatabase database;
    FirebaseAuth auth;
    RecyclerView commentRv;
    ArrayList<CommentModel> commentlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();
//back button
        setSupportActionBar(binding.toolbar2);
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");
        // Receive the adapter reference from intent
       //adapter = (HomePostAdapter) intent.getSerializableExtra("adapter");

        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostModel postmodel = snapshot.getValue(PostModel.class);

                            Picasso.get()
                                    .load(postmodel.getPostImage())
                                    .placeholder(R.drawable.profile)
                                    .into(binding.commentPostImage);
                            binding.captionPostcomment.setText(postmodel.getPostDescription() + "");
                            binding.commentNumber.setText(postmodel.getCommentCount() + "");

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });

        database.getReference()
                .child("Users")
                .child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel usermodel = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(usermodel.getProfilePhoto())
                                .placeholder(R.drawable.profile)
                                .into(binding.commentUserImage);
                        binding.commentUserName.setText(usermodel.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentModel commentmodel = new CommentModel();
                commentmodel.setCommentedAt(System.currentTimeMillis());
                commentmodel.setCommentedBy(FirebaseAuth.getInstance().getUid());
                commentmodel.setCommenttext(binding.commentWrite.getText().toString());
                database.getReference().child("posts").child(postId).child("comment").push()
                        .setValue(commentmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentCount = 0;
                                                if (snapshot.exists()) {
                                                    commentCount = snapshot.getValue(Integer.class);
                                                }
                                                database.getReference()
                                                        .child("posts")
                                                        .child(postId)
                                                        .child("commentCount").setValue(commentCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                binding.commentWrite.setText("");
                                                                Toast.makeText(CommentActivity.this, "commented", Toast.LENGTH_SHORT).show();

                                                                //notification
                                                                NotificationModel notificationModel = new NotificationModel();
                                                                notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                notificationModel.setNotificationAt(new Date().getTime());
                                                                notificationModel.setPostID(postId);
                                                                notificationModel.setPostedBy(postedBy);
                                                                notificationModel.setType("comment");

                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("notification")
                                                                        .child(postedBy)
                                                                        .push()
                                                                        .setValue(notificationModel);


                                                            }
                                                        });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                Toast.makeText(CommentActivity.this, "comment Uploaded", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

        commentAdapter adapter  = new commentAdapter(this,commentlist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.commentRvs.setLayoutManager(layoutManager);
        binding.commentRvs.setAdapter(adapter);
database.getReference().child("posts")
        .child(postId)
        .child("comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentlist.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CommentModel comment = dataSnapshot.getValue(CommentModel.class);
                    commentlist.add(comment);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}