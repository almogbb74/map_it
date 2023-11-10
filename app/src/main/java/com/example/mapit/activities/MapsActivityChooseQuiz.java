package com.example.mapit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mapit.R;
import com.example.mapit.classes.Quiz;
import com.example.mapit.fragments.ActionBottomDialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class MapsActivityChooseQuiz extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, ActionBottomDialogFragment.OnCompleteListener, View.OnClickListener {

    private GoogleMap mMap;
    private Quiz selectedQuiz;
    private ActionBottomDialogFragment[] bottomDialogArray;
    int markerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //TODO: Draw route between markers.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_choose_quiz);

        Gson gson = new Gson();
        String quizJson = getIntent().getStringExtra("quizJSON");
        selectedQuiz = gson.fromJson(quizJson, Quiz.class);
        Button playQuizBtn = findViewById(R.id.playQuizBtn);
        playQuizBtn.setOnClickListener(this);
        markerPosition = -1;
        System.out.println(selectedQuiz);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapChoose);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31, 35), 7));
        mMap.setOnMarkerClickListener(this);
        displayMarkersOnMap();
        setQuestionsOnMap();
    }

    public boolean onMarkerClick(@NonNull Marker marker) {
        int markerPosition = findClickedMarker(marker);
        System.out.println("POS: " + markerPosition);
        bottomDialogArray[markerPosition].setQuestionIndex(markerPosition);
        bottomDialogArray[markerPosition].show(getSupportFragmentManager(), ActionBottomDialogFragment.TAG);
        return false;
    }

    private void displayMarkersOnMap() {
        selectedQuiz.getMarkersPosition().forEach(
                latLng -> mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.getLatitude(), latLng.getLongitude()))));
    }

    private void setQuestionsOnMap() {
        bottomDialogArray = new ActionBottomDialogFragment[selectedQuiz.getQuestionsList().size()];
        for (int i = 0; i < selectedQuiz.getQuestionsList().size(); i++)
            bottomDialogArray[i] = ActionBottomDialogFragment.newInstance();
    }

    private int findClickedMarker(Marker marker) {
        for (int i = 0; i < selectedQuiz.getMarkersPosition().size(); i++) {
            if (selectedQuiz.getMarkersPosition().get(i).getLatitude() == marker.getPosition().latitude &&
                    selectedQuiz.getMarkersPosition().get(i).getLongitude() == marker.getPosition().longitude)
                return i;
        }
        return -1;
    }

    @Override
    public void onComplete(int position) {
        System.out.println("In listener");
        System.out.println(position);
        bottomDialogArray[position].fillFields(selectedQuiz.getQuestionsList().get(position));
    }

    @Override
    public void onClick(View view) {
        Gson gson = new Gson();
        String selectedQuizJson = gson.toJson(selectedQuiz);
        Intent intent = new Intent(MapsActivityChooseQuiz.this, QuizLobbyActivity.class);
        intent.putExtra("quizJSON", selectedQuizJson);
        startActivity(intent);
    }
}
