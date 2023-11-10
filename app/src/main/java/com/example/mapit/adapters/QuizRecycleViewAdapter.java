package com.example.mapit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapit.R;
import com.example.mapit.classes.QuizItem;

import java.util.List;

public class QuizRecycleViewAdapter extends RecyclerView.Adapter<QuizRecycleViewAdapter.QuizViewHolder> {

    List<QuizItem> quizItemList;
    private OnItemClickListener clickListener;

    public QuizRecycleViewAdapter(List<QuizItem> quizItemList) {
        this.quizItemList = quizItemList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizItem currentItem = quizItemList.get(position);
        holder.quizName.setText(currentItem.getQuizName());
        holder.quizSubject.setText(currentItem.getQuizSubject());
        String capacityString = "Questions: " + currentItem.getQuestionCapacity();
        holder.questionCapacity.setText(capacityString);
    }

    @Override
    public int getItemCount() {
        return quizItemList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizName, quizSubject, questionCapacity;

        public QuizViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
            quizName = itemView.findViewById(R.id.quizItemNameTextView);
            quizSubject = itemView.findViewById(R.id.quizItemSubjectTextView);
            questionCapacity = itemView.findViewById(R.id.quizItemQuestionsTextView);
        }
    }
}
