package com.example.kindnestapp2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    // Regex patterns
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // Firebase Realtime Database reference
        databaseRef = FirebaseDatabase.getInstance("https://kindnest-8d2d9-default-rtdb.firebaseio.com/")
                .getReference("users");

        EditText usernameEditText = findViewById(R.id.username);
        EditText emailEditText = findViewById(R.id.email);
        EditText phoneEditText = findViewById(R.id.phone);
        EditText passwordEditText = findViewById(R.id.password);
        EditText confirmPasswordEditText = findViewById(R.id.confirm_password);
        Button signupButton = findViewById(R.id.signup_button);
        TextView loginLink = findViewById(R.id.login_link);

        signupButton.setOnClickListener(v -> {
            final String username = usernameEditText.getText().toString().trim();
            final String email = emailEditText.getText().toString().trim();
            final String phone = phoneEditText.getText().toString().trim();
            final String password = passwordEditText.getText().toString().trim();
            final String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Basic validation
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!USERNAME_PATTERN.matcher(username).matches()) {
                Toast.makeText(SignupActivity.this, "Enter a valid username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignupActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!PASSWORD_PATTERN.matcher(password).matches()) {
                Toast.makeText(SignupActivity.this, "Weak password: Use upper, lower, digit & special char", Toast.LENGTH_LONG).show();
                return;
            }

            final String role = email.equalsIgnoreCase("admin@kindnest.com") ? "admin" : "user";

            // Create Firebase user
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Send verification email
                        user.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                            if (verifyTask.isSuccessful()) {
                                final String userId = user.getUid();
                                User newUser = new User(username, email, phone, role);

                                // Save user info in database
                                databaseRef.child(userId).setValue(newUser)
                                        .addOnCompleteListener(setUserTask -> {
                                            if (setUserTask.isSuccessful()) {
                                                Toast.makeText(SignupActivity.this,
                                                        "Verification email sent to " + email + ". Please verify before login.",
                                                        Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                Exception e = setUserTask.getException();
                                                String errorMsg = (e != null) ? e.getMessage() : "Unknown error";
                                                Toast.makeText(SignupActivity.this,
                                                        "Error saving user: " + errorMsg,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(SignupActivity.this,
                                        "Failed to send verification email: " + verifyTask.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Sign up failed";
                    Toast.makeText(SignupActivity.this, "Sign up failed: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });

        loginLink.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }
}
