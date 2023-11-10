package com.example.mapit.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mapit.fragments.LoginTabFragment;
import com.example.mapit.fragments.RegisterTabFragment;

public class LoginAdapter extends FragmentPagerAdapter {
    int totalTabs;

    public LoginAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new LoginTabFragment();
            case 1:
                return new RegisterTabFragment();
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
