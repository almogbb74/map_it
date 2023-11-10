package com.example.mapit.fragments;

import android.content.Intent;
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
import com.example.mapit.activities.MainScreenActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginTabFragment extends Fragment {
    EditText emailEt,passwordEt;
    Button loginBtn;
    FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment,container,false);
        mAuth = FirebaseAuth.getInstance();
        emailEt = root.findViewById(R.id.loginEmail);
        passwordEt = root.findViewById(R.id.loginPassword);
        loginBtn = root.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(view -> loginUser());
        return root;
    }

    private void loginUser() {
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
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(),"Login Succeeded!",Toast.LENGTH_LONG).show();
                    System.out.println(mAuth.getCurrentUser());
                    startActivity(new Intent(getContext(), MainScreenActivity.class));
                }
                else
                    Toast.makeText(getActivity(),"Login Failed: " + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
            });
        }
    }
}
