package com.example.mapit.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mapit.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterTabFragment extends Fragment {
    EditText passwordEt,emailEt;
    Button registerBtn;
    FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.register_tab_fragment,container,false);
        mAuth = FirebaseAuth.getInstance();
        emailEt = root.findViewById(R.id.registerEmail);
        passwordEt = root.findViewById(R.id.registerPassword);
        registerBtn = root.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(view -> createUser());
        return root;
    }

    private void createUser() {
        String email = emailEt.getText().toString(),password=passwordEt.getText().toString();
        if (TextUtils.isEmpty(email)){
            emailEt.setError("Email cannot be empty");
            emailEt.requestFocus();
        }
        else if (TextUtils.isEmpty(password)){
                passwordEt.setError("Password cannot be empty");
                passwordEt.requestFocus();
            }
        else {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    Toast.makeText(getActivity(),"User Created! Login required",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity(),"Registration Error: " + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
            });
        }
    }
}