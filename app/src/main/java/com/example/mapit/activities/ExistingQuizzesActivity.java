package com.example.mapit.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mapit.R;
import com.example.mapit.adapters.QuizRecycleViewAdapter;
import com.example.mapit.classes.Quiz;
import com.example.mapit.classes.QuizItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExistingQuizzesActivity extends AppCompatActivity {

    RecyclerView quizRecycleView;
    QuizRecycleViewAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db;
    List<Quiz> quizzesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_quizzes);
        List<QuizItem> recyclerViewItems = new ArrayList<>();
        quizzesList = new ArrayList<>();
        quizRecycleView = findViewById(R.id.quizzesRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        quizRecycleView.setLayoutManager(layoutManager);
        quizRecycleView.setHasFixedSize(true);
        db = FirebaseFirestore.getInstance();
        db.collection("quizzes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            quizzesList.add(document.toObject(Quiz.class));
                            System.out.println("fetched!");
                        }
                        quizzesList.forEach(quiz -> {
                            recyclerViewItems.add(quiz.createNewItem());
                        });
                        adapter = new QuizRecycleViewAdapter(recyclerViewItems);
                        adapter.setOnItemClickListener(position -> {

                            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
                            builder.setTitle("Confirm Quiz");
                            builder.setMessage("Are you sure you want choose this quiz?");
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                Quiz selectedQuiz = quizzesList.get(position);
                                selectedQuiz.setQuizDuration(1);
                                Gson gson = new Gson();
                                String selectedQuizJson = gson.toJson(selectedQuiz);
                                Intent intent = new Intent(ExistingQuizzesActivity.this, MapsActivityChooseQuiz.class);
                                intent.putExtra("quizJSON", selectedQuizJson);
                                startActivity(intent);
                            });
                            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                            AlertDialog alert = builder.create();
                            alert.show();

                        });
                        quizRecycleView.setAdapter(adapter);
                    } else {
                        System.out.println("Error getting documents");
                    }
                });
    }
}