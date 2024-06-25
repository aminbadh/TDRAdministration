package com.aminbadh.tdradministrationlpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.adapter.ClassRecyclerAdapter;
import com.aminbadh.tdradministrationlpm.custom.Class;
import com.aminbadh.tdradministrationlpm.custom.Level;
import com.aminbadh.tdradministrationlpm.databinding.ListMainBinding;
import com.aminbadh.tdradministrationlpm.interfaces.OnMainListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.aminbadh.tdradministrationlpm.custom.Constants.CLASSES_COL;
import static com.aminbadh.tdradministrationlpm.custom.Constants.CLASS_FIELD;
import static com.aminbadh.tdradministrationlpm.custom.Constants.CLASS_OBJECT;
import static com.aminbadh.tdradministrationlpm.custom.Constants.LEVELS_REF;
import static com.aminbadh.tdradministrationlpm.custom.Constants.LEVEL_OBJECT;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarStr;

public class ClassActivity extends AppCompatActivity implements OnMainListener {

    private static final String TAG = ClassActivity.class.getSimpleName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListMainBinding binding;
    private Level level;
    private final ArrayList<Class> classes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the binding object.
        binding = ListMainBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Load the UI.
        loadUI();
    }

    private void loadUI() {
        // Get the objects from the intent.
        getClassIntent();
        // Set the Activity's title.
        setTitle(level.getLevel());
        // Get the data.
        getData();
    }

    private void getClassIntent() {
        // Get objects from the intent.
        level = (Level) getIntent().getSerializableExtra(LEVEL_OBJECT);
        if (level == null) {
            // If one of the objects is null, stop the app.
            Toast.makeText(this, R.string.a_prob_happened_getting_data,
                    Toast.LENGTH_LONG).show();
            ClassActivity.this.finish();
        }
    }

    private void getData() {
        // Show the wait feedback.
        binding.constraintLayoutWaitMainList.setVisibility(View.VISIBLE);
        // Get the classes.
        db.collection(LEVELS_REF).document(level.getDocId()).collection(CLASSES_COL)
                .orderBy(CLASS_FIELD).get().addOnCompleteListener(task -> {
            // Set the wait feedback visibility to Gone.
            binding.constraintLayoutWaitMainList.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                // If the task was successful, loop in the results.
                for (QueryDocumentSnapshot documentSnapshot :
                        Objects.requireNonNull(task.getResult())) {
                    // Convert the QueryDocumentSnapshot object to a Class object.
                    Class mClass = documentSnapshot.toObject(Class.class);
                    // Add the document ID to the Class object.
                    mClass.setDocId(documentSnapshot.getId());
                    // Add the Level object to the Class object.
                    mClass.setLevel(level);
                    // Add the Class object to the classes ArrayList.
                    classes.add(mClass);
                }
            } else {
                // If the task failed, log the exception.
                Log.e(TAG, "onComplete: Task Failed", task.getException());
                // Display the exception's message.
                displaySnackbarStr(binding.getRoot(), Objects.requireNonNull(task
                        .getException()).getMessage());
            }
            // Setup the RecyclerView.
            setUpRecyclerView();
        });
    }

    private void setUpRecyclerView() {
        if (classes.isEmpty()) {
            // If the classes ArrayList is empty, show the no data feedback.
            binding.constraintLayoutNoDataMain.setVisibility(View.VISIBLE);
            binding.recyclerViewMain.setVisibility(View.GONE);
        } else {
            // If there is data in the classes ArrayList,
            // create a ClassRecyclerAdapter object and initialise it.
            ClassRecyclerAdapter adapter = new ClassRecyclerAdapter(classes, this);
            // Setup the RecyclerView.
            binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewMain.setHasFixedSize(true);
            binding.recyclerViewMain.setAdapter(adapter);
        }
    }

    @Override
    public void onClickListener(int position) {
        // Create an Intent object.
        Intent intent = new Intent(ClassActivity.this, RegistrationActivity.class);
        // Add the Class object as an extra to the Intent object.
        intent.putExtra(CLASS_OBJECT, classes.get(position));
        // Start the Intent.
        startActivity(intent);
    }
}