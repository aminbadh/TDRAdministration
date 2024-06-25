package com.aminbadh.tdradministrationlpm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.adapter.AbsencesRecyclerAdapter;
import com.aminbadh.tdradministrationlpm.adapter.RegistrationRecyclerAdapter;
import com.aminbadh.tdradministrationlpm.custom.Class;
import com.aminbadh.tdradministrationlpm.custom.Registration;
import com.aminbadh.tdradministrationlpm.databinding.ActivityDetailBinding;

import static com.aminbadh.tdradministrationlpm.custom.Constants.CLASS_OBJECT;
import static com.aminbadh.tdradministrationlpm.custom.Constants.REGISTRATION_OBJECT;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarStr;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private Registration registration;
    private Class aClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the Activity's title.
        setTitle(R.string.details);
        // Initialise the binding object.
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Load the UI.
        loadUI();
    }

    private void loadUI() {
        // Get the objects with the intent.
        getDetailIntent();
        // Set textViews text values.
        String name = getString(R.string.name) + " " + registration.getProfessorName();
        binding.textViewNameD.setText(name);
        String subject = getString(R.string.subject) + " " + registration.getSubject();
        binding.textViewSubjectD.setText(subject);
        String classString = getString(R.string.classD) + " " + aClass.getClassName();
        binding.textViewClass.setText(classString);
        String group = getString(R.string.group) + " " + registration.getGroup();
        binding.textViewGroup.setText(group);
        String date = getString(R.string.date) + " " + RegistrationRecyclerAdapter
                .reformatTime(registration.getSubmitTime());
        binding.textViewDateD.setText(date);
        String from = getString(R.string.from) + " " + registration.getFromTime();
        binding.textViewFromD.setText(from);
        String to = getString(R.string.to) + " " + registration.getToTime();
        binding.textViewToD.setText(to);
        if (registration.getAbsences().isEmpty()) {
            // Show no absences feedback.
            binding.textViewNoAbsencesD.setVisibility(View.VISIBLE);
            binding.recyclerViewAbsencesD.setVisibility(View.GONE);
        } else {
            // Setup the RecyclerView.
            binding.textViewNoAbsencesD.setVisibility(View.GONE);
            binding.recyclerViewAbsencesD.setVisibility(View.VISIBLE);
            binding.recyclerViewAbsencesD.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewAbsencesD.setHasFixedSize(true);
            binding.recyclerViewAbsencesD.setAdapter(new AbsencesRecyclerAdapter(
                    registration.getAbsences(), position -> {
                // Intent intent = new Intent(
                //         DetailActivity.this, StudentActivity.class);
                // intent.putExtra(STUDENT_NAME, registration.getAbsences().get(position));
                // intent.putExtra(CLASS_OBJECT, aClass);
                // startActivity(intent);
                displaySnackbarStr(binding.getRoot(), "Display data of " +
                        registration.getAbsences().get(position));
            }));
        }
    }

    private void getDetailIntent() {
        // Get the objects passed with the intent.
        registration = (Registration) getIntent()
                .getSerializableExtra(REGISTRATION_OBJECT);
        aClass = (Class) getIntent().getSerializableExtra(CLASS_OBJECT);
        if (registration == null || aClass == null) {
            // If one of the objects is null, stop the app.
            Toast.makeText(this, R.string.a_prob_happened_getting_data,
                    Toast.LENGTH_LONG).show();
            DetailActivity.this.finish();
        }
    }
}