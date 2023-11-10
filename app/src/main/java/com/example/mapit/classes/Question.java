package com.example.mapit.classes;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Question {
    String question,ans1,ans2,ans3,ans4;
    int correctAnswer;

    public Question(String question, String ans1, String ans2, String ans3, String ans4, int correctAnswer) {
        this.question = question;
        this.ans1 = ans1;
        this.ans2 = ans2;
        this.ans3 = ans3;
        this.ans4 = ans4;
        this.correctAnswer = correctAnswer;
    }

    public Question() {
    }

    public String getQuestion() {
        return question;
    }

    public String getAns1() {
        return ans1;
    }

    public String getAns2() {
        return ans2;
    }

    public String getAns3() {
        return ans3;
    }

    public String getAns4() {
        return ans4;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(),"%s | Correct answer: %d", question, correctAnswer);
    }
}
