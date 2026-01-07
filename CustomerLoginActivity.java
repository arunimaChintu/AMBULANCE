//////package saksham.medrescue.saksham;
//////
//////import android.content.Intent;
//////
//////import androidx.annotation.NonNull;
//////import androidx.appcompat.app.AppCompatActivity;
//////import android.os.Bundle;
//////import android.util.Log;
//////import android.view.View;
//////import android.widget.Button;
//////import android.widget.EditText;
//////import android.widget.Toast;
//////
//////import com.google.android.gms.tasks.OnCompleteListener;
//////import com.google.android.gms.tasks.Task;
//////import com.google.firebase.auth.AuthResult;
//////import com.google.firebase.auth.FirebaseAuth;
//////import com.google.firebase.auth.FirebaseUser;
//////import com.google.firebase.database.DatabaseReference;
//////import com.google.firebase.database.FirebaseDatabase;
//////
//////import sachdeva.saksham.medrescue.R;
//////
//////public class CustomerLoginActivity extends AppCompatActivity {
//////
//////
//////    private EditText mEmail, mPassword;
//////    private static final String TAG = "CustomerLoginActivity";
//////
//////    private FirebaseAuth mAuth;
//////    private FirebaseAuth.AuthStateListener firebaseAuthListener;
//////    @Override
//////    protected void onCreate(Bundle savedInstanceState) {
//////        Button mLogin, mRegistration,mForgetPassword;
//////        super.onCreate(savedInstanceState);
//////        setContentView(R.layout.activity_customer_login);
//////
//////        mEmail=  findViewById(R.id.email);
//////        mPassword=  findViewById(R.id.password);
//////
//////        mLogin=  findViewById(R.id.login);
//////        mRegistration= findViewById(sachdeva.saksham.medrescue.R.id.registration);
//////
//////        mForgetPassword = findViewById(R.id.forgetPassword);
//////        mForgetPassword.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View v) {
//////                Intent intent = new Intent(CustomerLoginActivity.this , ResetPasswordActivity.class);
//////                startActivity(intent);
//////                finish();
//////
//////            }
//////        });
//////
//////
//////        mAuth = FirebaseAuth.getInstance();
//////        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//////            @Override
//////            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//////                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//////                if(user!=null && mAuth.getCurrentUser().isEmailVerified()){
//////
//////                    Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
//////                    Toast.makeText(CustomerLoginActivity.this, "Welcome to Med Rescue", Toast.LENGTH_SHORT).show();
//////                    startActivity(intent);
//////                    finish();
//////
//////                }
//////
//////            }
//////        };
//////
//////        mRegistration.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View v) {
//////                Intent intent=new Intent(CustomerLoginActivity.this,CustomerSignup.class);
//////                Log.v(TAG, "First" );
//////                startActivity(intent);
//////                return;
//////
//////            }
//////        });
//////
//////
//////        mLogin.setOnClickListener(new View.OnClickListener(){
//////            @Override
//////            public void onClick (View v){
//////                final String email=mEmail.getText().toString();
//////                final String password =mPassword.getText().toString();
//////                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
//////                    @Override
//////                    public void onComplete(@NonNull Task<AuthResult> task) {
//////                        if(!task.isSuccessful()){
//////
//////                            Toast.makeText(CustomerLoginActivity.this, "Incorrect Email-id/Password.", Toast.LENGTH_SHORT).show();
//////                    }else{
//////                            if(mAuth.getCurrentUser().isEmailVerified()){
//////                                String user_id = mAuth.getCurrentUser().getUid();
//////                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
//////                                current_user_db.setValue(true);
//////
//////                            }else{
//////                                Toast.makeText(CustomerLoginActivity.this, "Please, verify your email.", Toast.LENGTH_SHORT).show();
//////
//////                            }
//////                        }
//////
//////                    }
//////                });
//////
//////
//////            }
//////        });
//////    }
//////
//////    @Override
//////    protected void onStart(){
//////        super.onStart();
//////        mAuth.addAuthStateListener(firebaseAuthListener);
//////    }
//////    @Override
//////    protected void onStop(){
//////        super.onStop();
//////        mAuth.removeAuthStateListener(firebaseAuthListener);
//////    }}
//////////// AAMAR
////////
////////package saksham.medrescue.saksham;
////////
////////import android.content.Intent;
////////import android.os.Bundle;
////////import android.util.Log;
////////import android.view.View;
////////import android.widget.Button;
////////import android.widget.EditText;
////////import android.widget.Toast;
////////
////////import androidx.annotation.NonNull;
////////import androidx.appcompat.app.AppCompatActivity;
////////
////////import com.google.android.gms.tasks.OnCompleteListener;
////////import com.google.android.gms.tasks.Task;
////////import com.google.firebase.auth.AuthResult;
////////import com.google.firebase.auth.FirebaseAuth;
////////import com.google.firebase.auth.FirebaseUser;
////////import com.google.firebase.database.DatabaseReference;
////////import com.google.firebase.database.FirebaseDatabase;
////////
////////import sachdeva.saksham.medrescue.R;
////////
////////public class CustomerLoginActivity extends AppCompatActivity {
////////
////////    private EditText mEmail, mPassword;
////////    private Button mLogin, mRegistration, mForgetPassword;
////////    private static final String TAG = "CustomerLoginActivity";
////////
////////    private FirebaseAuth mAuth;
////////    private FirebaseAuth.AuthStateListener firebaseAuthListener;
////////
//////////    @Override
//////////    protected void onCreate(Bundle savedInstanceState) {
//////////        super.onCreate(savedInstanceState);
//////////        setContentView(R.layout.activity_customer_login);
//////////
//////////        // Initialize views safely
//////////        mEmail = findViewById(R.id.email);
//////////        mPassword = findViewById(R.id.password);
//////////        mLogin = findViewById(R.id.login);
//////////        mRegistration = findViewById(R.id.registration);
//////////        mForgetPassword = findViewById(R.id.forgetPassword);
//////////
//////////        // Firebase Auth instance
//////////        mAuth = FirebaseAuth.getInstance();
//////////
//////////        // Auth state listener
//////////        firebaseAuthListener = firebaseAuth -> {
//////////            FirebaseUser user = mAuth.getCurrentUser();
//////////            if (user != null && user.isEmailVerified()) {
//////////                Toast.makeText(CustomerLoginActivity.this, "Welcome to Med Rescue", Toast.LENGTH_SHORT).show();
//////////                startActivity(new Intent(CustomerLoginActivity.this, CustomerMapActivity.class));
//////////                finish();
//////////            }
//////////        };
//////////
//////////        // Forget password click
//////////        mForgetPassword.setOnClickListener(v -> {
//////////            startActivity(new Intent(CustomerLoginActivity.this, ResetPasswordActivity.class));
//////////        });
//////////
//////////        // Registration click
//////////        mRegistration.setOnClickListener(v -> {
//////////            Log.d(TAG, "Navigating to Customer Signup");
//////////            startActivity(new Intent(CustomerLoginActivity.this, CustomerSignup.class));
//////////        });
//////////
//////////        // Login click
//////////        mLogin.setOnClickListener(v -> {
//////////            String email = mEmail.getText().toString().trim();
//////////            String password = mPassword.getText().toString().trim();
//////////
//////////            if (email.isEmpty() || password.isEmpty()) {
//////////                Toast.makeText(CustomerLoginActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
//////////                return;
//////////            }
//////////
//////////            Log.d(TAG, "Attempting login for: " + email);
//////////
//////////            mAuth.signInWithEmailAndPassword(email, password)
//////////                    .addOnCompleteListener(CustomerLoginActivity.this, task -> {
//////////                        if (!task.isSuccessful()) {
//////////                            Toast.makeText(CustomerLoginActivity.this, "Incorrect Email/Password", Toast.LENGTH_SHORT).show();
//////////                            Log.e(TAG, "Login failed", task.getException());
//////////                        } else {
//////////                            FirebaseUser currentUser = mAuth.getCurrentUser();
//////////                            if (currentUser != null && currentUser.isEmailVerified()) {
//////////                                String userId = currentUser.getUid();
//////////                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
//////////                                        .child("Users").child("Customers").child(userId);
//////////                                dbRef.setValue(true);
//////////
//////////                                startActivity(new Intent(CustomerLoginActivity.this, CustomerMapActivity.class));
//////////                                finish();
//////////                            } else {
//////////                                Toast.makeText(CustomerLoginActivity.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
//////////                            }
//////////                        }
//////////                    });
//////////        });
//////////    }
////////@Override
////////protected void onCreate(Bundle savedInstanceState) {
////////    super.onCreate(savedInstanceState);
////////    setContentView(R.layout.activity_customer_login);
////////
////////    // Firebase Auth instance
////////    mAuth = FirebaseAuth.getInstance();
////////
////////    // ✅ Check if user is already logged in & verified
////////    FirebaseUser currentUser = mAuth.getCurrentUser();
////////    if (currentUser != null && currentUser.isEmailVerified()) {
////////        Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
////////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////////        startActivity(intent);
////////        finish();
////////        return; // Prevent rest of onCreate() from running
////////    }
////////
////////    // Initialize views safely
////////    mEmail = findViewById(R.id.email);
////////    mPassword = findViewById(R.id.password);
////////    mLogin = findViewById(R.id.login);
////////    mRegistration = findViewById(R.id.registration);
////////    mForgetPassword = findViewById(R.id.forgetPassword);
////////
////////    // Auth state listener
////////    firebaseAuthListener = firebaseAuth -> {
////////        FirebaseUser user = mAuth.getCurrentUser();
////////        if (user != null && user.isEmailVerified()) {
////////            Toast.makeText(CustomerLoginActivity.this, "Welcome to Med Rescue", Toast.LENGTH_SHORT).show();
////////            startActivity(new Intent(CustomerLoginActivity.this, CustomerMapActivity.class));
////////            finish();
////////        }
////////    };
////////
////////    // Forget password click
////////    mForgetPassword.setOnClickListener(v -> {
////////        startActivity(new Intent(CustomerLoginActivity.this, ResetPasswordActivity.class));
////////    });
////////
////////    // Registration click
////////    mRegistration.setOnClickListener(v -> {
////////        Log.d(TAG, "Navigating to Customer Signup");
////////        startActivity(new Intent(CustomerLoginActivity.this, CustomerSignup.class));
////////    });
////////
////////    // Login click
////////    mLogin.setOnClickListener(v -> {
////////        String email = mEmail.getText().toString().trim();
////////        String password = mPassword.getText().toString().trim();
////////
////////        if (email.isEmpty() || password.isEmpty()) {
////////            Toast.makeText(CustomerLoginActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
////////            return;
////////        }
////////
////////        Log.d(TAG, "Attempting login for: " + email);
////////
////////        mAuth.signInWithEmailAndPassword(email, password)
////////                .addOnCompleteListener(CustomerLoginActivity.this, task -> {
////////                    if (!task.isSuccessful()) {
////////                        Toast.makeText(CustomerLoginActivity.this, "Incorrect Email/Password", Toast.LENGTH_SHORT).show();
////////                        Log.e(TAG, "Login failed", task.getException());
////////                    } else {
////////                        FirebaseUser currentUserLogin = mAuth.getCurrentUser();
////////                        if (currentUserLogin != null && currentUserLogin.isEmailVerified()) {
////////                            String userId = currentUserLogin.getUid();
////////                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
////////                                    .child("Users").child("Customers").child(userId);
////////                            dbRef.setValue(true);
////////
////////                            startActivity(new Intent(CustomerLoginActivity.this, CustomerMapActivity.class));
////////                            finish();
////////                        } else {
////////                            Toast.makeText(CustomerLoginActivity.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
////////                        }
////////                    }
////////                });
////////    });
////////}
////////
////////
////////    @Override
////////    protected void onStart() {
////////        super.onStart();
////////        mAuth.addAuthStateListener(firebaseAuthListener);
////////    }
////////
////////    @Override
////////    protected void onStop() {
////////        super.onStop();
////////        if (firebaseAuthListener != null) {
////////            mAuth.removeAuthStateListener(firebaseAuthListener);
////////        }
////////    }
////////}
////
////
////
////package saksham.medrescue.saksham;
////
////import android.content.Intent;
////import android.os.Bundle;
////import android.util.Log;
////import android.view.View;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AppCompatActivity;
////
////import com.google.android.gms.tasks.OnCompleteListener;
////import com.google.android.gms.tasks.Task;
////import com.google.firebase.auth.AuthResult;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.auth.FirebaseUser;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////
////import sachdeva.saksham.medrescue.R;
////
////public class CustomerLoginActivity extends AppCompatActivity {
////
////    private static final String TAG = "CustomerLoginActivity";
////
////    private EditText mEmail, mPassword;
////    private Button mLogin, mRegistration, mForgetPassword;
////
////    private FirebaseAuth mAuth;
////    private FirebaseAuth.AuthStateListener firebaseAuthListener;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_customer_login);
////
////        // Initialize views
////        mEmail = findViewById(R.id.email);
////        mPassword = findViewById(R.id.password);
////        mLogin = findViewById(R.id.login);
////        mRegistration = findViewById(R.id.registration);
////        mForgetPassword = findViewById(R.id.forgetPassword);
////
////        // Initialize Firebase Auth
////        mAuth = FirebaseAuth.getInstance();
////
////        // Auth state listener
////        firebaseAuthListener = firebaseAuth -> {
////            FirebaseUser user = mAuth.getCurrentUser();
////            if (user != null && user.isEmailVerified()) {
////                Toast.makeText(CustomerLoginActivity.this, "Welcome to Med Rescue", Toast.LENGTH_SHORT).show();
////                navigateToMap();
////            }
////        };
////
////        // Check if already logged in & verified
////        FirebaseUser currentUser = mAuth.getCurrentUser();
////        if (currentUser != null && currentUser.isEmailVerified()) {
////            navigateToMap();
////            return;
////        }
////
////        // Forget Password
////        mForgetPassword.setOnClickListener(v -> startActivity(new Intent(CustomerLoginActivity.this, ResetPasswordActivity.class)));
////
////        // Registration
////        mRegistration.setOnClickListener(v -> {
////            Log.d(TAG, "Navigating to Customer Signup");
////            startActivity(new Intent(CustomerLoginActivity.this, CustomerSignup.class));
////        });
////
////        // Login
////        mLogin.setOnClickListener(v -> {
////            String email = mEmail.getText().toString().trim();
////            String password = mPassword.getText().toString().trim();
////
////            if (email.isEmpty() || password.isEmpty()) {
////                Toast.makeText(CustomerLoginActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
////                return;
////            }
////
////            mAuth.signInWithEmailAndPassword(email, password)
////                    .addOnCompleteListener(CustomerLoginActivity.this, task -> {
////                        if (!task.isSuccessful()) {
////                            Toast.makeText(CustomerLoginActivity.this, "Incorrect Email/Password", Toast.LENGTH_SHORT).show();
////                            Log.e(TAG, "Login failed", task.getException());
////                        } else {
////                            FirebaseUser userLogin = mAuth.getCurrentUser();
////                            if (userLogin != null && userLogin.isEmailVerified()) {
////                                String userId = userLogin.getUid();
////                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
////                                        .child("Users").child("Customers").child(userId);
////                                dbRef.setValue(true);
////
////                                navigateToMap();
////                            } else {
////                                Toast.makeText(CustomerLoginActivity.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
////                            }
////                        }
////                    });
////        });
////    }
////
////    private void navigateToMap() {
////        Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////        startActivity(intent);
////        finish();
////    }
////
////    @Override
////    protected void onStart() {
////        super.onStart();
////        if (firebaseAuthListener != null) {
////            mAuth.addAuthStateListener(firebaseAuthListener);
////        }
////    }
////
////    @Override
////    protected void onStop() {
////        super.onStop();
////        if (firebaseAuthListener != null) {
////            mAuth.removeAuthStateListener(firebaseAuthListener);
////        }
////    }
////}
//package saksham.medrescue.saksham;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import sachdeva.saksham.medrescue.R;
//
//public class CustomerLoginActivity extends AppCompatActivity {
//
//    private static final String TAG = "CustomerLoginActivity";
//
//    private EditText mEmail, mPassword;
//    private Button mLogin, mRegistration;
//    private TextView mForgetPassword;
//
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_customer_login);
//
//        // 🔹 Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        // 🔹 If user is already logged in and verified, go directly to map
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null && currentUser.isEmailVerified()) {
//            Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//            return; // Stop executing rest of onCreate()
//        }
//
//        // 🔹 Initialize views
//        mEmail = findViewById(R.id.email);
//        mPassword = findViewById(R.id.password);
//        mLogin = findViewById(R.id.login);
//        mRegistration = findViewById(sachdeva.saksham.medrescue.R.id.registration);
//        mForgetPassword = findViewById(R.id.forgetPassword);
//
//        // 🔹 Login button
//        mLogin.setOnClickListener(v -> {
//            String email = mEmail.getText().toString().trim();
//            String password = mPassword.getText().toString().trim();
//
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(CustomerLoginActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(task -> {
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(CustomerLoginActivity.this, "Incorrect Email/Password", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Login failed", task.getException());
//                        } else {
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            if (user != null && user.isEmailVerified()) {
//                                // Optional: write to database
//                                String userId = user.getUid();
//                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
//                                        .child("Users").child("Customers").child(userId);
//                                dbRef.setValue(true);
//
//                                // Go to Map
//                                Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                                finish();
//                            } else {
//                                Toast.makeText(CustomerLoginActivity.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        });
//
//        // 🔹 Registration button
//        mRegistration.setOnClickListener(v -> {
//            startActivity(new Intent(CustomerLoginActivity.this, CustomerSignup.class));
//        });
//
//        // 🔹 Forget password
//        mForgetPassword.setOnClickListener(v -> {
//            startActivity(new Intent(CustomerLoginActivity.this, ResetPasswordActivity.class));
//        });
//    }
//}
//3
package saksham.medrescue.saksham;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sachdeva.saksham.medrescue.R; // <-- adjust this import if your app package is different

public class CustomerLoginActivity extends AppCompatActivity {

    private static final String TAG = "CustomerLoginActivity";

    private EditText mEmail, mPassword;
    private Button mLogin, mRegistration, mForgetPassword;

    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the layout name you provided earlier
        setContentView(R.layout.activity_customer_login);

        // init views — IDs must match those in customerlogin.xml
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login);
        mRegistration = findViewById(R.id.registration);
        mForgetPassword = findViewById(R.id.forgetPassword);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();

        // Auth state listener (navigates to map if already logged-in + verified)
//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // user signed in; check email verification
//                    if (user.isEmailVerified()) {
//                        // safe navigation to map activity
//                        Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        Toast.makeText(CustomerLoginActivity.this, "Welcome to Med Rescue", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        // user exists but not verified
//                        // Do not automatically sign them out here; just inform them
//                        Log.d(TAG, "User not verified yet: " + user.getEmail());
//                    }
//                }
//            }
//        };

        // forget password click -> open ResetPasswordActivity (assumed to exist)
        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerLoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        // registration click -> open CustomerSignup (assumed to exist)
        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerLoginActivity.this, CustomerSignup.class);
                startActivity(intent);
            }
        });

        // login click -> attempt sign in with Firebase
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();

                // basic validation
                if (email.isEmpty()) {
                    mEmail.setError("Enter email");
                    mEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    mPassword.setError("Enter password");
                    mPassword.requestFocus();
                    return;
                }

                // sign in
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // helpful error message and log
                                    String err = "Authentication failed.";
                                    if (task.getException() != null && task.getException().getMessage() != null) {
                                        err = task.getException().getMessage();
                                    }
                                    Toast.makeText(CustomerLoginActivity.this, "Login failed: " + err, Toast.LENGTH_LONG).show();
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                } else {
                                    // signed in successfully; check verification
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null && user.isEmailVerified()) {
                                        // write to Realtime Database that this user is a Customer
                                        String userId = user.getUid();
                                        DatabaseReference currentUserDb = FirebaseDatabase.getInstance()
                                                .getReference().child("Users").child("Customers").child(userId);
                                        // set a simple boolean or you can write user info object later
                                        currentUserDb.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> dbTask) {
                                                if (dbTask.isSuccessful()) {
                                                    // go to map
                                                    Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    // db write failed — still allow navigation or show toast
                                                    Toast.makeText(CustomerLoginActivity.this, "Login succeeded but DB write failed.", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });
                                    } else {
                                        // user is not verified
                                        Toast.makeText(CustomerLoginActivity.this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                                        // optionally sign out to clear session
                                        mAuth.signOut();
                                    }
                                }
                            }
                        });
            }
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mAuth != null && firebaseAuthListener != null) {
//            mAuth.addAuthStateListener(firebaseAuthListener);
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mAuth != null && firebaseAuthListener != null) {
//            mAuth.removeAuthStateListener(firebaseAuthListener);
//        }
//    }
}
