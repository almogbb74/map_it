package com.example.mapit.activities;

import static com.example.mapit.fragments.ActionBottomDialogFragment.TAG;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mapit.R;
import com.example.mapit.classes.MyLatLng;
import com.example.mapit.classes.Question;
import com.example.mapit.classes.Quiz;
import com.example.mapit.fragments.ActionBottomDialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapsActivityCreateQuiz extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    GoogleMap mMap;
    List<Marker> markedPoints;
    ImageView removeImage;
    Button finishBtn, createQuizBtn;
    ActionBottomDialogFragment[] bottomSheetDialogArray;
    Rect bounds;
    Boolean isFinished, firstClick,dialogFinishedByInput=false;
    Quiz newQuiz;
    ArrayAdapter<String> categoriesAdapter, subcategoriesAdapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //TODO: Add a finish button, create quiz after user filled all questions
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_create_quiz);

        Spinner categoriesSpinner, subcategoriesSpinner;
        ArrayList<String> categories = new ArrayList<>(), physics = new ArrayList<>(), biology = new ArrayList<>();
        Button dialogCreateButton;
        EditText dialogQuizNameEditText;
        TextView existingQuizTextView;

        categories.add("Physics");
        physics.add("Mechanics");
        physics.add("Electricity");
        physics.add("Optics");
        categories.add("Biology");
        biology.add("Ecology");
        biology.add("Cell biology");
        biology.add("Metabolism");
        categoriesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);

        db = FirebaseFirestore.getInstance();


        removeImage = findViewById(R.id.removerMarkerImg);
        finishBtn = findViewById(R.id.finishBtn);
        createQuizBtn = findViewById(R.id.createQuizBtn);

        createQuizBtn.setOnClickListener(view -> {
            int finishedDialogsCounter = 0;
            for (ActionBottomDialogFragment dialog : bottomSheetDialogArray) {
                if (dialog != null && dialog.isFinished()) {
                    finishedDialogsCounter++;
                }
            }
            if (finishedDialogsCounter == bottomSheetDialogArray.length) { // All questions filled

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                System.out.println(newQuiz.getMarkersPosition());
                DocumentReference myQuizzesRef = rootRef.collection("myQuizzes").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                HashMap<String,Quiz> quizHashMap = new HashMap<>();
                quizHashMap.put(newQuiz.getQuizId(),newQuiz);
                myQuizzesRef.set(quizHashMap, SetOptions.merge());

                db.collection("quizzes").document(newQuiz.getQuizId()).set(newQuiz).
                        addOnSuccessListener(unused -> System.out.println("Quiz Created!"));
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
                builder.setTitle("Quiz Created");
                builder.setMessage("Do you want to play the quiz?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    Gson gson = new Gson();
                    String selectedQuizJson = gson.toJson(newQuiz);
                    Intent intent = new Intent(MapsActivityCreateQuiz.this, QuizLobbyActivity.class);
                    intent.putExtra("quizJSON", selectedQuizJson);
                    startActivity(intent);
                });
                builder.setNegativeButton("No", (dialog, which) ->
                        startActivity(new Intent(getApplicationContext(), MainScreenActivity.class)));
                AlertDialog alert = builder.create();
                alert.show();
            } else
                Toast.makeText(getApplicationContext(), "Please fill all questions", Toast.LENGTH_SHORT).show();

        });

        markedPoints = new ArrayList<>();
        newQuiz = new Quiz();
        isFinished = false;
        firstClick = true;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapCreate);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        final View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_maps, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("New Quiz");
        alertDialog.setCancelable(true);
        alertDialog.setOnDismissListener(dialogInterface -> {
            if (!dialogFinishedByInput)
                startActivity(new Intent(getApplicationContext(),MainScreenActivity.class));
        });

        dialogCreateButton = dialogView.findViewById(R.id.finishDialogBtn);
        dialogQuizNameEditText = dialogView.findViewById(R.id.quizNameEditText);
        categoriesSpinner = dialogView.findViewById(R.id.mapsCategoriesSpinner);
        subcategoriesSpinner = dialogView.findViewById(R.id.mapsSubcategoriesSpinner);
        existingQuizTextView = dialogView.findViewById(R.id.useExistingQuizTextView);
        categoriesSpinner.setAdapter(categoriesAdapter);

        dialogCreateButton.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(dialogQuizNameEditText.getText().toString())) {
                dialogFinishedByInput = true;
                newQuiz.setQuizSubject(categoriesSpinner.getSelectedItem().toString() + " - " + subcategoriesSpinner.getSelectedItem().toString());
                newQuiz.setQuizName(dialogQuizNameEditText.getText().toString());
                alertDialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "Quiz name must be submitted", Toast.LENGTH_LONG).show();
            }
        });

        existingQuizTextView.setOnClickListener(view -> startActivity(new Intent(this,ExistingQuizzesActivity.class)));

                categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                subcategoriesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, physics);
                                break;
                            case 1:
                                subcategoriesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, biology);
                        }
                        subcategoriesSpinner.setAdapter(subcategoriesAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        bounds = new Rect(removeImage.getLeft(), removeImage.getTop(), removeImage.getLeft() + removeImage.getWidth(), removeImage.getTop() + removeImage.getHeight());
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31, 35), 7));
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);
        finishBtn.setOnClickListener(view -> {
            if (markedPoints.size() >= 2) {
                List<LatLng> latLngsOfMarkers = new ArrayList<>();
                markedPoints.forEach((marker ->{
                    latLngsOfMarkers.add(marker.getPosition());
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    marker.setDraggable(false);
                }));
                List<MyLatLng> quizMarkersList = new ArrayList<>();
                latLngsOfMarkers.forEach(latLng -> quizMarkersList.add(new MyLatLng(latLng.latitude,latLng.longitude)));
                newQuiz.setMarkersPosition(quizMarkersList);
                mMap.addPolyline(new PolylineOptions().addAll(latLngsOfMarkers).width(10)
                        .color(ContextCompat.getColor(this, R.color.blue_s)).visible(true).clickable(false));
                finishBtn.setEnabled(false);
                finishBtn.setVisibility(View.INVISIBLE);
                createQuizBtn.setEnabled(true);
                createQuizBtn.setVisibility(View.VISIBLE);
                bottomSheetDialogArray = new ActionBottomDialogFragment[markedPoints.size()];
                isFinished = true; // Clicked on finished button
            } else {
                if (markedPoints.size() == 1)
                    Toast.makeText(this, "Please Add More Than 1 Question", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, "Please Add Questions", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (isFinished) {
            if (bottomSheetDialogArray[markedPoints.indexOf(marker)] == null) {
                bottomSheetDialogArray[markedPoints.indexOf(marker)] = ActionBottomDialogFragment.newInstance();
                bottomSheetDialogArray[markedPoints.indexOf(marker)].enableCreateQuiz();
                bottomSheetDialogArray[markedPoints.indexOf(marker)].addMarker(marker);
            }
            bottomSheetDialogArray[markedPoints.indexOf(marker)].show(getSupportFragmentManager(),
                    TAG);
        }
        return false;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (!isFinished) {
            if (firstClick) {
                finishBtn.setVisibility(View.VISIBLE);
                firstClick = false;
            }
            Marker m = mMap.addMarker(new MarkerOptions().position(latLng));
            assert m != null;
            newQuiz.addMarkerToList(new MyLatLng(m.getPosition().latitude,m.getPosition().longitude));
            m.setDraggable(true);
            markedPoints.add(m);
        }
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        if (!isFinished) {
            Point markerScreenPosition = mMap.getProjection().toScreenLocation(marker.getPosition());
            if (bounds.contains(markerScreenPosition.x, markerScreenPosition.y)) {
                System.out.println(markedPoints);
                marker.remove();
                markedPoints.remove(marker);
                System.out.println(markedPoints);
                removeImage.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        removeImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        if (!isFinished){
            removeImage.setVisibility(View.VISIBLE);
        }
    }

    public void addQuestionToQuiz(Question newQuestion) {
        newQuiz.addQuestion(newQuestion);
    }
}

