package com.aminbadh.tdradministrationlpm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.custom.Class;
import com.aminbadh.tdradministrationlpm.interfaces.OnData;
import com.aminbadh.tdradministrationlpm.interfaces.OnMainListener;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ClassRecyclerAdapter extends FirestoreRecyclerAdapter<Class,
        ClassRecyclerAdapter.ClassHolder> {

    private final OnMainListener onMainListener;
    private final OnData onData;

    public ClassRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Class> options,
                                OnMainListener onMainListener, OnData onData) {
        super(options);
        this.onMainListener = onMainListener;
        this.onData = onData;
    }

    @NonNull
    @Override
    public ClassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_class, parent, false);
        return new ClassHolder(view, onMainListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull ClassHolder holder, int position, @NonNull Class model) {
        holder.textViewName.setText(model.getClassName());
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        onData.onData();
    }

    public Class getClass(int position) {
        Class mClass = getItem(position);
        mClass.setDocRef(getSnapshots().getSnapshot(position).getReference().getPath());
        return mClass;
    }

    static class ClassHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public ClassHolder(@NonNull View itemView, OnMainListener onMainListener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewMainName);
            itemView.setOnClickListener(view -> onMainListener.onClickListener(getAdapterPosition()));
        }
    }
}
