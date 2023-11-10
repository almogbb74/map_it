package com.example.mapit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mapit.R;
import com.example.mapit.activities.ExistingQuizzesActivity;
import com.example.mapit.activities.MapsActivityChooseQuiz;
import com.example.mapit.activities.QuestionActivity;
import com.example.mapit.classes.Question;
import com.example.mapit.classes.Quiz;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Date;

public class JoinQuizTabFragment extends Fragment implements View.OnClickListener {
    EditText quizCodeEt;
    Button joinQuizBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.join_quiz_tab_fragment, container, false);
        quizCodeEt = root.findViewById(R.id.quizCodeEt);
        joinQuizBtn = root.findViewById(R.id.joinQuizBtn);
        joinQuizBtn.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View view) {


        FirebaseDatabase realTimeDb = FirebaseDatabase.getInstance("https://map-it-6a7c2-default-rtdb.europe-west1.firebasedatabase.app");
        if ((!TextUtils.isEmpty(quizCodeEt.getText().toString()))) {
            System.out.println(quizCodeEt.getText().toString());
            realTimeDb.getReference(quizCodeEt.getText().toString()).get().addOnSuccessListener(dataSnapshot -> {
                System.out.println("here");
                if (dataSnapshot.exists()) {
                    String quizId =  dataSnapshot.child("QUIZ_ID").getValue().toString();
                    System.out.println(quizId);
                    System.out.println("exists");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference quizRef = db.collection("quizzes").document(quizId);
                    quizRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Quiz quiz = documentSnapshot.toObject(Quiz.class);
                            Gson gson = new Gson();
                            String selectedQuizJson = gson.toJson(quiz);
                            Bundle extras = new Bundle();
                            Intent intent = new Intent(getActivity(), QuestionActivity.class);
                            extras.putString("quizJSON",selectedQuizJson);
                            extras.putBoolean("isHost",false);
                            intent.putExtras(extras);
                            startActivity(intent);
                        }
                    });
                }
            });
        }
        else {
            Toast.makeText(getContext(), "Please enter a valid quiz code", Toast.LENGTH_SHORT).show();
        }
    }
}
