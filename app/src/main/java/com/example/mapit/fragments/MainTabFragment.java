package com.example.mapit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mapit.R;
import com.example.mapit.activities.MapsActivityCreateQuiz;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainTabFragment extends Fragment {
    TextView mainTv;
    FloatingActionButton toCreateQuizFab;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.main_tab_fragment,container,false);
        mainTv=root.findViewById(R.id.mainTv);
        toCreateQuizFab=root.findViewById(R.id.toCreateQuizFab);
        toCreateQuizFab.setOnClickListener(view -> startActivity(new Intent(getContext(), MapsActivityCreateQuiz.class)));
        return root;
    }
}
