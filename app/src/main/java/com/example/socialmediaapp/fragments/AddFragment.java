package com.example.socialmediaapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmediaapp.HomeActivity;
import com.example.socialmediaapp.Model.PostModel;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.FragmentAddBinding;
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

import java.util.Date;


public class AddFragment extends Fragment {

    FragmentAddBinding binding;
    Uri uri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;



    public AddFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Post Uploading");
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        database.getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            UserModel user = snapshot.getValue(UserModel.class);
                            //loading image from database
                            Picasso.get()
                                    .load(user.getProfilePhoto())
                                    .placeholder(R.drawable.profile)
                                    .into(binding.postProfileImage);
                            binding.postUsername.setText(user.getName());
                            binding.postProfession.setText(user.getProfession());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.captionAddPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String caption = binding.captionAddPost.getText().toString();
                if(!caption.isEmpty()){
                    binding.postBtn.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
                    binding.postBtn.setEnabled(true);
                }
                else{
                    binding.postBtn.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                    binding.postBtn.setEnabled(false);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });
binding.postBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        dialog.show();
        final StorageReference reference = storage.getReference().child("posts")
                .child(FirebaseAuth.getInstance().getUid())
                .child(new Date().getTime()+"");
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        PostModel postmodel = new PostModel();
                        postmodel.setPostImage(uri.toString());
                        postmodel.setPostedBy(FirebaseAuth.getInstance().getUid());
                        postmodel.setPostDescription(binding.captionAddPost.getText().toString());
                        postmodel.setPostedAt(System.currentTimeMillis());
                        database.getReference().child("posts")
                                .push()
                                .setValue(postmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "post Uploaded", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        startActivity(new Intent(getContext(),HomeActivity.class));

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData() != null){
            uri = data.getData();
            binding.selectImage.setImageURI(uri);
            //
            binding.postBtn.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
            binding.postBtn.setEnabled(true);
        }
    }
}