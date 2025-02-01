package com.example.socialmediaapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Model.StoryModel;
import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.Model.UserStories;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.StoryRvItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {
    ArrayList<StoryModel> list;
    Context context;

    public StoryAdapter(ArrayList<StoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_rv_item, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        StoryModel story = list.get(position);
        if (story.getStories().size() > 0) {
            UserStories lastStory = story.getStories().get(story.getStories().size() - 1);
            Picasso.get().load(lastStory.getImage()).into(holder.binding.storyimage);
            holder.binding.statusCircle.setPortionsCount(story.getStories().size());
            FirebaseDatabase.getInstance().getReference().child("Users").child(story.getStoryBy()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    Picasso.get().load(userModel.getProfilePhoto()).placeholder(R.drawable.profile).into(holder.binding.UserImage);
                    holder.binding.nameUser.setText(userModel.getName());
                    //story viweing
                    holder.binding.storyimage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<MyStory> myStories = new ArrayList<>();

                            for (UserStories stories : story.getStories()) {
                                long timestamp = stories.getStoryAt(); // Assuming getStoryAt() returns a long timestamp
                                Date date = new Date(timestamp); // Convert timestamp to Date
                                myStories.add(new MyStory(
                                        stories.getImage(),date

                                ));
                            }
                            new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager()).setStoriesList(myStories) // Required
                                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                    .setTitleText(userModel.getName()) // Default is Hidden
                                    .setSubtitleText("") // Default is Hidden
                                    .setTitleLogoUrl(userModel.getProfilePhoto()) // Default is Hidden
                                    .setStoryClickListeners(new StoryClickListeners() {
                                        @Override
                                        public void onDescriptionClickListener(int position) {
                                            //your action
                                        }

                                        @Override
                                        public void onTitleIconClickListener(int position) {
                                            //your action
                                        }
                                    }) // Optional Listeners
                                    .build() // Must be called before calling show method
                                    .show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        StoryRvItemBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StoryRvItemBinding.bind(itemView);

        }


    }
}
