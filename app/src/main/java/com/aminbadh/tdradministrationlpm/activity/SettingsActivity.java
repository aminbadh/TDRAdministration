package com.aminbadh.tdradministrationlpm.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aminbadh.tdradministrationlpm.R;
import com.aminbadh.tdradministrationlpm.custom.LocaleHelper;
import com.aminbadh.tdradministrationlpm.databinding.ActivitySettingsBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.aminbadh.tdradministrationlpm.custom.Constants.ENGLISH;
import static com.aminbadh.tdradministrationlpm.custom.Constants.FRENCH;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarRef;
import static com.aminbadh.tdradministrationlpm.custom.Functions.displaySnackbarStr;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private ActivitySettingsBinding binding;
    private FirebaseAuth auth;
    private String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the binding object.
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        // Set orientation to locked.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Set the Activity's title.
        setTitle(R.string.settings);
        // Initialise the auth object.
        auth = FirebaseAuth.getInstance();
        // Setup language.
        setupLanguage();
        // Setup email.
        setupEmail();
        // Setup password.
        setupPassword();
        // Setup version.
        setupVersion();
        // Setup support.
        setupSupport();
        // Setup logout.
        setupLogout();
    }

    private void setupLanguage() {
        // Create an int variable that holds the language index.
        int languageIndex = -1;
        // Create a String array with the supported languages in it.
        String[] languages = {getString(R.string.english), getString(R.string.french)};
        // Get the locale code and store it in a String variable.
        String locale = LocaleHelper.getLanguage(this);
        // Check the locale.
        if (locale.equals(ENGLISH)) {
            // update the languageIndex to 0.
            languageIndex = 0;
            // update the textViewLanguage's text value.
            binding.textViewLanguage.setText(R.string.english);
        } else if (locale.equals(FRENCH)) {
            // update the languageIndex to 1.
            languageIndex = 1;
            // update the textViewLanguage's text value.
            binding.textViewLanguage.setText(R.string.french);
        }
        // Create a final copy of the languageIndex variable.
        int finalLanguageIndex = languageIndex;
        // Set the CLLanguage's onClickListener. | Create a new AlertDialog#Builder.
        binding.CLLanguage.setOnClickListener(view -> new AlertDialog.Builder(SettingsActivity.this)
                // Set the dialog's title.
                .setTitle(R.string.select_language)
                // Set the dialog's content.
                .setSingleChoiceItems(languages, finalLanguageIndex, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            // Update the language to english.
                            LocaleHelper.setLocale(SettingsActivity.this, ENGLISH);
                            break;
                        case 1:
                            // Update the language to french.
                            LocaleHelper.setLocale(SettingsActivity.this, FRENCH);
                            break;
                    }
                    // Dismiss the dialog.
                    dialogInterface.dismiss();
                    // Restart the method.
                    setupLanguage();
                    // Inform the user that he need to restart.
                    displayRestart();
                }).show());
    }

    private void setupEmail() {
        // Set the CLEmail's onClickListener.
        binding.CLEmail.setOnClickListener(view -> {
            // Create a view from the view_email_change layout.
            @SuppressLint("InflateParams") final View parent = getLayoutInflater()
                    .inflate(R.layout.view_email_change, null);
            // Show a new AlertDialog#Builder.
            new AlertDialog.Builder(this)
                    // Set the dialog title.
                    .setTitle(R.string.change_email)
                    .setView(parent)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        EditText editText = parent.findViewById(R.id.editTextNewEmail);
                        String email = editText.getText().toString();
                        if (email.trim().isEmpty()) {
                            displaySnackbarRef(binding.getRoot(), R.string.fill_inputs);
                        } else {
                            // Inform the user that the operation started.
                            displaySnackbarStr(binding.getRoot(), getString(R.string.updating));
                            // Update the user's email address.
                            Objects.requireNonNull(auth.getCurrentUser()).updateEmail(email).
                                    addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // If the task was successful, log a message.
                                            Log.i(TAG, "onComplete: Email Address changed.");
                                            // Display a message for the user.
                                            displaySnackbarRef(binding.getRoot(),
                                                    R.string.email_updated);
                                        } else {
                                            // If the task failed, log the exception.
                                            Log.e(TAG, "onComplete: ", task.getException());
                                            // Display the exception's message.
                                            displaySnackbarStr(binding.getRoot(),
                                                    Objects.requireNonNull(task.getException())
                                                            .getMessage());
                                        }
                                    });
                        }
                    }).setNegativeButton(R.string.no, null).show();
        });
    }

    private void setupPassword() {
        // Set the CLPassword's onClickListener.
        binding.CLPassword.setOnClickListener(view -> {
            // Create a view from the view_password_change layout.
            @SuppressLint("InflateParams") final View parent = getLayoutInflater()
                    .inflate(R.layout.view_password_change, null);
            // Show a new AlertDialog#Builder.
            new AlertDialog.Builder(this)
                    // Set the dialog title.
                    .setTitle(R.string.change_password)
                    .setView(parent)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        EditText editText = parent.findViewById(R.id.editTextNewPasswordChange);
                        String password = editText.getText().toString();
                        if (password.trim().isEmpty()) {
                            displaySnackbarRef(binding.getRoot(), R.string.fill_inputs);
                        } else {
                            // Inform the user that the operation started.
                            displaySnackbarStr(binding.getRoot(), getString(R.string.updating));
                            // Update the user's email address.
                            Objects.requireNonNull(auth.getCurrentUser()).updatePassword(password).
                                    addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // If the task was successful, log a message.
                                            Log.i(TAG, "onComplete: Password changed.");
                                            // Display a message for the user.
                                            displaySnackbarRef(binding.getRoot(),
                                                    R.string.password_updated);
                                        } else {
                                            // If the task failed, log the exception.
                                            Log.e(TAG, "onComplete: ", task.getException());
                                            // Display the exception's message.
                                            displaySnackbarStr(binding.getRoot(),
                                                    Objects.requireNonNull(task.getException())
                                                            .getMessage());
                                        }
                                    });
                        }
                    }).setNegativeButton(R.string.no, null).show();
        });
    }

    private void setupVersion() {
        // Create a String variable that holds the version name.
        versionName = getString(R.string.v);
        // Get the version.
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName += pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // Update the textViewVersion's text value.
        binding.textViewVersion.setText(versionName);
    }

    private void setupSupport() {
        String[] addresses = {getString(R.string.dev_email_address)};
        String subject = getString(R.string.feedback_from) + " " + auth.getUid();
        String body = "\n\n" + getString(R.string.do_not_delete) + "\n"
                + getString(R.string.app_package) + " " + getApplicationContext().getPackageName()
                + "\n" + getString(R.string.app_version) + " " + versionName;
        binding.CLSupport.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });
    }

    private void setupLogout() {
        // Set the CLLogout's onClickListener.
        binding.CLLogout.setOnClickListener(view ->
                // Create a new AlertDialog#Builder.
                new AlertDialog.Builder(SettingsActivity.this)
                        // Set the dialog's title.
                        .setTitle(R.string.logout_q)
                        // Set the dialog's content.
                        .setMessage(R.string.logout_info)
                        // Set the dialog's positive button.
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            // Logout the user.
                            FirebaseAuth.getInstance().signOut();
                            // Finish the current Activity.
                            finish();
                        })
                        // Set the dialog's negative button.
                        .setNegativeButton(R.string.no, null)
                        // Show the dialog.
                        .show());
    }

    private void displayRestart() {
        // Show a Snackbar informing the user to restart the app.
        Snackbar.make(binding.getRoot(), R.string.restart_to_change_the_language,
                BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.exit_now,
                view -> SettingsActivity.this.finishAffinity()).show();
    }
}