package com.aminbadh.tdradministrationlpm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.interfaces.OnMainListener;

import java.util.ArrayList;

public class ClassAbsencesRecyclerAdapter extends RecyclerView
        .Adapter<ClassAbsencesRecyclerAdapter.ClassAbsencesHolder> {

    private final ArrayList<String> absences;
    private final OnMainListener onMainListener;

    public ClassAbsencesRecyclerAdapter(ArrayList<String> absences, OnMainListener onMainListener) {
        this.absences = absences;
        this.onMainListener = onMainListener;
    }

    @NonNull
    @Override
    public ClassAbsencesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_class_absences, parent, false);
        return new ClassAbsencesHolder(view, onMainListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAbsencesHolder holder, int position) {
        holder.textViewName.setText(absences.get(position));
    }

    @Override
    public int getItemCount() {
        return absences.size();
    }

    static class ClassAbsencesHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public ClassAbsencesHolder(@NonNull View itemView, OnMainListener onMainListener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewAbsentStudentName);
            itemView.setOnClickListener(view -> onMainListener.onClickListener(getAdapterPosition()));
        }
    }
}
