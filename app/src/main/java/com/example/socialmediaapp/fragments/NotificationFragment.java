package com.example.socialmediaapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmediaapp.Adapter.ViewPagerAdapter;
import com.example.socialmediaapp.R;
import com.google.android.material.tabs.TabLayout;


public class NotificationFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;



    public NotificationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        viewPager = view.findViewById(R.id.viewpagerNotification);
        viewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));

        tabLayout = view.findViewById(R.id.tablayoutNotification);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}