package com.example.socialmediaapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmediaapp.Adapter.FollowersAdapter;
import com.example.socialmediaapp.HomeActivity;
import com.example.socialmediaapp.Login;
import com.example.socialmediaapp.Model.FollowModel;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
     FragmentProfileBinding binding;
     FirebaseStorage storage;
     FirebaseDatabase database;
     FirebaseAuth auth;

     ArrayList<FollowModel> list = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding  = FragmentProfileBinding.inflate(inflater, container, false);
        FollowersAdapter adapter = new FollowersAdapter(getContext(),list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        binding.followRv.setLayoutManager(layoutManager);
        binding.followRv.setAdapter(adapter);

        //Getting data for users followers
        database.getReference().child("Users")
                        .child(auth.getUid())
                                .child("followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            FollowModel followModel = dataSnapshot.getValue(FollowModel.class);
                            list.add(followModel);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //Fetch User data from database
        database.getReference().child("Users").child(auth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    UserModel user = snapshot.getValue(UserModel.class);
                                    //fetching image and setting
                                    Picasso.get()
                                            .load(user.getProfilePhoto())
                                            .placeholder(R.drawable.profile).into(binding.profileimage2);
                                    binding.completename.setText(user.getName());
                                    binding.Profession.setText(user.getProfession());
                                    binding.followersNo.setText(user.getFollowerCount()+"");

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


        binding.profileimage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,11);
            }

        });
        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 11 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            final StorageReference reference = storage.getReference()
                    .child("profile_photo")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Update the user's profile photo URL in the database
                            database.getReference()
                                    .child("Users")
                                    .child(auth.getUid())
                                    .child("profilePhoto")
                                    .setValue(uri.toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Profile photo saved", Toast.LENGTH_SHORT).show();
                                            // Load the updated profile photo using Picasso
                                            Picasso.get().load(uri).placeholder(R.drawable.profile).into(binding.profileimage2);
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

}