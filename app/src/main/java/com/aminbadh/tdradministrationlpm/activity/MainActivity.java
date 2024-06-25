package com.aminbadh.tdradministrationlpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.adapter.ClassRecyclerAdapter;
import com.aminbadh.tdradministrationlpm.custom.Class;
import com.aminbadh.tdradministrationlpm.databinding.ActivityMainBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import static com.aminbadh.tdradministrationlpm.custom.Constants.CLASSES_COL;
import static com.aminbadh.tdradministrationlpm.custom.Constants.CLASS_OBJECT;
import static com.aminbadh.tdradministrationlpm.custom.Constants.USERS_COLLECTION;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarStr;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;
    private ActivityMainBinding binding;
    private ClassRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the binding object.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Set the Activity's title.
        setTitle(R.string.app_full);
        // Initialise the auth object.
        auth = FirebaseAuth.getInstance();
        // Review data.
        review();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start the login Activity.
            startActivity(new Intent(this, LoginActivity.class));
            // Stop listening.
            adapter.stopListening();
            // Finish the current Activity.
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (adapter != null) {
            // Stop listening.
            adapter.stopListening();
        }
        super.onDestroy();
    }

    private void review() {
        db.collection(USERS_COLLECTION).document(Objects.requireNonNull(auth.getUid())).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // Setup the recyclerView.
                            setupRecyclerView();
                        } else {
                            // Log a message.
                            Log.e(TAG, "onComplete: User doesn't exist!");
                            // Sign out the user.
                            auth.signOut();
                            // Finish the current Activity.
                            finish();
                        }
                    } else {
                        // Hide the getting data feedback.
                        binding.CLGettingData.setVisibility(View.GONE);
                        // Make sure there is an exception.
                        assert task.getException() != null;
                        // Log the exception.
                        Log.e(TAG, "onFailure: ", task.getException());
                        // Display the exception's message.
                        displaySnackbarStr(binding.getRoot(), task.getException().getMessage());
                    }
                });
    }

    private void setupRecyclerView() {
        // Create a Query.
        Query query = db.collectionGroup(CLASSES_COL).orderBy("data.full");
        // Initialise the adapter.
        adapter = new ClassRecyclerAdapter(new FirestoreRecyclerOptions.Builder<Class>()
                .setQuery(query, Class.class).build(), position -> {
            Intent intent = new Intent(this, RegistrationsActivity.class);
            intent.putExtra(CLASS_OBJECT, adapter.getClass(position));
            startActivity(intent);
        }, () -> {
            if (binding.recyclerView.getVisibility() == View.GONE) {
                binding.recyclerView.setAlpha(0f);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.recyclerView.animate().alpha(1f)
                        .setDuration(getResources().getInteger
                                (android.R.integer.config_mediumAnimTime))
                        .setListener(null);
                binding.CLGettingData.setVisibility(View.GONE);
            }
        });
        // Setup the recyclerView.
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager
                (this, calculateNoOfColumns(400)));
        binding.recyclerView.setAdapter(adapter);
        // Start listening.
        adapter.startListening();
    }

    public int calculateNoOfColumns(float columnWidthDp) {
        // Create a DisplayMetrics object.
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Create a screenWidth variable.
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        // Return the number of possible columns.
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Return true.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Start the SettingsActivity.
        startActivity(new Intent(this, SettingsActivity.class));
        // Return true.
        return true;
    }
}