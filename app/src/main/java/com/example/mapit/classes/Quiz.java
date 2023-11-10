package com.example.mapit.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Quiz {
    private final String quizId;
    private String quizName, quizSubject, joiningCode="";
    private List<MyLatLng> markersPosition; //TODO: Replace with (double,double) class
    private final List<Question> questionsList;
    private int quizDuration;

    public Quiz() {
        StringBuilder output = new StringBuilder(5);
        for (int i=1;i<=5;i++){
            int randomNum = ThreadLocalRandom.current().nextInt(1, 9 + 1);
           output.append(randomNum);
        }
        quizId=output.toString();
        System.out.println(quizId);
        questionsList = new ArrayList<>();
        markersPosition = new ArrayList<>();
    }

    public String getQuizId() { return quizId; }

    public String getQuizName() {
        return quizName;
    }

    public String getQuizSubject() {
        return quizSubject;
    }

    public List<MyLatLng> getMarkersPosition() {
        return markersPosition;
    }

    public List<Question> getQuestionsList() {
        return questionsList;
    }

    public int getQuizDuration() { return quizDuration;}

    public void setQuizDuration(int quizDuration) { this.quizDuration = quizDuration; }

    public void addQuestion(Question newQuestion) {
        questionsList.add(newQuestion);
    }

    public void addMarkerToList(MyLatLng marker) {
        markersPosition.add(marker);
    }

    public void setMarkersPosition(List<MyLatLng> markersPosition) { this.markersPosition = markersPosition;};

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public void setQuizSubject(String quizSubject) {
        this.quizSubject = quizSubject;
    }

    public QuizItem createNewItem(){
        return new QuizItem(quizName,quizSubject.toString(),questionsList.size(),quizId);
    }

    public String getJoiningCode() {
        return joiningCode;
    }

    public void setJoiningCode(String joiningCode) {
        this.joiningCode = joiningCode;
    }

    public void generateJoiningCode(){
        StringBuilder output = new StringBuilder(5);
        for (int i=1;i<=5;i++){
            int randomNum = ThreadLocalRandom.current().nextInt(1, 9 + 1);
            output.append(randomNum);
        }
        System.out.println(output.toString());
        this.joiningCode=output.toString();
    }

    @Override
    public String toString() {
        List<String> questionsString= new ArrayList<>();
        String output = String.format(
                "Quiz name: %s\n" +
                 "Subject: %s\n" +
                 "Join Code: %s\n"+
                "Markers list: %s\n" +
                "Questions:\n",quizName,quizSubject,joiningCode,markersPosition.toString());
        questionsList.forEach(question -> questionsString.add(question.toString()));
        output+=questionsString.toString();
        return output;
    }
}
