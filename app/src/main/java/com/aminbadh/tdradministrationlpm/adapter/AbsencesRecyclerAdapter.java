package com.aminbadh.tdradministrationlpm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aminbadh.tdradministrationlpm.R;

import java.util.ArrayList;

public class AbsencesRecyclerAdapter extends
        RecyclerView.Adapter<AbsencesRecyclerAdapter.AbsencesHolder> {

    private final ArrayList<String> absences;

    public AbsencesRecyclerAdapter(ArrayList<String> absences) {
        this.absences = absences;
    }

    @NonNull
    @Override
    public AbsencesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_student,
                parent, false);
        return new AbsencesHolder(view);
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

        public AbsencesHolder(@NonNull View itemView) {
            super(itemView);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
        }
    }
}
