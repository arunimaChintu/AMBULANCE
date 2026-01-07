//package saksham.medrescue.saksham;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import sachdeva.saksham.medrescue.R;
//
//public class DriverLoginActivity extends AppCompatActivity {
//
//    private EditText mEmail, mPassword;
//    private Button mLogin;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_driver_login);
//
//        // UI elements
//        mEmail = findViewById(R.id.email);
//        mPassword = findViewById(R.id.password);
//        mLogin = findViewById(R.id.login);
//
//        // Login button → open map ALWAYS
//        mLogin.setOnClickListener(v -> {
//            saveDummyLoginSession();
//            openDriverMap();
//        });
//    }
//
//    private void saveDummyLoginSession() {
//        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
//        prefs.edit().putBoolean("loggedIn", true).apply();
//    }
//
//    private void openDriverMap() {
//        Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
//}
package saksham.medrescue.saksham;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import sachdeva.saksham.medrescue.R;

public class DriverLoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        // IF USER ALREADY LOGGED IN → OPEN MAP
        if (mAuth.getCurrentUser() != null) {
            openDriverMap();
            return;
        }

        mLogin.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show();
                return;
            }

            loginDriver(email, password);
        });
    }

    private void loginDriver(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        openDriverMap();
                    } else {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openDriverMap() {
        Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
