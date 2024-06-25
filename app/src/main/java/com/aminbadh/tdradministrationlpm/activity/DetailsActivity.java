package com.aminbadh.tdradministrationlpm.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.adapter.AbsencesRecyclerAdapter;
import com.aminbadh.tdradministrationlpm.adapter.RegistrationsRecyclerAdapter;
import com.aminbadh.tdradministrationlpm.custom.Registration;
import com.aminbadh.tdradministrationlpm.databinding.ActivityDetailsBinding;

import static com.aminbadh.tdradministrationlpm.custom.Constants.REGISTRATION_OBJECT;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;
    private Registration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the binding object.
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Set the Activity's title.
        setTitle(R.string.details);
        // Initialise the registration object.
        registration = (Registration) getIntent().getSerializableExtra(REGISTRATION_OBJECT);
        // Setup UI.
        setupUI();
    }

    private void setupUI() {
        // Setup texts.
        String className = getString(R.string.class_c) + " " + registration.getClassName();
        binding.textViewClass.setText(className);
        binding.textViewGroup.setText(registration.getGroup());
        String name = getString(R.string.name_c) + " " + registration.getProfessorName();
        binding.textViewProfName.setText(name);
        String subject = getString(R.string.subject_c) + " " + registration.getSubject();
        binding.textViewProfSubject.setText(subject);
        String from = getString(R.string.from_c) + " " + registration.getFromTime();
        binding.textViewFrom.setText(from);
        String to = getString(R.string.to_c) + " " + registration.getToTime();
        binding.textViewTo.setText(to);
        String data = getString(R.string.date_c) + " " + RegistrationsRecyclerAdapter
                .reformatTime(registration.getSubmitTime());
        binding.textViewRDate.setText(data);
        // Setup recyclerView.
        if (registration.getAbsences().isEmpty()) {
            binding.recyclerViewAbsences.setVisibility(View.GONE);
        } else {
            binding.textViewNoAbsences.setVisibility(View.GONE);
            AbsencesRecyclerAdapter adapter = new AbsencesRecyclerAdapter(registration.getAbsences());
            binding.recyclerViewAbsences.setHasFixedSize(true);
            binding.recyclerViewAbsences.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewAbsences.setAdapter(adapter);
        }
    }
}