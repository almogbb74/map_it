package com.example.mapit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.mapit.R;
import com.example.mapit.classes.Quiz;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Date;

public class QuizLobbyActivity extends AppCompatActivity {
    Quiz selectedQuiz;
    Button playQuizBtn;
    TextView quizCodeTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_lobby);
        Gson gson = new Gson();
        String quizJson = getIntent().getStringExtra("quizJSON");
        selectedQuiz = gson.fromJson(quizJson, Quiz.class);
        playQuizBtn = findViewById(R.id.lobbyPlayBtn);
        quizCodeTv = findViewById(R.id.quizCodeTv);
        String textViewString = String.format("Quiz code: %s",selectedQuiz.getQuizId());
        quizCodeTv.setText(textViewString);
        playQuizBtn.setOnClickListener(view -> {
            System.out.println(selectedQuiz.getQuizId());
            Intent intent = new Intent(QuizLobbyActivity.this,QuestionActivity.class);
            Bundle extras = new Bundle();
            extras.putString("quizJSON",gson.toJson(selectedQuiz));
            extras.putBoolean("isHost",true);
            intent.putExtras(extras);
            Date date = new Date();
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://map-it-6a7c2-default-rtdb.europe-west1.firebasedatabase.app");
            database.getReference(selectedQuiz.getQuizId()).child("START_TIMESTAMP").setValue(date).addOnCompleteListener
                    (task -> System.out.println("TIMESTAMP STORED"));
            startActivity(intent);
            date.setMinutes(date.getMinutes() + selectedQuiz.getQuizDuration());
            database.getReference(selectedQuiz.getQuizId()).child("END_TIMESTAMP").setValue(date).addOnCompleteListener
                    (task -> System.out.println("TIMESTAMP STORED"));
            database.getReference(selectedQuiz.getQuizId()).child("QUIZ_ID").setValue(selectedQuiz.getQuizId()).addOnCompleteListener
                    (task -> System.out.println("ID STORED"));
            startActivity(intent);
        });
    }
}