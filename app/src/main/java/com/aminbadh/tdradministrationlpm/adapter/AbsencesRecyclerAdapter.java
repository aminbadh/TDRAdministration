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

public class AbsencesRecyclerAdapter extends
        RecyclerView.Adapter<AbsencesRecyclerAdapter.AbsencesHolder> {

    private final ArrayList<String> absences;
    private final OnMainListener onMainListener;

    public AbsencesRecyclerAdapter(ArrayList<String> absences, OnMainListener onMainListener) {
        this.absences = absences;
        this.onMainListener = onMainListener;
    }

    @NonNull
    @Override
    public AbsencesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_student,
                parent, false);
        return new AbsencesHolder(view, onMainListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsencesHolder holder, int position) {
        holder.textViewStudentName.setText(absences.get(position));
    }

    @Override
    public int getItemCount() {
        return absences.size();
    }


    static class AbsencesHolder extends RecyclerView.ViewHolder {
        TextView textViewStudentName;

        public AbsencesHolder(@NonNull View itemView, final OnMainListener onMainListener) {
            super(itemView);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            itemView.setOnClickListener(view -> onMainListener.onClickListener(getAdapterPosition()));
        }
    }
}
