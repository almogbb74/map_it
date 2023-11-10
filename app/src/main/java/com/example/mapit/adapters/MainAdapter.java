package com.example.mapit.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mapit.fragments.JoinQuizTabFragment;
import com.example.mapit.fragments.MainTabFragment;

public class MainAdapter extends FragmentPagerAdapter {
    int totalTabs;

    public MainAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MainTabFragment();
            case 1:
                return new JoinQuizTabFragment();
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
