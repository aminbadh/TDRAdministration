package com.aminbadh.tdradministrationlpm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.custom.LocaleHelper;
import com.aminbadh.tdradministrationlpm.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.aminbadh.tdradministrationlpm.custom.Constants.ROLE_ADMIN;
import static com.aminbadh.tdradministrationlpm.custom.Constants.ROLE_DEV;
import static com.aminbadh.tdradministrationlpm.custom.Constants.ROLE_FIELD;
import static com.aminbadh.tdradministrationlpm.custom.Constants.ROLE_MANAGER;
import static com.aminbadh.tdradministrationlpm.custom.Constants.USERS_COLLECTION;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarRef;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarStr;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set activity's title.
        setTitle(R.string.login);
        // Initialise the binding object.
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // Set Activity's content view.
        setContentView(binding.getRoot());
        // Initialise the auth object.
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            // If the current user isn't null,
            // start the MainActivity.
            startMainActivity();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public void login(View view) {
        // Retrieve texts from the email and password EditTexts.
        String email = binding.editTextEmailAddress.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        // Make sure that the data is not empty.
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            // If the data is empty, inform the user that he must fill the inputs.
            displaySnackbarRef(binding.getRoot(), R.string.fill_inputs);
        } else {
            // Disable the login button.
            enableButtons(false);
            // Set the wait feedback visibility to visible.
            setWaitFeedbackVisibility(true);
            // Request the focus for the root.
            binding.getRoot().requestFocus();
            // Login the user using the given data.
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Create a FirebaseUser variable and assign to it the current user.
                            FirebaseUser user = auth.getCurrentUser();
                            // Log the ID of the current user.
                            assert user != null;
                            Log.i(TAG, "onComplete: User signed in successfully => " +
                                    user.getUid());
                            // Check user's role.
                            checkUserDoc(user.getUid());
                        } else {
                            // Set the wait feedback visibility to Gone.
                            setWaitFeedbackVisibility(false);
                            // Enable the login button.
                            enableButtons(true);
                            // Log the exception.
                            Log.e(TAG, "onComplete: User sign in failed", task.getException());
                            // Display the exception's message.
                            displaySnackbarStr(binding.getRoot(), Objects
                                    .requireNonNull(task.getException()).getMessage());
                        }
                    });
        }
    }

    private void checkUserDoc(String id) {
        // Get the user's document.
        db.collection(USERS_COLLECTION).document(id).get()
                .addOnCompleteListener(task -> {
                    // Set the wait feedback visibility to Gone.
                    setWaitFeedbackVisibility(false);
                    if (task.isSuccessful()) {
                        // Create a String variable that holds the user's role.
                        String role = task.getResult().getString(ROLE_FIELD);
                        if (role != null && (role.equals(ROLE_ADMIN)
                                || role.equals(ROLE_MANAGER) || role.equals(ROLE_DEV))) {
                            // Start the MainActivity.
                            startMainActivity();
                        } else {
                            // Enable the buttons.
                            enableButtons(true);
                            // Inform the user that his role isn't compatible.
                            displaySnackbarRef(binding.getRoot(), R.string.current_role_false);
                            // Logout the user.
                            auth.signOut();
                        }
                    } else {
                        // Make sure that there's an exception.
                        assert task.getException() != null;
                        // Enable the buttons.
                        enableButtons(true);
                        // Log the exception.
                        Log.e(TAG, "onFailure: Doc", task.getException());
                        // Display the exception's message.
                        displaySnackbarStr(binding.getRoot(), task.getException().getMessage());
                        // Logout the user.
                        auth.signOut();
                    }
                });
    }

    private void startMainActivity() {
        // Start the intent going to the MainActivity class.
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        // Finish the current activity.
        LoginActivity.this.finish();
    }

    private void setWaitFeedbackVisibility(boolean b) {
        if (b) {
            // Show the CLWait.
            binding.CLWait.setVisibility(View.VISIBLE);
        } else {
            // Hide the CLWait.
            binding.CLWait.setVisibility(View.GONE);
        }
    }

    private void enableButtons(boolean b) {
        // Enable or disable the buttons.
        binding.buttonLogin.setEnabled(b);
    }
}