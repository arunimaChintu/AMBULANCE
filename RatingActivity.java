package saksham.medrescue.saksham;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sachdeva.saksham.medrescue.R;

public class RatingActivity extends AppCompatActivity {

    TextView driverName, driverPhone, driverRating;
    RatingBar ratingBar;
    Button submitRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        driverName = findViewById(R.id.driverName);
        driverPhone = findViewById(R.id.driverPhone);
        driverRating = findViewById(R.id.driverRating);
        ratingBar = findViewById(R.id.ratingBar);
        submitRating = findViewById(R.id.submitRating);

        // Load dummy driver info Rstored earlier
        SharedPreferences prefs = getSharedPreferences("DriverInfo", MODE_PRIVATE);

        String name = prefs.getString("name", "Dummy Driver");
        String phone = prefs.getString("phone", "Not Available");
        String rating = prefs.getString("rating", "0.0");

        // Show on screen
        driverName.setText("Driver: " + name);
        driverPhone.setText("Phone: " + phone);
        driverRating.setText("Previous Rating: " + rating);

        // submit button
        submitRating.setOnClickListener(view -> {
            float userRating = ratingBar.getRating();
            Toast.makeText(RatingActivity.this,
                    "Thanks! You rated: " + userRating + "⭐",
                    Toast.LENGTH_LONG).show();

            finish(); // close page
        });
    }
}
