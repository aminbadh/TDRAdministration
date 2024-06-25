package com.aminbadh.tdradministrationlpm.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.custom.Registration;
import com.aminbadh.tdradministrationlpm.interfaces.OnDataAction;
import com.aminbadh.tdradministrationlpm.interfaces.OnMainListener;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegistrationsRecyclerAdapter extends FirestorePagingAdapter<Registration,
        RegistrationsRecyclerAdapter.RegistrationsHolder> {

    private final SwipeRefreshLayout swipeRefreshLayout;
    private final OnDataAction action;
    private final OnMainListener onMainListener;

    public RegistrationsRecyclerAdapter(@NonNull FirestorePagingOptions<Registration> options,
                                        SwipeRefreshLayout swipeRefreshLayout, OnDataAction action,
                                        OnMainListener onMainListener) {
        super(options);
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.action = action;
        this.onMainListener = onMainListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull RegistrationsHolder holder, int position,
                                    @NonNull Registration model) {
        String time = model.getFromTime() + " -> " + model.getToTime();
        holder.textViewTime.setText(time);
        holder.textViewDate.setText(reformatTime(model.getSubmitTime()));
        holder.textViewSubject.setText(model.getSubject());
        holder.textViewProfessor.setText(model.getProfessorName());
    }

    public static String reformatTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatter.format(calendar.getTime());
    }

    public Registration getRegistration(int position) {
        return getItem(position).toObject(Registration.class);
    }

    @NonNull
    @Override
    public RegistrationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_registration, parent, false);
        return new RegistrationsHolder(view, onMainListener);
    }

    @Override
    protected void onError(@NonNull Exception e) {
        super.onError(e);
        Log.e("MainActivity", e.getMessage());
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state) {
            case LOADING_INITIAL:
            case LOADING_MORE:
                swipeRefreshLayout.setRefreshing(true);
                break;
            case LOADED:
            case FINISHED:
            case ERROR:
                swipeRefreshLayout.setRefreshing(false);
                if (getItemCount() <= 0) {
                    action.onEmpty();
                } else {
                    action.onData();
                }
                break;
        }
    }

    static class RegistrationsHolder extends RecyclerView.ViewHolder {
        final TextView textViewTime, textViewDate, textViewSubject, textViewProfessor;

        public RegistrationsHolder(@NonNull View itemView, OnMainListener onMainListener) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewSubject = itemView.findViewById(R.id.textViewSubject);
            textViewProfessor = itemView.findViewById(R.id.textViewProfessor);
            itemView.setOnClickListener(view -> onMainListener.onClickListener(getAdapterPosition()));
        }
    }
}
