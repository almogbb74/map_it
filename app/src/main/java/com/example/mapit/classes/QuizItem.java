package com.example.mapit.classes;

public class QuizItem {
    String quizName,quizSubject,quizId;
    int questionCapacity;

    public String getQuizName() {
        return quizName;
    }

    public String getQuizSubject() {
        return quizSubject;
    }

    public int getQuestionCapacity() {
        return questionCapacity;
    }

    public QuizItem(String quizName, String quizSubject, int questionCapacity,String quizId) {
        this.quizName = quizName;
        this.quizSubject = quizSubject;
        this.questionCapacity = questionCapacity;
        this.quizId=quizId;
    }
}
