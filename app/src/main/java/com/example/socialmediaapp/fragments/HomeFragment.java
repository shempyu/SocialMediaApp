package com.example.socialmediaapp.fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Adapter.HomePostAdapter;
import com.example.socialmediaapp.Adapter.StoryAdapter;
import com.example.socialmediaapp.Model.PostModel;
import com.example.socialmediaapp.Model.StoryModel;
import com.example.socialmediaapp.Model.UserStories;
import com.example.socialmediaapp.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {
    RecyclerView storyRv,homePostRv;
    ArrayList<StoryModel> list;

    ArrayList<PostModel> postlist;
     FirebaseDatabase database;
     FirebaseAuth auth;
    FragmentHomeBinding binding;
     ActivityResultLauncher<String>  galleryLauncher;
     FirebaseStorage storage;
     CircleImageView addStoryImage;

     ProgressDialog dialog;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog  =new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
       //story Recyclerview
        storyRv = binding.StoryRv;
        list = new ArrayList<>();

        StoryAdapter adapter = new StoryAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        storyRv.setLayoutManager(linearLayoutManager);
        storyRv.setNestedScrollingEnabled(false);
        storyRv.setAdapter(adapter);

        database.getReference()
                .child("stories").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if(snapshot.exists()){
                          list.clear();
                          for(DataSnapshot storySnapshot : snapshot.getChildren()){
                              StoryModel storyModel = new StoryModel();
                              storyModel.setStoryBy(storySnapshot.getKey());
                              storyModel.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));

                              ArrayList<UserStories> stories = new ArrayList<>();
                              for(DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()){
                                  UserStories userStories = snapshot1.getValue(UserStories.class);
                                  stories.add(userStories);

                              }
                              storyModel.setStories(stories);
                              list.add(storyModel);
                          }
                          adapter.notifyDataSetChanged();
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //homepostadapter recyclerview

        postlist = new ArrayList<>();
       homePostRv = binding.homePostRv;
        HomePostAdapter postAdapter = new HomePostAdapter(postlist,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        homePostRv.setLayoutManager(layoutManager);
        homePostRv.addItemDecoration(new DividerItemDecoration(homePostRv.getContext(),DividerItemDecoration.HORIZONTAL));
        homePostRv.setNestedScrollingEnabled(false);
        homePostRv.setAdapter(postAdapter);

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PostModel postmodel = dataSnapshot.getValue(PostModel.class);
                    postmodel.setPostId(dataSnapshot.getKey());
                    postlist.add(postmodel);

                }

                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //ADDING STORY
        addStoryImage = binding.UserStoryImage;
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 galleryLauncher.launch("image/*");
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                addStoryImage.setImageURI(result);
                dialog.show();
                final StorageReference reference = storage.getReference()
                        .child("stories")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime()+"");
                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               StoryModel story = new StoryModel();
                               story.setStoryAt(new Date().getTime());
                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("postedBy")
                                        .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                UserStories userstories = new UserStories(uri.toString(), story.getStoryAt());

                                                database.getReference()
                                                        .child("stories")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("userStories")
                                                        .push()
                                                        .setValue(userstories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        });
                           }
                       });
                    }
                });

            }

        });

        return binding.getRoot();
    }
}