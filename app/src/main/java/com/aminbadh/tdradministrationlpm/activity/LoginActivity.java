package com.aminbadh.tdradministrationlpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.aminbadh.tdradministrationlpm.custom.Constants.ROLE_ADMIN;
import static com.aminbadh.tdradministrationlpm.custom.Constants.ROLE_DEV;
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
        // Set the wait feedback visibility to gone.
        setWaitFeedbackVisibility(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            // Disable the login button.
            enableLoginButton(false);
            // Set the wait feedback visibility to visible.
            setWaitFeedbackVisibility(true);
            // Check user document.
            checkUserDoc(auth.getCurrentUser().getUid());
        }
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
            enableLoginButton(false);
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
                            enableLoginButton(true);
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
        // Get user document from the database.
        db.collection(USERS_COLLECTION).document(id).get()
                .addOnSuccessListener(documentSnapshot ->
                        // Update the UI using the following method.
                        updateUI(Objects.requireNonNull(documentSnapshot.getString("role"))))
                .addOnFailureListener(e -> {
                    // Set the wait feedback visibility to Gone.
                    setWaitFeedbackVisibility(false);
                    // Enable the login button.
                    enableLoginButton(true);
                    // Log the exception.
                    Log.e(TAG, "onFailure: Doc", e);
                    // Display the exception's message.
                    displaySnackbarStr(binding.getRoot(), e.getMessage());
                    // Log out the user.
                    auth.signOut();
                });
    }

    private void updateUI(String role) {
        // Set the wait feedback visibility to Gone.
        setWaitFeedbackVisibility(false);
        if (role.equals(ROLE_ADMIN) || role.equals(ROLE_DEV)) {
            // If the user's role is an Admin or a Developer,
            // create a new Intent object going to the MainActivity class.
            Intent intent = new Intent(LoginActivity.this, LevelActivity.class);
            // Start the intent.
            startActivity(intent);
            // Finish the current activity.
            LoginActivity.this.finish();
        } else {
            // Enable the login button.
            enableLoginButton(true);
            // Inform the user that his role isn't compatible.
            displaySnackbarRef(binding.getRoot(), R.string.current_role_false);
            auth.signOut();
        }
    }

    private void setWaitFeedbackVisibility(boolean b) {
        if (b) {
            binding.constraintLayoutWait.setVisibility(View.VISIBLE);
        } else {
            binding.constraintLayoutWait.setVisibility(View.GONE);
        }
    }

    private void enableLoginButton(boolean b) {
        binding.buttonLogin.setEnabled(b);
    }
}