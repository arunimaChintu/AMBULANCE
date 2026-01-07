package saksham.medrescue.saksham;

import android.content.Intent;
import sachdeva.saksham.medrescue.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class DriverSignup extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private Button mLogin, msignup;

    private AwesomeValidation awesomeValidation;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[a-zA-Z])" +
                    "(?=.*[@#$%^&+=?])" +
                    "(?=\\S+$)" +
                    ".{8,}" +
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_signup);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login);
        msignup = findViewById(R.id.signup);

        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.error_invalid_email);
        awesomeValidation.addValidation(this, R.id.password, PASSWORD_PATTERN, R.string.error_incorrect_password);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                startActivity(new Intent(DriverSignup.this, DriverLoginActivity.class));
                finish();
            }
        };

        msignup.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (awesomeValidation.validate()) {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(DriverSignup.this, task -> {

                            if (!task.isSuccessful()) {
                                Toast.makeText(DriverSignup.this, "Sign Up Error: " +
                                                (task.getException() != null ? task.getException().getMessage() : ""),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // ⭐ IMPORTANT FIX: ADD DRIVER TO DATABASE
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Users")
                                        .child("Drivers")
                                        .child(user.getUid())
                                        .setValue(true);

                                user.sendEmailVerification()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(DriverSignup.this,
                                                        "Registered Successfully! Check your email for verification.",
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(DriverSignup.this,
                                                        "Verification email failed: " + task1.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            FirebaseAuth.getInstance().signOut();
                        });
            }
        });

        mLogin.setOnClickListener(v ->
                startActivity(new Intent(DriverSignup.this, DriverLoginActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
