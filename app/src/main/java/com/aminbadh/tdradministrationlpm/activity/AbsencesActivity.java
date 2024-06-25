package com.aminbadh.tdradministrationlpm.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.adapter.ClassAbsencesRecyclerAdapter;
import com.aminbadh.tdradministrationlpm.custom.Class;
import com.aminbadh.tdradministrationlpm.databinding.ActivityAbsencesBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.aminbadh.tdradministrationlpm.custom.Constants.CLASS_OBJECT;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarRef;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarStr;
import static com.aminbadh.tdradministrationlpm.custom.Functions.getInternetConnectionStatus;

public class AbsencesActivity extends AppCompatActivity {

    private static final String TAG = AbsencesActivity.class.getSimpleName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityAbsencesBinding binding;
    private Class mClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the binding object.
        binding = ActivityAbsencesBinding.inflate(getLayoutInflater());
        // Set the Activity's title.
        setTitle(R.string.absences);
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Initialise the Class object.
        mClass = (Class) getIntent().getSerializableExtra(CLASS_OBJECT);
        // get Data.
        getData();
    }

    private void getData() {
        // Get the class document.
        db.document(mClass.getDocRef()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update the Class object.
                mClass = task.getResult().toObject(Class.class);
                // Set the Class' document reference.
                mClass.setDocRef(task.getResult().getReference().getPath());
                // Setup the recyclerView.
                setupRecyclerView();
            } else {
                // Make sure that there is an exception.
                assert task.getException() != null;
                // Log the exception.
                Log.e(TAG, "getData: ", task.getException());
                // Display the exception's message.
                displaySnackbarStr(binding.getRoot(), task.getException().getMessage());
            }
        });
    }

    private void setupRecyclerView() {
        // Create a new ArrayList of Strings to hold the absences list.
        ArrayList<String> absences = new ArrayList<>();
        // Check if the class's absences list is null.
        if (mClass.getAbsences() != null) {
            // Update the absences list to the class's one.
            absences = mClass.getAbsences();
        }
        // Create an final copy of the absences list.
        ArrayList<String> finalAbsences = absences;
        // Create an adapter.
        ClassAbsencesRecyclerAdapter adapter = new ClassAbsencesRecyclerAdapter(absences,
                position -> {
                    // Check internet connection status.
                    if (getInternetConnectionStatus(this)) {
                        // Create and show an AlertDialog.
                        new AlertDialog.Builder(this).setTitle(R.string.remove_student_q)
                                .setMessage(getString(R.string.remove_info_1) + " " + finalAbsences.get(position)
                                        + " " + getString(R.string.remove_info_2))
                                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                    // Remove the student.
                                    finalAbsences.remove(finalAbsences.get(position));
                                    db.document(mClass.getDocRef()).update("absences", finalAbsences)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    finish();
                                                    Toast.makeText(this, R.string.students_removed,
                                                            Toast.LENGTH_LONG).show();
                                                } else {
                                                    assert task.getException() != null;
                                                    Log.e(TAG, "setupRecyclerView: ", task.getException());
                                                    displaySnackbarStr(binding.getRoot(), task.getException().getMessage());
                                                }
                                            });
                                }).setNegativeButton(R.string.no, null).create().show();
                    } else {
                        displaySnackbarRef(binding.getRoot(), R.string.you_are_offline);
                    }
                });
        // Setup the recyclerView.
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }
}