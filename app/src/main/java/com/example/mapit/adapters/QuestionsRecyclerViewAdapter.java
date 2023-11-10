package com.example.mapit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapit.R;


public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.QuestionsViewHolder> {
    Context context;
    String[] questions;
    private QuestionsRecyclerViewAdapter.OnItemClickListener clickListener;

    public QuestionsRecyclerViewAdapter(Context context, String[] questions) {
        this.context = context;
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.questions_recyclerview_item,parent,false);
        return new QuestionsViewHolder(view,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsViewHolder holder, int position) {
        holder.questionsTv.setText(questions[position]);
    }

    @Override
    public int getItemCount() {
        return questions.length;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(QuestionsRecyclerViewAdapter.OnItemClickListener listener) {
        clickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void swapDataSet(String[] newData){

        this.questions = newData;
        notifyDataSetChanged();

    }
    public static class QuestionsViewHolder extends RecyclerView.ViewHolder {
        TextView questionsTv;
        public QuestionsViewHolder(@NonNull View itemView, QuestionsRecyclerViewAdapter.OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
            questionsTv = itemView.findViewById(R.id.generatedQuestionTv);
        }
    }
}
