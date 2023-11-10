package com.example.mapit.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mapit.adapters.MainAdapter;
import com.example.mapit.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainScreenActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TabLayout tabLayout;
    ViewPager viewPager;
    MainAdapter mainAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        mAuth = FirebaseAuth.getInstance();
        tabLayout=findViewById(R.id.mainTabLayout);
        viewPager=findViewById(R.id.mainViewPager);

        @SuppressLint("InflateParams") View view2 = getLayoutInflater().inflate(R.layout.tab_layout_custom, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_home);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.tab_layout_custom, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_app_icon_black);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mainAdapter=new MainAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(mainAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        builder.setTitle("Sign out");
        builder.setMessage("Are you sure you want to sign out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            mAuth.signOut();
            dialog.dismiss();
            Toast.makeText(getApplicationContext(),"Signed out!",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}