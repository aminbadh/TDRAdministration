package com.aminbadh.tdradministrationlpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.adapter.RegistrationsRecyclerAdapter;
import com.aminbadh.tdradministrationlpm.custom.Class;
import com.aminbadh.tdradministrationlpm.custom.Registration;
import com.aminbadh.tdradministrationlpm.databinding.ActivityRegistrationsBinding;
import com.aminbadh.tdradministrationlpm.interfaces.OnDataAction;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.aminbadh.tdradministrationlpm.custom.Constants.CLASS_OBJECT;
import static com.aminbadh.tdradministrationlpm.custom.Constants.REGISTRATIONS_COL;
import static com.aminbadh.tdradministrationlpm.custom.Constants.REGISTRATION_OBJECT;

public class RegistrationsActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityRegistrationsBinding binding;
    private RegistrationsRecyclerAdapter adapter;
    private Class mClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the binding object.
        binding = ActivityRegistrationsBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Initialise the class object.
        mClass = (Class) getIntent().getSerializableExtra(CLASS_OBJECT);
        // Set the Activity's title.
        setTitle(mClass.getClassName());
        // Setup recyclerView.
        setupRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start listening.
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening.
        adapter.stopListening();
    }

    private void setupRecyclerView() {
        // Initialise the adapter.
        adapter = new RegistrationsRecyclerAdapter(new FirestorePagingOptions.Builder<Registration>()
                .setLifecycleOwner(this).setQuery(db.document(mClass.getDocRef()).collection(REGISTRATIONS_COL)
                                .orderBy("submitTime", Query.Direction.DESCENDING),
                        new PagedList.Config.Builder().setEnablePlaceholders(false)
                                .setPrefetchDistance(2).setPageSize(4).build(), Registration.class)
                .build(), binding.swipeRefreshLayout, new OnDataAction() {
            @Override
            public void onData() {
                binding.CLNoData.setVisibility(View.GONE);
            }

            @Override
            public void onEmpty() {
                binding.CLNoData.setVisibility(View.VISIBLE);
            }
        }, position -> {
            // Send an intent to the DetailsActivity.
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(REGISTRATION_OBJECT, adapter.getRegistration(position));
            startActivity(intent);
        });
        // Setup the swipeRefreshLayout.
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.cyan_secondary);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> adapter.refresh());
        // Setup the recyclerView.
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager
                (this, calculateNoOfColumns(400)));
        binding.recyclerView.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.menu_registrations, menu);
        // Return true.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Finish the current Activity.
            finish();
        } else {
            // Send an intent to the AbsencesActivity.
            Intent intent = new Intent(this, AbsencesActivity.class);
            intent.putExtra(CLASS_OBJECT, mClass);
            startActivity(intent);
        }
        return true;
    }
}