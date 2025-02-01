package com.example.socialmediaapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmediaapp.Adapter.usersAdapter;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    ArrayList<UserModel> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    FragmentSearchBinding binding;

    public SearchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        usersAdapter adapter = new usersAdapter(getContext(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.userRv.setLayoutManager(layoutManager);
        binding.userRv.setAdapter(adapter);
database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        list.clear();


            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UserModel  userModel = dataSnapshot.getValue(UserModel.class);
                    userModel.setUserID(dataSnapshot.getKey());
                    if(userModel.getUserID().equals(FirebaseAuth.getInstance().getUid()))
                    {

                    }
                    else {
                        list.add(userModel);
                    }
            }
            adapter.notifyDataSetChanged();

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

        return binding.getRoot();
    }
}