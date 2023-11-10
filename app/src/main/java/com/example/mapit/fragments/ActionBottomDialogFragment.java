package com.example.mapit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapit.R;
import com.example.mapit.activities.MapsActivityCreateQuiz;
import com.example.mapit.adapters.QuestionsRecyclerViewAdapter;
import com.example.mapit.classes.Question;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ActionBottomDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public static final String TAG = "ActionBottomDialog";
    EditText question, ans1, ans2, ans3, ans4;
    Button finishBtn;
    ImageView appLogo;
    RadioGroup answersGroup;
    Boolean dialogFinished, disableListener = false;
    Marker selectedMarker;
    private int questionIndex;
    private OnCompleteListener completeListener;

    public static ActionBottomDialogFragment newInstance() {
        return new ActionBottomDialogFragment();
    }

    public void enableCreateQuiz() {
        this.disableListener = true;
    }

    public interface OnCompleteListener {
        void onComplete(int position);
    }

    public void addMarker(Marker selectedMarker) {
        dialogFinished = false;
        this.selectedMarker = selectedMarker;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public boolean isFinished() {
        return this.dialogFinished;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        question = view.findViewById(R.id.questionEditText);
        ans1 = view.findViewById(R.id.answer1EditText);
        ans2 = view.findViewById(R.id.answer2EditText);
        ans3 = view.findViewById(R.id.answer3EditText);
        ans4 = view.findViewById(R.id.answer4EditText);
        appLogo = view.findViewById(R.id.appLogoGenerateQuestion);
        answersGroup = view.findViewById(R.id.answersRadioGroup);
        finishBtn = view.findViewById(R.id.finishQuestionBtn);

        finishBtn.setOnClickListener(this);
        if (!disableListener)
            completeListener.onComplete(questionIndex);

        appLogo.setOnClickListener(view1 -> {
            if (disableListener) {
                final View dialogView = getLayoutInflater().inflate(R.layout.generate_question_dialog, null);
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setView(dialogView);
                alertDialog.show();
                Button generateQuestionBtn = alertDialog.findViewById(R.id.generateQuestionBtn);
                EditText subjectEditText = alertDialog.findViewById(R.id.subjectEditText);
                ProgressBar loading = alertDialog.findViewById(R.id.generateProgressBar);
                RecyclerView questionsRecyclerView = alertDialog.findViewById(R.id.questionsRecyclerView);
                QuestionsRecyclerViewAdapter adapter = new QuestionsRecyclerViewAdapter(getContext(),new String[]{});
                questionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                questionsRecyclerView.setAdapter(adapter);
                Objects.requireNonNull(generateQuestionBtn).setOnClickListener(view2 -> {
                    if (!TextUtils.isEmpty(subjectEditText.getText().toString())) {
                        loading.setVisibility(View.VISIBLE);
                        HttpUrl.Builder httpBuilder = HttpUrl.parse("http://192.168.56.1:5000/").newBuilder();
                        OkHttpClient okhttpclient = new OkHttpClient();
                        okhttpclient.setReadTimeout(60, TimeUnit.SECONDS);
                        httpBuilder.addQueryParameter("subject", subjectEditText.getText().toString());
                        Request request = new Request.Builder().url(httpBuilder.build()).build();
                        okhttpclient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                System.out.println("FAILURE");
                                System.out.println(e.getMessage());
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                String result = response.body().string();
                                if (!result.equals("-1")){
                                    String[] questions = result.split(",");
                                    loading.setVisibility(View.INVISIBLE);
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        adapter.setOnItemClickListener(position -> {
                                            question.setText(questions[position]);
                                            alertDialog.dismiss();
                                        });
                                        adapter.swapDataSet(questions);
                                    });
                                }
                                else{
                                    loading.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(),"Information cannot be found",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (!disableListener)
                this.completeListener = (OnCompleteListener) context;
        } catch (final ClassCastException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        if (!TextUtils.isEmpty(question.getText().toString())) {
            if (!TextUtils.isEmpty(ans1.getText().toString()) && !TextUtils.isEmpty(ans2.getText().toString()) &&
                    !TextUtils.isEmpty(ans3.getText().toString()) && !TextUtils.isEmpty(ans4.getText().toString())) {
                if (answersGroup.getCheckedRadioButtonId() != -1) {
                    System.out.println(finishBtn.isEnabled());
                    if (dialogFinished)
                        Toast.makeText(getContext(), "Question already submitted", Toast.LENGTH_LONG).show();
                    else {
                        MapsActivityCreateQuiz activity = (MapsActivityCreateQuiz) getActivity();
                        int correctAnsId = answersGroup.getCheckedRadioButtonId();
                        RadioButton correctButton = answersGroup.findViewById(correctAnsId);
                        Question newQuestion = new Question(question.getText().toString(), ans1.getText().toString(), ans2.getText().toString(),
                                ans3.getText().toString(), ans4.getText().toString(), Integer.parseInt(correctButton.getText().toString().split(" ")[1]));
                        assert activity != null;
                        activity.addQuestionToQuiz(newQuestion);
                        selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        dialogFinished = true;
                        dismiss();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Please enter 4 answers", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Question must be entered", Toast.LENGTH_SHORT).show();
        }
    }

    public void fillFields(Question fillQuestion) {
        question.setText(fillQuestion.getQuestion());
        question.setEnabled(false);
        ans1.setText(fillQuestion.getAns1());
        ans1.setEnabled(false);
        ans2.setText(fillQuestion.getAns2());
        ans2.setEnabled(false);
        ans3.setText(fillQuestion.getAns3());
        ans3.setEnabled(false);
        ans4.setText(fillQuestion.getAns4());
        ans4.setEnabled(false);
        answersGroup.setEnabled(false);
        finishBtn.setEnabled(false);
        finishBtn.setVisibility(View.INVISIBLE);

        for (int i = 0; i < answersGroup.getChildCount(); i++) {
            answersGroup.getChildAt(i).setEnabled(false);
        }
        switch (fillQuestion.getCorrectAnswer()) {
            case 1:
                answersGroup.check(R.id.ans1RadioButton);
                break;
            case 2:
                answersGroup.check(R.id.ans2RadioButton);
                break;
            case 3:
                answersGroup.check(R.id.ans3RadioButton);
                break;
            case 4:
                answersGroup.check(R.id.ans4RadioButton);
                break;
        }
    }
}
