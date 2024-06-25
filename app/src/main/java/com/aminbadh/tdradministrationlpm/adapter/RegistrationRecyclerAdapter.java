package com.aminbadh.tdradministrationlpm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.custom.Registration;
import com.aminbadh.tdradministrationlpm.interfaces.OnMainListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RegistrationRecyclerAdapter extends
        RecyclerView.Adapter<RegistrationRecyclerAdapter.RegistrationHolder> {

    private final ArrayList<Registration> registrations;
    private final OnMainListener onMainListener;
    private final View.OnClickListener onClickListener;

    public RegistrationRecyclerAdapter(ArrayList<Registration> registrations,
                                       OnMainListener onMainListener,
                                       View.OnClickListener onClickListener) {
        this.registrations = registrations;
        this.onMainListener = onMainListener;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == (getItemCount() - 1)) {
            return 2;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RegistrationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_registration, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_more, parent, false);
        }
        return new RegistrationHolder(view, onMainListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationHolder holder, int position) {
        if (position == (getItemCount() - 1)) {
            holder.buttonLoadMore.setOnClickListener(onClickListener);
        } else {
            Registration currentRegistration = registrations.get(position);
            holder.textViewSubject.setText(currentRegistration.getSubject());
            holder.textViewName.setText(currentRegistration.getProfessorName());
            holder.textViewDate.setText(reformatTime(currentRegistration.getSubmitTime()));
        }
    }

    @Override
    public int getItemCount() {
        return registrations.size();
    }

    public static String reformatTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatter.format(calendar.getTime());
    }

    static class RegistrationHolder extends RecyclerView.ViewHolder {
        TextView textViewSubject, textViewName, textViewDate;
        Button buttonLoadMore;

        public RegistrationHolder(@NonNull View itemView, final OnMainListener onMainListener) {
            super(itemView);
            textViewSubject = itemView.findViewById(R.id.textViewSubject);
            textViewName = itemView.findViewById(R.id.textViewProfName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            buttonLoadMore = itemView.findViewById(R.id.buttonLoadMore);
            itemView.setOnClickListener(view -> onMainListener.onClickListener(getAdapterPosition()));
        }
    }
}
