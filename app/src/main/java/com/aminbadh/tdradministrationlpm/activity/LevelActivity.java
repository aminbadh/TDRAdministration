package com.aminbadh.tdradministrationlpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.adapter.LevelRecyclerAdapter;
import com.aminbadh.tdradministrationlpm.custom.Level;
import com.aminbadh.tdradministrationlpm.databinding.ListMainBinding;
import com.aminbadh.tdradministrationlpm.interfaces.OnMainListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.aminbadh.tdradministrationlpm.custom.Constants.LEVELS_REF;
import static com.aminbadh.tdradministrationlpm.custom.Constants.LEVEL_FIELD;
import static com.aminbadh.tdradministrationlpm.custom.Constants.LEVEL_OBJECT;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarStr;

public class LevelActivity extends AppCompatActivity implements OnMainListener {

    private static final String TAG = LevelActivity.class.getSimpleName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListMainBinding binding;
    private final ArrayList<Level> levels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the binding object.
        binding = ListMainBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Get data.
        getData();
    }

    private void getData() {
        // Show the wait feedback.
        binding.constraintLayoutWaitMainList.setVisibility(View.VISIBLE);
        db.collection(LEVELS_REF).orderBy(LEVEL_FIELD).get()
                .addOnCompleteListener(task -> {
                    // Set the wait feedback visibility to Gone.
                    binding.constraintLayoutWaitMainList.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // If the task was successful,
                        // loop in each documentSnapshot and do the following.
                        for (QueryDocumentSnapshot documentSnapshot :
                                Objects.requireNonNull(task.getResult())) {
                            // Convert the documentSnapshot to a Level object.
                            Level level = documentSnapshot.toObject(Level.class);
                            // Set the Level object Id.
                            level.setDocId(documentSnapshot.getId());
                            // Add the Level object.
                            levels.add(level);
                        }
                    } else {
                        // If the task failed, log the exception.
                        Log.e(TAG, "onComplete: Task Failed", task.getException());
                        // Display the exception's message.
                        displaySnackbarStr(binding.getRoot(), Objects.requireNonNull(task
                                .getException()).getMessage());
                    }
                    // Load the UI.
                    loadUI();
                });
    }

    private void loadUI() {
        if (levels.isEmpty()) {
            // If the levels ArrayList is empty, inform the user.
            binding.constraintLayoutNoDataMain.setVisibility(View.VISIBLE);
            binding.recyclerViewMain.setVisibility(View.GONE);
        } else {
            // If the levels ArrayList has data, setup the RecyclerView to display the levels.
            // First, create and initialise a LevelRecyclerAdapter.
            LevelRecyclerAdapter adapter = new LevelRecyclerAdapter(levels, this);
            // Setup the RecyclerView.
            binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewMain.setHasFixedSize(true);
            binding.recyclerViewMain.setAdapter(adapter);
        }
    }

    @Override
    public void onClickListener(int position) {
        // Create an Intent going to the ClassActivity.
        Intent intent = new Intent(this, ClassActivity.class);
        // Add the Level object as an extra.
        intent.putExtra(LEVEL_OBJECT, levels.get(position));
        // Start the operation.
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.men_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(LevelActivity.this, LoginActivity.class));
            LevelActivity.this.finish();
        }
        return true;
    }
}