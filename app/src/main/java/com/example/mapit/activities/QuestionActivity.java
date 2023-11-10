package com.example.mapit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapit.R;
import com.example.mapit.classes.Quiz;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class QuestionActivity extends AppCompatActivity {
    Quiz currentQuiz;
    TextView timerTv, questionTv, questionIndexTv;
    Button ans1Btn, ans2Btn, ans3Btn, ans4Btn;
    int currentQuestionIndex = 0, score = 0;
    boolean isHost;
    CountDownTimer countDownTimer;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //TODO: isHost and calculate score
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        timerTv = findViewById(R.id.countDownTimerTv);
        questionTv = findViewById(R.id.inQuizQuestionTv);
        questionIndexTv = findViewById(R.id.questionIndexTv);
        ans1Btn = findViewById(R.id.inQuizAns1);
        ans2Btn = findViewById(R.id.inQuizAns2);
        ans3Btn = findViewById(R.id.inQuizAns3);
        ans4Btn = findViewById(R.id.inQuizAns4);

        Gson gson = new Gson();
        db = FirebaseDatabase.getInstance("https://map-it-6a7c2-default-rtdb.europe-west1.firebasedatabase.app");
        Bundle extras = getIntent().getExtras();
        String quizJson = extras.getString("quizJSON");
        isHost = extras.getBoolean("isHost");
        currentQuiz = gson.fromJson(quizJson, Quiz.class);
        System.out.println("size" + currentQuiz.getQuestionsList().size());
        System.out.println(isHost);
        if (!isHost) {
            fillQuestions();
        } else {
            questionTv.setText(R.string.wait);
            questionIndexTv.setVisibility(View.INVISIBLE);
            ans1Btn.setVisibility(View.INVISIBLE);
            ans2Btn.setVisibility(View.INVISIBLE);
            ans3Btn.setVisibility(View.INVISIBLE);
            ans4Btn.setVisibility(View.INVISIBLE);
        }
        startTimer();
        ans1Btn.setOnClickListener(view -> { //TODO: update questions
            checkAnswer(1);
            currentQuestionIndex++;
            fillQuestions();
        });

        ans2Btn.setOnClickListener(view -> {
            checkAnswer(2);
            currentQuestionIndex++;
            fillQuestions();
        });

        ans3Btn.setOnClickListener(view -> {
            checkAnswer(3);
            currentQuestionIndex++;
            fillQuestions();
        });

        ans4Btn.setOnClickListener(view -> {
            checkAnswer(4);
            currentQuestionIndex++;
            fillQuestions();
        });

    }

    private void startTimer() {

        db.getReference(currentQuiz.getQuizId()).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                System.out.println(dataSnapshot.getRef());
                System.out.println(dataSnapshot);
                Date endTimestamp = dataSnapshot.child("END_TIMESTAMP").getValue(Date.class);
                System.out.println(endTimestamp);
                Date now = new Date();
                countDownTimer = new CountDownTimer(endTimestamp.getTime() - now.getTime(), 1000) {
                    @Override
                    public void onTick(long l) {
                        updateTextView(l);
                    }

                    @Override
                    public void onFinish() { //TODO: When the player finish all questions- notify
                        if (isHost) {
                            db.getReference(currentQuiz.getQuizId()).child("clients").child("scores").get().addOnSuccessListener(dataSnapshot1 -> {
                                HashMap<String,String> map = (HashMap<String, String>) dataSnapshot1.getValue();
                                Object[] scoresArray = map.values().toArray();
                                List<Integer> scores = new ArrayList<>();
                                for (int i = 0 ; i < scoresArray.length; i+=2){
                                    scores.add((int) (long) scoresArray[i]);
                                }
                                System.out.println(scores);
                                questionTv.setText(String.format(Locale.getDefault(),"Top Score: %d \n Average Score: %.2f",Collections.max(scores),calculateAverage(scores)));
                                db.getReference(currentQuiz.getQuizId()).removeValue().addOnSuccessListener(unused -> System.out.println("Data deleted!"));
                            });
                        }
                    }
                }.start();
            } else {
                System.out.println("Unable to get timestamp");
            }
        });
    }

    private void updateTextView(long timeLeft) {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        String timeLeftString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTv.setText(timeLeftString);
    }

    private void fillQuestions() {
        System.out.println("Current ques: " + currentQuestionIndex);
        if (currentQuestionIndex < currentQuiz.getQuestionsList().size()){
            questionTv.setText(currentQuiz.getQuestionsList().get(currentQuestionIndex).getQuestion());
            ans1Btn.setText(currentQuiz.getQuestionsList().get(currentQuestionIndex).getAns1());
            ans2Btn.setText(currentQuiz.getQuestionsList().get(currentQuestionIndex).getAns2());
            ans3Btn.setText(currentQuiz.getQuestionsList().get(currentQuestionIndex).getAns3());
            ans4Btn.setText(currentQuiz.getQuestionsList().get(currentQuestionIndex).getAns4());
            questionIndexTv.setText(String.format(Locale.getDefault(), "Question %d/%d", currentQuestionIndex + 1, currentQuiz.getQuestionsList().size()));
        }
        if (currentQuestionIndex == currentQuiz.getQuestionsList().size()) {
            Toast.makeText(this, "Quiz Finished", Toast.LENGTH_SHORT).show();
            ans1Btn.setVisibility(View.INVISIBLE);
            ans2Btn.setVisibility(View.INVISIBLE);
            ans3Btn.setVisibility(View.INVISIBLE);
            ans4Btn.setVisibility(View.INVISIBLE);
            questionTv.setText(String.format(Locale.getDefault(), "Score: %d", score));
            FirebaseAuth auth = FirebaseAuth.getInstance();
            db.getReference(currentQuiz.getQuizId()).child("clients").
                    child("scores").push().setValue(score).addOnCompleteListener
                    (task -> System.out.println("CLIENT SCORE STORED"));
        }
    }

    private double calculateAverage(List <Integer> scores) {
        return scores.stream()
                .mapToDouble(s -> s)
                .average()
                .orElse(0.0);
    }

    private void checkAnswer(int ans) {
        System.out.println("here");
        System.out.println(currentQuestionIndex);
        System.out.println(ans);
        System.out.println(currentQuiz.getQuestionsList().get(currentQuestionIndex).getCorrectAnswer());
        if (ans == currentQuiz.getQuestionsList().get(currentQuestionIndex).getCorrectAnswer()) {
            score += 100;
            System.out.println(score);
        }
    }
}