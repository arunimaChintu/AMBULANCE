////
//////WORKING CODE 2
////package saksham.medrescue.saksham;
////
////import sachdeva.saksham.medrescue.R;
////
////import android.Manifest;
////import android.content.Intent;
////import android.content.pm.PackageManager;
////import android.location.Location;
////import android.os.Bundle;
////import android.os.Looper;
////import android.view.View;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.LinearLayout;
////import android.widget.RatingBar;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AlertDialog;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.core.app.ActivityCompat;
////import androidx.core.content.ContextCompat;
////
////import com.firebase.geofire.GeoFire;
////import com.firebase.geofire.GeoLocation;
////import com.firebase.geofire.GeoQuery;
////import com.firebase.geofire.GeoQueryEventListener;
////import com.google.android.gms.common.api.Status;
////import com.google.android.gms.location.FusedLocationProviderClient;
////import com.google.android.gms.location.LocationCallback;
////import com.google.android.gms.location.LocationRequest;
////import com.google.android.gms.location.LocationResult;
////import com.google.android.gms.location.LocationServices;
////import com.google.android.gms.maps.CameraUpdateFactory;
////import com.google.android.gms.maps.GoogleMap;
////import com.google.android.gms.maps.OnMapReadyCallback;
////import com.google.android.gms.maps.SupportMapFragment;
////import com.google.android.gms.maps.model.BitmapDescriptorFactory;
////import com.google.android.gms.maps.model.LatLng;
////import com.google.android.gms.maps.model.Marker;
////import com.google.android.gms.maps.model.MarkerOptions;
////import com.google.android.libraries.places.api.Places;
////import com.google.android.libraries.places.api.model.Place;
////import com.google.android.libraries.places.api.model.TypeFilter;
////import com.google.android.libraries.places.widget.Autocomplete;
////import com.google.android.libraries.places.widget.AutocompleteActivity;
////import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////
////import java.util.ArrayList;
////import java.util.Arrays;
////import java.util.HashMap;
////import java.util.List;
////
////public class CustomerMapActivity extends AppCompatActivity implements OnMapReadyCallback {
////
////    private GoogleMap mMap;
////    private LinearLayout driverInfoCard;
////    private TextView cardDriverName, cardDriverVehicle, cardDriverPhone;
////    private Button callDriverBtn;
////    private String selectedDriverPhone = "";
////
////    private FusedLocationProviderClient mFusedLocationClient;
////    private Location mLastLocation;
////    private LocationRequest mLocationRequest;
////
////    private Button mLogout, mRequest, mSettings, mHistory;
////    private LinearLayout mDriverInfo;
////    private TextView mDriverName, mDriverPhone, mDriverCar;
////    private RatingBar mRatingBar;
////
////    private EditText mFromLocation, mToLocation;
////
////    private LatLng pickupLocation;
////    private Boolean requestBol = false;
////    private Marker pickupMarker;
////    private Marker mDriverMarker;
////
////    private String destination;
////    private LatLng destinationLatlng;
////
////    private int radius = 1;
////    private Boolean driverFound = false;
////    private String driverFoundID;
////
////    private GeoQuery geoQuery;
////    private DatabaseReference driverLocationRef, driveHasEndedRef;
////    private ValueEventListener driverLocationRefListener, driveHasEndedRefListener;
////
////    private static final int AUTOCOMPLETE_FROM_REQUEST_CODE = 1001;
////    private static final int AUTOCOMPLETE_TO_REQUEST_CODE = 1002;
////
////    // To store multiple available driver markers
////    private HashMap<String, Marker> availableDriverMarkers = new HashMap<>();
////
////
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_customer_map);
////        driverInfoCard = findViewById(R.id.driver_info_card);
////        cardDriverName = findViewById(R.id.card_driver_name);
////        cardDriverVehicle = findViewById(R.id.card_driver_vehicle);
////        cardDriverPhone = findViewById(R.id.card_driver_phone);
////        callDriverBtn = findViewById(R.id.btn_call_driver);
////
////
////
////        if (!Places.isInitialized()) {
////            Places.initialize(getApplicationContext(), "AIzaSyCPwOM_929IDlkLCOs3N7u2OsWVISeCuKU");
////        }
////
////        mLogout = findViewById(R.id.logout);
////        mRequest = findViewById(R.id.request);
////        mSettings = findViewById(R.id.settings);
////        mHistory = findViewById(R.id.history);
////        mDriverInfo = findViewById(R.id.driverInfo);
////        mDriverName = findViewById(R.id.driverName);
////        mDriverPhone = findViewById(R.id.driverPhone);
////        mDriverCar = findViewById(R.id.driverCar);
////        mRatingBar = findViewById(R.id.ratingBar);
////
////        mFromLocation = findViewById(R.id.fromLocation);
////        mToLocation = findViewById(R.id.toLocation);
////
////        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
////
////        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
////                .findFragmentById(R.id.map);
////        if (mapFragment != null) mapFragment.getMapAsync(this);
////
////        mFromLocation.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_FROM_REQUEST_CODE));
////        mToLocation.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_TO_REQUEST_CODE));
////
////        mLogout.setOnClickListener(v -> {
////            FirebaseAuth.getInstance().signOut();
////            startActivity(new Intent(CustomerMapActivity.this, Welcome_Activity.class));
////            finish();
////        });
////
////        mRequest.setOnClickListener(v -> {
////            if (requestBol) {
////                endRide();
////            } else {
////                if (mLastLocation == null) {
////                    Toast.makeText(this, "Please wait for location...", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////
////                requestBol = true;
////                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
////                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
////                new GeoFire(ref).setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
////
////                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
////                pickupMarker = mMap.addMarker(new MarkerOptions()
////                        .position(pickupLocation)
////                        .title("Pickup Here")
////                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));
////
////                // ✅ NEW: Show all available drivers on map
////                showAllAvailableDrivers();
////
////                mRequest.setText("Getting Your Ambulance...");
////                getClosestDriver();
////            }
////        });
////
////        mSettings.setOnClickListener(v -> startActivity(new Intent(CustomerMapActivity.this, CustomerSettingsActivity.class)));
////        mHistory.setOnClickListener(v -> {
////            Intent intent = new Intent(CustomerMapActivity.this, HistoryActivity.class);
////            intent.putExtra("customerOrDriver", "Customers");
////            startActivity(intent);
////        });
////    }
////
////    private void openAutocomplete(int requestCode) {
////        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
////        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
////                .setTypeFilter(TypeFilter.ADDRESS)
////                .setCountry("IN")
////                .build(this);
////        startActivityForResult(intent, requestCode);
////    }
////
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE || requestCode == AUTOCOMPLETE_TO_REQUEST_CODE) {
////            if (resultCode == RESULT_OK) {
////                Place place = Autocomplete.getPlaceFromIntent(data);
////                if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE) {
////                    mFromLocation.setText(place.getAddress());
////                } else {
////                    mToLocation.setText(place.getAddress());
////                    destination = place.getName();
////                    destinationLatlng = place.getLatLng();
////                }
////                if (place.getLatLng() != null && mMap != null) {
////                    mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
////                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
////                }
////            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
////                Status status = Autocomplete.getStatusFromIntent(data);
////                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
////            }
////        }
////    }
////
//////     ✅ NEW FUNCTION: Show all available ambulance drivers
//////    private void showAllAvailableDrivers() {
//////        DatabaseReference driversAvailableRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
//////        driversAvailableRef.addListenerForSingleValueEvent(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot snapshot) {
//////                // Clear old markers
//////                for (Marker marker : availableDriverMarkers.values()) {
//////                    marker.remove();
//////                }
//////                availableDriverMarkers.clear();
//////
//////                for (DataSnapshot child : snapshot.getChildren()) {
//////                    if (child.child("l").exists()) {
//////                        List<Object> loc = (List<Object>) child.child("l").getValue();
//////                        double lat = Double.parseDouble(loc.get(0).toString());
//////                        double lng = Double.parseDouble(loc.get(1).toString());
//////                        LatLng driverLatLng = new LatLng(lat, lng);
//////
//////                        Marker driverMarker = mMap.addMarker(new MarkerOptions()
//////                                .position(driverLatLng)
//////                                .title("Available Ambulance")
//////                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//////                        availableDriverMarkers.put(child.getKey(), driverMarker);
//////                    }
//////                }
//////                Toast.makeText(CustomerMapActivity.this, "Available ambulances shown on map.", Toast.LENGTH_SHORT).show();
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError error) {
//////                Toast.makeText(CustomerMapActivity.this, "Error loading available ambulances.", Toast.LENGTH_SHORT).show();
//////            }
//////        });
//////
//////    }
//////    private void showAllAvailableDrivers() {
//////        DatabaseReference driversAvailableRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
//////
//////        driversAvailableRef.addListenerForSingleValueEvent(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot snapshot) {
//////                // ✅ 1. Remove old markers
//////                for (Marker marker : availableDriverMarkers.values()) {
//////                    marker.remove();
//////                }
//////                availableDriverMarkers.clear();
//////
//////                // ✅ 2. Loop through all drivers in driversAvailable
//////                for (DataSnapshot child : snapshot.getChildren()) {
//////                    if (child.child("l").exists()) {
//////                        List<Object> loc = (List<Object>) child.child("l").getValue();
//////                        double lat = Double.parseDouble(loc.get(0).toString());
//////                        double lng = Double.parseDouble(loc.get(1).toString());
//////                        LatLng driverLatLng = new LatLng(lat, lng);
//////
//////                        String driverId = child.getKey(); // 🔑 Unique driver UID
//////
//////                        // ✅ 3. Fetch driver details from /Users/Drivers/<driverId>
//////                        FirebaseDatabase.getInstance()
//////                                .getReference("Users").child("Drivers").child(driverId)
//////                                .addListenerForSingleValueEvent(new ValueEventListener() {
//////                                    @Override
//////                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//////                                        if (snapshot.exists()) {
//////                                            String name = snapshot.child("Name").getValue(String.class);
//////                                            String vehicle = snapshot.child("VehicleNumber").getValue(String.class);
//////                                            String type = snapshot.child("VehicleType").getValue(String.class);
//////
//////                                            // ✅ 4. Add marker for each driver
//////                                            Marker driverMarker = mMap.addMarker(new MarkerOptions()
//////                                                    .position(driverLatLng)
//////                                                    .title(name != null ? name + " (" + type + ")" : "Available Ambulance")
//////                                                    .snippet(vehicle != null ? "Vehicle: " + vehicle : "")
//////                                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//////
//////                                            if (driverMarker != null) {
//////                                                driverMarker.setTag(driverId); // 🏷️ Store driver UID in marker
//////                                                availableDriverMarkers.put(driverId, driverMarker);
//////                                            }
//////                                        }
//////                                    }
//////
//////                                    @Override
//////                                    public void onCancelled(@NonNull DatabaseError error) {
//////                                        // Ignore single driver fetch failure
//////                                    }
//////                                });
//////                    }
//////                }
//////
//////                Toast.makeText(CustomerMapActivity.this, "Available ambulances shown on map.", Toast.LENGTH_SHORT).show();
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError error) {
//////                Toast.makeText(CustomerMapActivity.this, "Error loading available ambulances.", Toast.LENGTH_SHORT).show();
//////            }
//////        });
//////    }
//////    private void showAllAvailableDrivers() {
//////        DatabaseReference driversAvailableRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
//////        driversAvailableRef.addListenerForSingleValueEvent(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot snapshot) {
//////                // Clear old markers
//////                for (Marker marker : availableDriverMarkers.values()) {
//////                    marker.remove();
//////                }
//////                availableDriverMarkers.clear();
//////
//////                for (DataSnapshot child : snapshot.getChildren()) {
//////                    if (child.child("l").exists()) {
//////                        List<Object> loc = (List<Object>) child.child("l").getValue();
//////                        double lat = Double.parseDouble(loc.get(0).toString());
//////                        double lng = Double.parseDouble(loc.get(1).toString());
//////                        LatLng driverLatLng = new LatLng(lat, lng);
//////
//////                        Marker driverMarker = mMap.addMarker(new MarkerOptions()
//////                                .position(driverLatLng)
//////                                .title("Available Ambulance")
//////                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//////                        availableDriverMarkers.put(child.getKey(), driverMarker);
//////                    }
//////                }
//////                Toast.makeText(CustomerMapActivity.this, "Available ambulances shown on map.", Toast.LENGTH_SHORT).show();
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError error) {
//////                Toast.makeText(CustomerMapActivity.this, "Error loading available ambulances.", Toast.LENGTH_SHORT).show();
//////
//////
//////            }
//////        });
//////
//////        // ✅ STEP 5: Listen for real-time updates in driver locations
//////        driversAvailableRef.addValueEventListener(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot snapshot) {
//////                for (DataSnapshot child : snapshot.getChildren()) {
//////                    if (child.child("l").exists()) {
//////                        List<Object> loc = (List<Object>) child.child("l").getValue();
//////                        double lat = Double.parseDouble(loc.get(0).toString());
//////                        double lng = Double.parseDouble(loc.get(1).toString());
//////                        LatLng driverLatLng = new LatLng(lat, lng);
//////
//////                        if (availableDriverMarkers.containsKey(child.getKey())) {
//////                            // Update existing marker position
//////                            availableDriverMarkers.get(child.getKey()).setPosition(driverLatLng);
//////                        } else {
//////                            // Add new marker if not already on map
//////                            Marker driverMarker = mMap.addMarker(new MarkerOptions()
//////                                    .position(driverLatLng)
//////                                    .title("Available Ambulance")
//////                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//////                            availableDriverMarkers.put(child.getKey(), driverMarker);
//////                        }
//////                    } else if (availableDriverMarkers.containsKey(child.getKey())) {
//////                        // Remove marker if driver goes offline
//////                        availableDriverMarkers.get(child.getKey()).remove();
//////                        availableDriverMarkers.remove(child.getKey());
//////                    }
//////                }
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError error) {
//////            }
//////        });
//////    }
////
//////    private void showAllAvailableDrivers() {
//////        List<String> dummyDrivers = Arrays.asList(
//////                "🚑 Driver A\n📞 9999999999\n⭐ Rating: 4.5",
//////                "🚑 Driver B\n📞 8888888888\n⭐ Rating: 4.2",
//////                "🚑 Driver C\n📞 7777777777\n⭐ Rating: 4.8"
//////        );
//////
//////        DatabaseReference driversAvailableRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
//////        driversAvailableRef.addListenerForSingleValueEvent(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot snapshot) {
//////                // Clear old markers
//////                for (Marker marker : availableDriverMarkers.values()) {
//////                    marker.remove();
//////                }
//////                availableDriverMarkers.clear();
//////
//////                // Create list to store driver info for popup
//////                List<String> driverInfoList = new ArrayList<>();
//////
//////                for (DataSnapshot child : snapshot.getChildren()) {
//////                    if (child.child("l").exists()) {
//////                        List<Object> loc = (List<Object>) child.child("l").getValue();
//////                        double lat = Double.parseDouble(loc.get(0).toString());
//////                        double lng = Double.parseDouble(loc.get(1).toString());
//////                        LatLng driverLatLng = new LatLng(lat, lng);
//////
//////                        Marker driverMarker = mMap.addMarker(new MarkerOptions()
//////                                .position(driverLatLng)
//////                                .title("Available Ambulance")
//////                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//////                        availableDriverMarkers.put(child.getKey(), driverMarker);
//////
//////                        // Get driver info for list
//////                        String name = child.child("name").getValue(String.class);
//////                        String phone = child.child("phone").getValue(String.class);
//////                        String type = child.child("ambulanceType").getValue(String.class);
//////                        double rating = 0.0;
//////                        if (child.child("rating").exists()) {
//////                            rating = Double.parseDouble(child.child("rating").getValue().toString());
//////                        }
//////
//////                        driverInfoList.add("🚑 " + (name != null ? name : mDriverName ) +
//////                                "\n📞 " + (phone != null ? phone : mDriverPhone) +
//////
//////                                "\n⭐ Rating: " + rating);
//////                    }
//////                }
//////
//////                Toast.makeText(CustomerMapActivity.this, "Available ambulances shown on map.", Toast.LENGTH_SHORT).show();
//////
//////                // ✅ Show the popup list
//////                if (!driverInfoList.isEmpty()) {
//////                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
//////                    builder.setTitle("Available Drivers");
//////                    builder.setItems(driverInfoList.toArray(new String[0]), (dialog, which) -> {
//////                        String selectedDriver = driverInfoList.get(which);
//////                        Toast.makeText(CustomerMapActivity.this, "Selected:\n" + selectedDriver, Toast.LENGTH_LONG).show();
//////                    });
//////                    builder.setPositiveButton("Close", null);
//////                    builder.show();
//////                } else {
//////                    Toast.makeText(CustomerMapActivity.this, "No available drivers found nearby.", Toast.LENGTH_SHORT).show();
//////                }
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError error) {
//////                Toast.makeText(CustomerMapActivity.this, "Error loading available ambulances.", Toast.LENGTH_SHORT).show();
//////            }
//////        });
//////
//////        // ✅ Real-time location updates
//////        driversAvailableRef.addValueEventListener(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot snapshot) {
//////                for (DataSnapshot child : snapshot.getChildren()) {
//////                    if (child.child("l").exists()) {
//////                        List<Object> loc = (List<Object>) child.child("l").getValue();
//////                        double lat = Double.parseDouble(loc.get(0).toString());
//////                        double lng = Double.parseDouble(loc.get(1).toString());
//////                        LatLng driverLatLng = new LatLng(lat, lng);
//////
//////                        if (availableDriverMarkers.containsKey(child.getKey())) {
//////                            availableDriverMarkers.get(child.getKey()).setPosition(driverLatLng);
//////                        } else {
//////                            Marker driverMarker = mMap.addMarker(new MarkerOptions()
//////                                    .position(driverLatLng)
//////                                    .title("Available Ambulance")
//////                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//////                            availableDriverMarkers.put(child.getKey(), driverMarker);
//////                        }
//////                    } else if (availableDriverMarkers.containsKey(child.getKey())) {
//////                        availableDriverMarkers.get(child.getKey()).remove();
//////                        availableDriverMarkers.remove(child.getKey());
//////                    }
//////                }
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError error) { }
//////        });
//////    }
////private void showAllAvailableDrivers() {
////    DatabaseReference driversAvailableRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
////
////    // Clear old markers
////    for (Marker marker : availableDriverMarkers.values()) {
////        marker.remove();
////    }
////    availableDriverMarkers.clear();
////
////    // One-time load to populate markers + driver info list
////    driversAvailableRef.addListenerForSingleValueEvent(new ValueEventListener() {
////        @Override
////        public void onDataChange(@NonNull DataSnapshot snapshot) {
////            List<String> driverInfoList = new ArrayList<>();
////
////            for (DataSnapshot child : snapshot.getChildren()) {
////                final String driverId = child.getKey();
////                if (driverId == null) continue;
////
////                if (child.child("l").exists()) {
////                    List<Object> loc = (List<Object>) child.child("l").getValue();
////                    double lat = Double.parseDouble(loc.get(0).toString());
////                    double lng = Double.parseDouble(loc.get(1).toString());
////                    LatLng driverLatLng = new LatLng(lat, lng);
////
////                    // Add marker
////                    Marker driverMarker = mMap.addMarker(new MarkerOptions()
////                            .position(driverLatLng)
////                            .title("Available Ambulance")
////                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////                    if (driverMarker != null) {
////                        driverMarker.setTag(driverId);
////                        availableDriverMarkers.put(driverId, driverMarker);
////                    }
////
////                    // Fetch driver meta from Users/Drivers/<driverId>
////                    FirebaseDatabase.getInstance()
////                            .getReference("Users").child("Drivers").child(driverId)
////                            .addListenerForSingleValueEvent(new ValueEventListener() {
////                                @Override
////                                public void onDataChange(@NonNull DataSnapshot snap) {
////                                    String name = snap.child("Name").getValue(String.class);
////                                    String phone = snap.child("Phone").getValue(String.class);
////                                    String vehicle = snap.child("VehicleNumber").getValue(String.class);
////                                    double rating = 0.0;
////                                    if (snap.child("rating").exists()) {
////                                        try {
////                                            int sum = 0; int count = 0;
////                                            for (DataSnapshot r : snap.child("rating").getChildren()) {
////                                                sum += Integer.parseInt(r.getValue().toString());
////                                                count++;
////                                            }
////                                            if (count > 0) rating = (double) sum / count;
////                                        } catch (Exception ignored) {}
////                                    }
////
////                                    String info = "🚑 " + (name != null ? name : "Unknown") +
////                                            "\n📞 " + (phone != null ? phone : "N/A") +
////                                            (vehicle != null ? ("\n🚗 " + vehicle) : "") +
////                                            "\n⭐ Rating: " + String.format("%.1f", rating);
////
////                                    // Add to list safely on main/UI thread
////                                    driverInfoList.add(info);
////                                }
////
////                                @Override public void onCancelled(@NonNull DatabaseError error) {}
////                            });
////                }
////            }
////
////            // Show dialog AFTER a short delay to allow driver meta fetches to add info
////            // (If you want immediate show, you can skip waiting; this tries to let driver info populate)
////            new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
////                if (!driverInfoList.isEmpty()) {
////                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
////                    builder.setTitle("Available Drivers");
////                    builder.setItems(driverInfoList.toArray(new String[0]), (dialog, which) -> {
////                        String selectedDriver = driverInfoList.get(which);
////                        Toast.makeText(CustomerMapActivity.this, "Selected:\n" + selectedDriver, Toast.LENGTH_LONG).show();
////                    });
////                    builder.setPositiveButton("Close", null);
////                    builder.show();
////                } else {
////                    Toast.makeText(CustomerMapActivity.this, "No available drivers found nearby.", Toast.LENGTH_SHORT).show();
////                }
////            }, 600); // 600ms — small delay to allow the single-value child lookups to finish
////        }
////
////        @Override
////        public void onCancelled(@NonNull DatabaseError error) {
////            Toast.makeText(CustomerMapActivity.this, "Error loading available ambulances.", Toast.LENGTH_SHORT).show();
////        }
////    });
////
////    // Real-time updates: keep a listener to update markers position / add/remove
////    driversAvailableRef.addValueEventListener(new ValueEventListener() {
////        @Override
////        public void onDataChange(@NonNull DataSnapshot snapshot) {
////            for (DataSnapshot child : snapshot.getChildren()) {
////                String id = child.getKey();
////                if (id == null) continue;
////
////                if (child.child("l").exists()) {
////                    List<Object> loc = (List<Object>) child.child("l").getValue();
////                    double lat = Double.parseDouble(loc.get(0).toString());
////                    double lng = Double.parseDouble(loc.get(1).toString());
////                    LatLng driverLatLng = new LatLng(lat, lng);
////
////                    if (availableDriverMarkers.containsKey(id)) {
////                        availableDriverMarkers.get(id).setPosition(driverLatLng);
////                    } else {
////                        Marker driverMarker = mMap.addMarker(new MarkerOptions()
////                                .position(driverLatLng)
////                                .title("Available Ambulance")
////                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////                        if (driverMarker != null) {
////                            driverMarker.setTag(id);
////                            availableDriverMarkers.put(id, driverMarker);
////                        }
////                    }
////                } else {
////                    // driver went offline — remove marker
////                    if (availableDriverMarkers.containsKey(id)) {
////                        availableDriverMarkers.get(id).remove();
////                        availableDriverMarkers.remove(id);
////                    }
////                }
////            }
////        }
////
////        @Override public void onCancelled(@NonNull DatabaseError error) {}
////    });
////}
////
////    // ---------------- EXISTING FUNCTIONS BELOW ----------------
////
////    private void getClosestDriver() {
////        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
////        GeoFire geoFire = new GeoFire(driverLocation);
////        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
////        geoQuery.removeAllListeners();
////        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
////            @Override
////            public void onKeyEntered(String key, GeoLocation location) {
////                if (!driverFound && requestBol) {
////                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
////                            .child("Users").child("Drivers").child(key);
////                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                            if (dataSnapshot.exists()) {
////                                driverFound = true;
////                                driverFoundID = dataSnapshot.getKey();
////
////                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
////                                        .child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
////                                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
////                                HashMap map = new HashMap();
////                                map.put("customerRideId", customerId);
////                                map.put("destination", destination);
////                                if (destinationLatlng != null) {
////                                    map.put("destinationLat", destinationLatlng.latitude);
////                                    map.put("destinationLng", destinationLatlng.longitude);
////                                }
////                                driverRef.updateChildren(map);
////
////                                getDriverLocation();
////                                getDriverInfo();
////                                getHasRideEnded();
////                                mRequest.setText("Looking for Driver's Location...");
////                            }
////                        }
////
////                        @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
////                    });
////                }
////            }
////
////            @Override public void onKeyExited(String key) {}
////            @Override public void onKeyMoved(String key, GeoLocation location) {}
////            @Override public void onGeoQueryReady() { if (!driverFound) { radius++; getClosestDriver(); } }
////            @Override public void onGeoQueryError(DatabaseError error) {}
////        });
////    }
////
////    private void getDriverLocation() {
////        driverLocationRef = FirebaseDatabase.getInstance().getReference()
////                .child("driversWorking").child(driverFoundID).child("l");
////        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists() && requestBol) {
////                    List<Object> map = (List<Object>) dataSnapshot.getValue();
////                    double LocationLat = 0;
////                    double LocationLng = 0;
////                    if (map.get(0) != null) LocationLat = Double.parseDouble(map.get(0).toString());
////                    if (map.get(1) != null) LocationLng = Double.parseDouble(map.get(1).toString());
////
////                    LatLng driverLatLng = new LatLng(LocationLat, LocationLng);
////                    if (mDriverMarker != null) mDriverMarker.remove();
////
////                    Location loc1 = new Location("");
////                    loc1.setLatitude(pickupLocation.latitude);
////                    loc1.setLongitude(pickupLocation.longitude);
////
////                    Location loc2 = new Location("");
////                    loc2.setLatitude(driverLatLng.latitude);
////                    loc2.setLongitude(driverLatLng.longitude);
////
////                    float distance = loc1.distanceTo(loc2);
////                    if (distance < 100) {
////                        mRequest.setText("Ambulance Arrived");
////                    } else {
////                        int dis = (int) distance / 1000;
////                        mRequest.setText("Ambulance Found: " + dis + " Kms away...");
////                    }
////
////                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng)
////                            .title("Your Ambulance")
////                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////                }
////            }
////
////            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
////        });
////    }
////
////    private void getDriverInfo() {
////        mDriverInfo.setVisibility(View.VISIBLE);
////        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
////                .child("Users").child("Drivers").child(driverFoundID);
////        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists()) {
////                    if (dataSnapshot.child("Name") != null)
////                        mDriverName.setText(dataSnapshot.child("Name").getValue().toString());
////                    if (dataSnapshot.child("Phone") != null)
////                        mDriverPhone.setText(dataSnapshot.child("Phone").getValue().toString());
////                    if (dataSnapshot.child("Car") != null)
////                        mDriverCar.setText(dataSnapshot.child("Car").getValue().toString());
////
////                    int ratingSum = 0;
////                    float ratingsTotal = 0;
////                    float ratingsAvg = 0;
////                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
////                        ratingSum += Integer.parseInt(child.getValue().toString());
////                        ratingsTotal++;
////                    }
////                    if (ratingsTotal != 0) {
////                        ratingsAvg = ratingSum / ratingsTotal;
////                        mRatingBar.setRating(ratingsAvg);
////                    }
////                }
////            }
////
////            @Override public void onCancelled(DatabaseError databaseError) {}
////        });
////    }
////
////    private void getHasRideEnded() {
////        driveHasEndedRef = FirebaseDatabase.getInstance().getReference()
////                .child("Users").child("Drivers").child(driverFoundID)
////                .child("customerRequest").child("customerRideId");
////        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                if (!dataSnapshot.exists()) {
////                    endRide();
////                }
////            }
////
////            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
////        });
////    }
////
////    private void endRide() {
////        requestBol = false;
////        if (geoQuery != null) geoQuery.removeAllListeners();
////        if (driverLocationRefListener != null && driveHasEndedRefListener != null) {
////            driverLocationRef.removeEventListener(driverLocationRefListener);
////            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
////        }
////
////        if (driverFoundID != null) {
////            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
////                    .child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
////            driverRef.removeValue();
////            driverFoundID = null;
////        }
////
////        driverFound = false;
////        radius = 1;
////
////        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
////        new GeoFire(FirebaseDatabase.getInstance().getReference("customerRequest")).removeLocation(userId);
////
////        if (pickupMarker != null) pickupMarker.remove();
////        if (mDriverMarker != null) mDriverMarker.remove();
////
////        for (Marker marker : availableDriverMarkers.values()) marker.remove();
////        availableDriverMarkers.clear();
////
////        mRequest.setText("Request An Ambulance");
////        mDriverInfo.setVisibility(View.GONE);
////        mDriverName.setText("");
////        mDriverPhone.setText("");
////        mDriverCar.setText("");
////    }
////
////    @Override
////    public void onMapReady(GoogleMap googleMap) {
////        mMap = googleMap;
////        mLocationRequest = new LocationRequest();
////        mLocationRequest.setInterval(1000);
////        mLocationRequest.setFastestInterval(1000);
////        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
////
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
////                == PackageManager.PERMISSION_GRANTED) {
////            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
////            mMap.setMyLocationEnabled(true);
////        } else {
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
////        }
////        mMap.setOnMarkerClickListener(marker -> {
////            Object tag = marker.getTag();
////            if (tag != null && tag instanceof String) {
////                String driverId = (String) tag;
////
////                // Fetch driver details
////                FirebaseDatabase.getInstance()
////                        .getReference("Users/Drivers").child(driverId)
////                        .addListenerForSingleValueEvent(new ValueEventListener() {
////                            @Override
////                            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                if (snapshot.exists()) {
////                                    String name = snapshot.child("Name").getValue(String.class);
////                                    String vehicle = snapshot.child("VehicleNumber").getValue(String.class);
////                                    selectedDriverPhone = snapshot.child("Phone").getValue(String.class);
////
////                                    cardDriverName.setText(name);
////                                    cardDriverVehicle.setText("Vehicle: " + vehicle);
////                                    cardDriverPhone.setText("Phone: " + selectedDriverPhone);
////
////                                    driverInfoCard.setVisibility(View.VISIBLE);
////                                }
////                            }
////
////                            @Override
////                            public void onCancelled(@NonNull DatabaseError error) {}
////                        });
////            }
////            return false;
////        });
////
////    }
////
////    LocationCallback locationCallback = new LocationCallback() {
////        @Override
////        public void onLocationResult(LocationResult locationResult) {
////            for (Location location : locationResult.getLocations()) {
////                mLastLocation = location;
////                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
////                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
////            }
////        }
////    };
////}
////WORKING CODE 4
//// CustomerMapActivity.java
//package saksham.medrescue.saksham;
//
////import sachdeva.saksham.medrescue.R;
////
////import android.Manifest;
////import android.content.Intent;
////import android.content.pm.PackageManager;
////import android.location.Location;
////import android.net.Uri;
////import android.os.Bundle;
////import android.os.Handler;
////import android.os.Looper;
////import android.view.Gravity;
////import android.view.View;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.LinearLayout;
////import android.widget.RatingBar;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AlertDialog;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.core.app.ActivityCompat;
////import androidx.core.content.ContextCompat;
////
////import com.firebase.geofire.GeoFire;
////import com.firebase.geofire.GeoLocation;
////import com.firebase.geofire.GeoQuery;
////import com.firebase.geofire.GeoQueryEventListener;
////import com.google.android.gms.common.api.Status;
////import com.google.android.gms.location.FusedLocationProviderClient;
////import com.google.android.gms.location.LocationCallback;
////import com.google.android.gms.location.LocationRequest;
////import com.google.android.gms.location.LocationResult;
////import com.google.android.gms.location.LocationServices;
////import com.google.android.gms.maps.CameraUpdateFactory;
////import com.google.android.gms.maps.GoogleMap;
////import com.google.android.gms.maps.OnMapReadyCallback;
////import com.google.android.gms.maps.SupportMapFragment;
////import com.google.android.gms.maps.model.BitmapDescriptorFactory;
////import com.google.android.gms.maps.model.LatLng;
////import com.google.android.gms.maps.model.Marker;
////import com.google.android.gms.maps.model.MarkerOptions;
////import com.google.android.libraries.places.api.Places;
////import com.google.android.libraries.places.api.model.Place;
////import com.google.android.libraries.places.api.model.TypeFilter;
////import com.google.android.libraries.places.widget.Autocomplete;
////import com.google.android.libraries.places.widget.AutocompleteActivity;
////import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////
////import java.util.ArrayList;
////import java.util.Arrays;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////
////public class CustomerMapActivity extends AppCompatActivity implements OnMapReadyCallback {
////
////    private GoogleMap mMap;
////    private LinearLayout driverInfoCard;
////    private TextView cardDriverName, cardDriverVehicle, cardDriverPhone;
////    private Button callDriverBtn;
////    private String selectedDriverPhone = "";
////
////    private FusedLocationProviderClient mFusedLocationClient;
////    private Location mLastLocation;
////    private LocationRequest mLocationRequest;
////
////    private Button mLogout, mRequest, mSettings, mHistory;
////    private LinearLayout mDriverInfo;
////    private TextView mDriverName, mDriverPhone, mDriverCar;
////    private RatingBar mRatingBar;
////
////    private EditText mFromLocation, mToLocation;
////
////    private LatLng pickupLocation;
////    private Boolean requestBol = false;
////    private Marker pickupMarker;
////    private Marker mDriverMarker;
////
////    private String destination;
////    private LatLng destinationLatlng;
////
////    private int radius = 1;
////    private Boolean driverFound = false;
////    private String driverFoundID;
////
////    private GeoQuery geoQuery;
////    private DatabaseReference driverLocationRef, driveHasEndedRef;
////    private ValueEventListener driverLocationRefListener, driveHasEndedRefListener;
////
////    // NEW: for driversAvailable realtime listener management
////    private DatabaseReference driversAvailableRef;
////    private ValueEventListener driversAvailableRefListener;
////
////    private static final int AUTOCOMPLETE_FROM_REQUEST_CODE = 1001;
////    private static final int AUTOCOMPLETE_TO_REQUEST_CODE = 1002;
////
////    // To store multiple available driver markers
////    private HashMap<String, Marker> availableDriverMarkers = new HashMap<>();
////
////    // Optional: show dummy drivers only once per request
////    private boolean dummyShownForCurrentRequest = false;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_customer_map);
////        driverInfoCard = findViewById(R.id.driver_info_card);
////        cardDriverName = findViewById(R.id.card_driver_name);
////        cardDriverVehicle = findViewById(R.id.card_driver_vehicle);
////        cardDriverPhone = findViewById(R.id.card_driver_phone);
////        callDriverBtn = findViewById(R.id.btn_call_driver);
////
////        if (!Places.isInitialized()) {
////            // TODO: Move API key out of source code in production
////            Places.initialize(getApplicationContext(), "AIzaSyCPwOM_929IDlkLCOs3N7u2OsWVISeCuKU");
////        }
////
////        mLogout = findViewById(R.id.logout);
////        mRequest = findViewById(R.id.request);
////        mSettings = findViewById(R.id.settings);
////        mHistory = findViewById(R.id.history);
////        mDriverInfo = findViewById(R.id.driverInfo);
////        mDriverName = findViewById(R.id.driverName);
////        mDriverPhone = findViewById(R.id.driverPhone);
////        mDriverCar = findViewById(R.id.driverCar);
////        mRatingBar = findViewById(R.id.ratingBar);
////
////        mFromLocation = findViewById(R.id.fromLocation);
////        mToLocation = findViewById(R.id.toLocation);
////
////        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
////
////        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
////                .findFragmentById(R.id.map);
////        if (mapFragment != null) mapFragment.getMapAsync(this);
////
////        mFromLocation.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_FROM_REQUEST_CODE));
////        mToLocation.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_TO_REQUEST_CODE));
////
////        mLogout.setOnClickListener(v -> {
////            FirebaseAuth.getInstance().signOut();
////            startActivity(new Intent(CustomerMapActivity.this, Welcome_Activity.class));
////            finish();
////        });
////
////        mRequest.setOnClickListener(v -> {
////            if (requestBol) {
////                endRide();
////            } else {
////                if (mLastLocation == null) {
////                    Toast.makeText(this, "Please wait for location...", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////
////                requestBol = true;
////                dummyShownForCurrentRequest = false; // reset for this new request
////                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
////                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
////                new GeoFire(ref).setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
////
////                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
////                pickupMarker = mMap.addMarker(new MarkerOptions()
////                        .position(pickupLocation)
////                        .title("Pickup Here")
////                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));
////
////                // ✅ NEW: Show dummy immediate drivers and then real ones
////                showAllAvailableDrivers();
////
////                mRequest.setText("Getting Your Ambulance...");
////                getClosestDriver();
////            }
////        });
////
////        mSettings.setOnClickListener(v -> startActivity(new Intent(CustomerMapActivity.this, CustomerSettingsActivity.class)));
////        mHistory.setOnClickListener(v -> {
////            Intent intent = new Intent(CustomerMapActivity.this, HistoryActivity.class);
////            intent.putExtra("customerOrDriver", "Customers");
////            startActivity(intent);
////        });
////
////        // Wire call driver button safely (ACTION_DIAL)
////        callDriverBtn.setOnClickListener(v -> {
////            if (selectedDriverPhone == null || selectedDriverPhone.trim().isEmpty()) {
////                Toast.makeText(CustomerMapActivity.this, "Phone number not available", Toast.LENGTH_SHORT).show();
////                return;
////            }
////            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
////            dialIntent.setData(Uri.parse("tel:" + selectedDriverPhone));
////            startActivity(dialIntent);
////        });
////    }
////
////    private void openAutocomplete(int requestCode) {
////        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
////        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
////                .setTypeFilter(TypeFilter.ADDRESS)
////                .setCountry("IN")
////                .build(this);
////        startActivityForResult(intent, requestCode);
////    }
////
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE || requestCode == AUTOCOMPLETE_TO_REQUEST_CODE) {
////            if (resultCode == RESULT_OK && data != null) {
////                Place place = Autocomplete.getPlaceFromIntent(data);
////                if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE) {
////                    mFromLocation.setText(place.getAddress());
////                } else {
////                    mToLocation.setText(place.getAddress());
////                    destination = place.getName();
////                    destinationLatlng = place.getLatLng();
////                }
////                if (place.getLatLng() != null && mMap != null) {
////                    mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
////                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
////                }
////            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
////                Status status = Autocomplete.getStatusFromIntent(data);
////                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
////            }
////        }
////    }
////
////    // ----------------- Driver listing & dummy + realtime updates -----------------
////
////    private void showAllAvailableDrivers() {
////        // 1) Show immediate dummy markers once per request
////        if (!dummyShownForCurrentRequest) {
////            showInitialDummyDrivers();
////            dummyShownForCurrentRequest = true;
////        }
////
////        // 2) Load real drivers from Firebase and keep a realtime listener
////        driversAvailableRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
////
////        // One-time initial load
////        driversAvailableRef.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                // Remove dummy markers if real markers will replace them (optionally)
////                List<String> toRemoveDummyKeys = new ArrayList<>();
////                for (String key : new ArrayList<>(availableDriverMarkers.keySet())) {
////                    Marker mk = availableDriverMarkers.get(key);
////                    Object tag = mk != null ? mk.getTag() : null;
////                    if (tag != null && tag.toString().startsWith("DUMMY_")) {
////                        toRemoveDummyKeys.add(key);
////                    }
////                }
////                for (String k : toRemoveDummyKeys) {
////                    Marker m = availableDriverMarkers.remove(k);
////                    if (m != null) m.remove();
////                }
////
////                final List<String> driverInfoList = new ArrayList<>();
////
////                for (DataSnapshot child : snapshot.getChildren()) {
////                    final String driverId = child.getKey();
////                    if (driverId == null) continue;
////
////                    if (child.child("l").exists()) {
////                        List<Object> loc = (List<Object>) child.child("l").getValue();
////                        double lat = Double.parseDouble(loc.get(0).toString());
////                        double lng = Double.parseDouble(loc.get(1).toString());
////                        LatLng driverLatLng = new LatLng(lat, lng);
////
////                        // Add or update marker
////                        if (availableDriverMarkers.containsKey(driverId)) {
////                            availableDriverMarkers.get(driverId).setPosition(driverLatLng);
////                        } else {
////                            Marker driverMarker = mMap.addMarker(new MarkerOptions()
////                                    .position(driverLatLng)
////                                    .title("Available Ambulance")
////                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////                            if (driverMarker != null) {
////                                driverMarker.setTag(driverId);
////                                availableDriverMarkers.put(driverId, driverMarker);
////                            }
////                        }
////
////                        // Fetch driver meta from Users/Drivers/<driverId>
////                        FirebaseDatabase.getInstance()
////                                .getReference("Users").child("Drivers").child(driverId)
////                                .addListenerForSingleValueEvent(new ValueEventListener() {
////                                    @Override
////                                    public void onDataChange(@NonNull DataSnapshot snap) {
////                                        String name = snap.child("Name").getValue(String.class);
////                                        String phone = snap.child("Phone").getValue(String.class);
////                                        String vehicle = snap.child("VehicleNumber").getValue(String.class);
////                                        double rating = 0.0;
////                                        if (snap.child("rating").exists()) {
////                                            try {
////                                                int sum = 0, count = 0;
////                                                for (DataSnapshot r : snap.child("rating").getChildren()) {
////                                                    sum += Integer.parseInt(r.getValue().toString());
////                                                    count++;
////                                                }
////                                                if (count > 0) rating = (double) sum / count;
////                                            } catch (Exception ignored) {}
////                                        }
////                                        String info = "🚑 " + (name != null ? name : "Unknown")
////                                                + "\n📞 " + (phone != null ? phone : "N/A")
////                                                + (vehicle != null ? ("\n🚗 " + vehicle) : "")
////                                                + "\n⭐ Rating: " + String.format("%.1f", rating);
////                                        synchronized (driverInfoList) {
////                                            driverInfoList.add(info);
////                                        }
////                                    }
////
////                                    @Override public void onCancelled(@NonNull DatabaseError error) {}
////                                });
////                    } else {
////                        // driver offline: remove marker if present
////                        if (availableDriverMarkers.containsKey(driverId)) {
////                            availableDriverMarkers.get(driverId).remove();
////                            availableDriverMarkers.remove(driverId);
////                        }
////                    }
////                }
////
////                // Show popup list after a short delay to allow meta fetches to populate
////                new Handler(Looper.getMainLooper()).postDelayed(() -> {
////                    if (!driverInfoList.isEmpty()) {
////                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
////                        builder.setTitle("Available Drivers");
////                        builder.setItems(driverInfoList.toArray(new String[0]), (dialog, which) -> {
////                            String selectedDriver = driverInfoList.get(which);
////                            Toast.makeText(CustomerMapActivity.this, "Selected:\n" + selectedDriver, Toast.LENGTH_LONG).show();
////                        });
////                        builder.setPositiveButton("Close", null);
////                        builder.show();
////                    } else {
////                        // If empty, we keep dummy markers visible for responsiveness
////                    }
////                }, 600);
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////                Toast.makeText(CustomerMapActivity.this, "Error loading available ambulances.", Toast.LENGTH_SHORT).show();
////            }
////        });
////
////        // Realtime updates: keep a listener to update/add/remove markers
////        if (driversAvailableRefListener == null) {
////            driversAvailableRefListener = new ValueEventListener() {
////                @Override
////                public void onDataChange(@NonNull DataSnapshot snapshot) {
////                    for (DataSnapshot child : snapshot.getChildren()) {
////                        String id = child.getKey();
////                        if (id == null) continue;
////
////                        if (child.child("l").exists()) {
////                            List<Object> loc = (List<Object>) child.child("l").getValue();
////                            double lat = Double.parseDouble(loc.get(0).toString());
////                            double lng = Double.parseDouble(loc.get(1).toString());
////                            LatLng driverLatLng = new LatLng(lat, lng);
////
////                            if (availableDriverMarkers.containsKey(id)) {
////                                availableDriverMarkers.get(id).setPosition(driverLatLng);
////                                availableDriverMarkers.get(id).setTag(id); // ensure tag is real id
////                            } else {
////                                Marker driverMarker = mMap.addMarker(new MarkerOptions()
////                                        .position(driverLatLng)
////                                        .title("Available Ambulance")
////                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////                                if (driverMarker != null) {
////                                    driverMarker.setTag(id);
////                                    availableDriverMarkers.put(id, driverMarker);
////                                }
////                            }
////                        } else {
////                            if (availableDriverMarkers.containsKey(id)) {
////                                availableDriverMarkers.get(id).remove();
////                                availableDriverMarkers.remove(id);
////                            }
////                        }
////                    }
////                }
////
////                @Override public void onCancelled(@NonNull DatabaseError error) {}
////            };
////            driversAvailableRef.addValueEventListener(driversAvailableRefListener);
////        }
////    }
////
////    private void showInitialDummyDrivers() {
////        // Remove existing dummy markers first
////        List<String> toRemove = new ArrayList<>();
////        for (String key : new ArrayList<>(availableDriverMarkers.keySet())) {
////            if (key.startsWith("DUMMY_")) {
////                Marker m = availableDriverMarkers.get(key);
////                if (m != null) m.remove();
////                toRemove.add(key);
////            }
////        }
////        for (String k : toRemove) availableDriverMarkers.remove(k);
////
////        LatLng base = pickupLocation != null ? pickupLocation : (mLastLocation != null ? new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) : new LatLng(28.6139, 77.2090)); // default New Delhi
////
////        LatLng d1 = new LatLng(base.latitude + 0.004, base.longitude + 0.003);
////        LatLng d2 = new LatLng(base.latitude - 0.0035, base.longitude + 0.002);
////        LatLng d3 = new LatLng(base.latitude + 0.0025, base.longitude - 0.0035);
////
////        Marker m1 = mMap.addMarker(new MarkerOptions().position(d1).title("Available Ambulance").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////        if (m1 != null) { m1.setTag("DUMMY_1"); availableDriverMarkers.put("DUMMY_1", m1); }
////
////        Marker m2 = mMap.addMarker(new MarkerOptions().position(d2).title("Available Ambulance").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////        if (m2 != null) { m2.setTag("DUMMY_2"); availableDriverMarkers.put("DUMMY_2", m2); }
////
////        Marker m3 = mMap.addMarker(new MarkerOptions().position(d3).title("Available Ambulance").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////        if (m3 != null) { m3.setTag("DUMMY_3"); availableDriverMarkers.put("DUMMY_3", m3); }
////
////        List<String> dumInfo = Arrays.asList(
////                "🚑 Driver A\n📞 9999999999\n⭐ 4.5",
////                "🚑 Driver B\n📞 8888888888\n⭐ 4.2",
////                "🚑 Driver C\n📞 7777777777\n⭐ 4.8"
////        );
////        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
////        builder.setTitle("Available Drivers (nearby)");
////        builder.setItems(dumInfo.toArray(new String[0]), (dialog, which) -> {
////            String sel = dumInfo.get(which);
////            Toast.makeText(CustomerMapActivity.this, "Selected:\n" + sel, Toast.LENGTH_LONG).show();
////        });
////        builder.setPositiveButton("Close", null);
////        builder.show();
////    }
////
////    // ---------------- EXISTING FUNCTIONS BELOW (refactored & safer) ----------------
////
////    private void getClosestDriver() {
////        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
////        GeoFire geoFire = new GeoFire(driverLocation);
////        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
////        geoQuery.removeAllListeners();
////        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
////            @Override
////            public void onKeyEntered(String key, GeoLocation location) {
////                if (!driverFound && requestBol) {
////                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
////                            .child("Users").child("Drivers").child(key);
////                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                            if (dataSnapshot.exists()) {
////                                driverFound = true;
////                                driverFoundID = dataSnapshot.getKey();
////
////                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
////                                        .child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
////                                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
////                                HashMap map = new HashMap();
////                                map.put("customerRideId", customerId);
////                                map.put("destination", destination);
////                                if (destinationLatlng != null) {
////                                    map.put("destinationLat", destinationLatlng.latitude);
////                                    map.put("destinationLng", destinationLatlng.longitude);
////                                }
////                                driverRef.updateChildren(map);
////
////                                getDriverLocation();
////                                getDriverInfo();
////                                getHasRideEnded();
////                                mRequest.setText("Looking for Driver's Location...");
////                            }
////                        }
////
////                        @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
////                    });
////                }
////            }
////
////            @Override public void onKeyExited(String key) {}
////            @Override public void onKeyMoved(String key, GeoLocation location) {}
////            @Override public void onGeoQueryReady() { if (!driverFound) {
////                // add a reasonable maximum radius to avoid infinite expansion
////                if (radius < 50) {
////                    radius++;
////                    getClosestDriver();
////                } else {
////                    Toast.makeText(CustomerMapActivity.this, "No drivers found within 50 km.", Toast.LENGTH_SHORT).show();
////                    // let user cancel request manually or try again
////                }
////            } }
////            @Override public void onGeoQueryError(DatabaseError error) {}
////        });
////    }
////
////    private void getDriverLocation() {
////        driverLocationRef = FirebaseDatabase.getInstance().getReference()
////                .child("driversWorking").child(driverFoundID).child("l");
////        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists() && requestBol) {
////                    List<Object> map = (List<Object>) dataSnapshot.getValue();
////                    double LocationLat = 0;
////                    double LocationLng = 0;
////                    if (map.get(0) != null) LocationLat = Double.parseDouble(map.get(0).toString());
////                    if (map.get(1) != null) LocationLng = Double.parseDouble(map.get(1).toString());
////
////                    LatLng driverLatLng = new LatLng(LocationLat, LocationLng);
////                    if (mDriverMarker != null) mDriverMarker.remove();
////
////                    Location loc1 = new Location("");
////                    loc1.setLatitude(pickupLocation.latitude);
////                    loc1.setLongitude(pickupLocation.longitude);
////
////                    Location loc2 = new Location("");
////                    loc2.setLatitude(driverLatLng.latitude);
////                    loc2.setLongitude(driverLatLng.longitude);
////
////                    float distance = loc1.distanceTo(loc2);
////                    if (distance < 100) {
////                        mRequest.setText("Ambulance Arrived");
////                    } else {
////                        int dis = (int) distance / 1000;
////                        mRequest.setText("Ambulance Found: " + dis + " Kms away...");
////                    }
////
////                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng)
////                            .title("Your Ambulance")
////                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////                }
////            }
////
////            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
////        });
////    }
////
////    private void getDriverInfo() {
////        mDriverInfo.setVisibility(View.VISIBLE);
////        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
////                .child("Users").child("Drivers").child(driverFoundID);
////        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists()) {
////                    if (dataSnapshot.child("Name").getValue() != null)
////                        mDriverName.setText(dataSnapshot.child("Name").getValue().toString());
////                    if (dataSnapshot.child("Phone").getValue() != null) {
////                        mDriverPhone.setText(dataSnapshot.child("Phone").getValue().toString());
////                        selectedDriverPhone = dataSnapshot.child("Phone").getValue().toString();
////                    }
////                    if (dataSnapshot.child("Car").getValue() != null)
////                        mDriverCar.setText(dataSnapshot.child("Car").getValue().toString());
////
////                    // Safe rating calculation
////                    if (dataSnapshot.child("rating").exists()) {
////                        int ratingSum = 0;
////                        float ratingsCount = 0f;
////                        for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
////                            try {
////                                ratingSum += Integer.parseInt(child.getValue().toString());
////                                ratingsCount++;
////                            } catch (NumberFormatException e) {
////                                // ignore malformed rating entries
////                            }
////                        }
////                        if (ratingsCount > 0f) {
////                            float ratingsAvg = (float) ratingSum / ratingsCount;
////                            mRatingBar.setRating(ratingsAvg);
////                        } else {
////                            mRatingBar.setRating(0f);
////                        }
////                    } else {
////                        mRatingBar.setRating(0f);
////                    }
////                }
////            }
////
////            @Override public void onCancelled(DatabaseError databaseError) {}
////        });
////    }
////
////    private void getHasRideEnded() {
////        driveHasEndedRef = FirebaseDatabase.getInstance().getReference()
////                .child("Users").child("Drivers").child(driverFoundID)
////                .child("customerRequest").child("customerRideId");
////        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                if (!dataSnapshot.exists()) {
////                    // Ride ended — prompt rating for the driverFoundID (if present)
////                    if (driverFoundID != null) {
////                        promptForRating(driverFoundID);
////                    } else {
////                        endRide();
////                    }
////                }
////            }
////
////            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
////        });
////    }
////
////    private void promptForRating(String driverId) {
////        if (driverId == null) {
////            endRide();
////            return;
////        }
////
////        AlertDialog.Builder builder = new AlertDialog.Builder(this);
////        builder.setTitle("Rate your Ambulance Service");
////
////        final RatingBar ratingBar = new RatingBar(this);
////        ratingBar.setNumStars(5);
////        ratingBar.setStepSize(0.5f);
////        ratingBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
////
////        LinearLayout container = new LinearLayout(this);
////        container.setPadding(50, 20, 50, 20);
////        container.setGravity(Gravity.CENTER);
////        container.addView(ratingBar);
////
////        builder.setView(container);
////
////        builder.setPositiveButton("Submit", (dialog, which) -> {
////            float rating = ratingBar.getRating();
////            int intRating = Math.max(1, Math.round(rating)); // store as integer (1..5)
////            saveDriverRating(driverId, intRating);
////            endRide();
////        });
////
////        builder.setNegativeButton("Skip", (dialog, which) -> {
////            endRide();
////        });
////
////        builder.setCancelable(false);
////        builder.show();
////    }
////
////    private void saveDriverRating(String driverId, int rating) {
////        if (driverId == null) return;
////
////        DatabaseReference ratingRef = FirebaseDatabase.getInstance()
////                .getReference().child("Users").child("Drivers").child(driverId).child("rating");
////
////        ratingRef.push().setValue(rating).addOnCompleteListener(task -> {
////            if (task.isSuccessful()) {
////                Toast.makeText(CustomerMapActivity.this, "Thanks for rating!", Toast.LENGTH_SHORT).show();
////            } else {
////                Toast.makeText(CustomerMapActivity.this, "Couldn't save rating — try again later.", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////
////    private void endRide() {
////        requestBol = false;
////        if (geoQuery != null) geoQuery.removeAllListeners();
////
////        if (driverLocationRefListener != null && driverLocationRef != null) {
////            driverLocationRef.removeEventListener(driverLocationRefListener);
////            driverLocationRefListener = null;
////        }
////        if (driveHasEndedRefListener != null && driveHasEndedRef != null) {
////            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
////            driveHasEndedRefListener = null;
////        }
////
////        if (driverFoundID != null) {
////            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
////                    .child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
////            driverRef.removeValue();
////            driverFoundID = null;
////        }
////
////        driverFound = false;
////        radius = 1;
////
////        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
////        new GeoFire(FirebaseDatabase.getInstance().getReference("customerRequest")).removeLocation(userId);
////
////        if (pickupMarker != null) { pickupMarker.remove(); pickupMarker = null; }
////        if (mDriverMarker != null) { mDriverMarker.remove(); mDriverMarker = null; }
////
////        for (Marker marker : availableDriverMarkers.values()) {
////            if (marker != null) marker.remove();
////        }
////        availableDriverMarkers.clear();
////
////        // remove driversAvailable listener if present
////        if (driversAvailableRef != null && driversAvailableRefListener != null) {
////            driversAvailableRef.removeEventListener(driversAvailableRefListener);
////            driversAvailableRefListener = null;
////            driversAvailableRef = null;
////        }
////
////        mRequest.setText("Request An Ambulance");
////        mDriverInfo.setVisibility(View.GONE);
////        mDriverName.setText("");
////        mDriverPhone.setText("");
////        mDriverCar.setText("");
////        selectedDriverPhone = "";
////    }
////
////    @Override
////    public void onMapReady(GoogleMap googleMap) {
////        mMap = googleMap;
////        mLocationRequest = new LocationRequest();
////        mLocationRequest.setInterval(1000);
////        mLocationRequest.setFastestInterval(1000);
////        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
////
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
////                == PackageManager.PERMISSION_GRANTED) {
////            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
////            mMap.setMyLocationEnabled(true);
////        } else {
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
////        }
////        mMap.setOnMarkerClickListener(marker -> {
////            Object tag = marker.getTag();
////            if (tag != null && tag instanceof String) {
////                String driverId = (String) tag;
////
////                // Fetch driver details
////                FirebaseDatabase.getInstance()
////                        .getReference("Users/Drivers").child(driverId)
////                        .addListenerForSingleValueEvent(new ValueEventListener() {
////                            @Override
////                            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                if (snapshot.exists()) {
////                                    String name = snapshot.child("Name").getValue(String.class);
////                                    String vehicle = snapshot.child("VehicleNumber").getValue(String.class);
////                                    selectedDriverPhone = snapshot.child("Phone").getValue(String.class);
////
////                                    cardDriverName.setText(name != null ? name : "Driver");
////                                    cardDriverVehicle.setText("Vehicle: " + (vehicle != null ? vehicle : "N/A"));
////                                    cardDriverPhone.setText("Phone: " + (selectedDriverPhone != null ? selectedDriverPhone : "N/A"));
////
////                                    driverInfoCard.setVisibility(View.VISIBLE);
////                                }
////                            }
////
////                            @Override
////                            public void onCancelled(@NonNull DatabaseError error) {}
////                        });
////            }
////            return false;
////        });
////
////    }
////
////    LocationCallback locationCallback = new LocationCallback() {
////        @Override
////        public void onLocationResult(LocationResult locationResult) {
////            for (Location location : locationResult.getLocations()) {
////                mLastLocation = location;
////                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
////                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
////            }
////        }
////    };
////
////    @Override
////    protected void onDestroy() {
////        super.onDestroy();
////        // remove any Firebase listeners that may still be attached
////        if (driverLocationRefListener != null && driverLocationRef != null) {
////            driverLocationRef.removeEventListener(driverLocationRefListener);
////            driverLocationRefListener = null;
////        }
////        if (driveHasEndedRefListener != null && driveHasEndedRef != null) {
////            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
////            driveHasEndedRefListener = null;
////        }
////        if (driversAvailableRef != null && driversAvailableRefListener != null) {
////            driversAvailableRef.removeEventListener(driversAvailableRefListener);
////            driversAvailableRefListener = null;
////            driversAvailableRef = null;
////        }
////        if (mFusedLocationClient != null && locationCallback != null) {
////            mFusedLocationClient.removeLocationUpdates(locationCallback);
////        }
////    }
////}
//// CustomerMapActivity.java
//
//
//import sachdeva.saksham.medrescue.R;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.firebase.geofire.GeoFire;
//import com.firebase.geofire.GeoLocation;
//import com.firebase.geofire.GeoQuery;
//import com.firebase.geofire.GeoQueryEventListener;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.model.TypeFilter;
//import com.google.android.libraries.places.widget.Autocomplete;
//import com.google.android.libraries.places.widget.AutocompleteActivity;
//import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//public class CustomerMapActivity extends AppCompatActivity implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//    private LinearLayout driverInfoCard;
//    private TextView cardDriverName, cardDriverVehicle, cardDriverPhone;
//    private Button callDriverBtn;
//    private String selectedDriverPhone = "";
//
//    private FusedLocationProviderClient mFusedLocationClient;
//    private Location mLastLocation;
//    private LocationRequest mLocationRequest;
//
//    private Button mLogout, mRequest, mSettings, mHistory;
//    private LinearLayout mDriverInfo;
//    private TextView mDriverName, mDriverPhone, mDriverCar;
//    private RatingBar mRatingBar;
//
//    private EditText mFromLocation, mToLocation;
//
//    private LatLng pickupLocation;
//    private Boolean requestBol = false;
//    private Marker pickupMarker;
//    private Marker mDriverMarker;
//    private String currentRequestId = null;
//
//    private String destination;
//    private LatLng destinationLatlng;
//
//    private int radius = 1;
//    private Boolean driverFound = false;
//    private String driverFoundID;
//
//    private GeoQuery geoQuery;
//    private DatabaseReference driverLocationRef, driveHasEndedRef;
//    private ValueEventListener driverLocationRefListener, driveHasEndedRefListener;
//
//    private DatabaseReference driversAvailableRef;
//    private ValueEventListener driversAvailableRefListener;
//
//    private static final int AUTOCOMPLETE_FROM_REQUEST_CODE = 1001;
//    private static final int AUTOCOMPLETE_TO_REQUEST_CODE = 1002;
//
//    // To store multiple available driver markers
//    private HashMap<String, Marker> availableDriverMarkers = new HashMap<>();
//
//    // show dummy drivers once per request
//    private boolean dummyShownForCurrentRequest = false;
//    private DatabaseReference currentRideRef;
//    private ValueEventListener currentRideListener;
//
//    private void watchRequestAssignment(String requestId) {
//        if (currentRideRef != null && currentRideListener != null) {
//            try { currentRideRef.removeEventListener(currentRideListener); } catch (Exception ignored) {}
//        }
//        currentRideRef = FirebaseDatabase.getInstance().getReference("RideRequests").child(requestId);
//        currentRideListener = currentRideRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!snapshot.exists()) return;
//                String acceptedDriver = snapshot.child("acceptedDriver").getValue(String.class);
//                String status = snapshot.child("status").getValue(String.class);
//                if (acceptedDriver != null && !acceptedDriver.isEmpty()) {
//                    // driver assigned — reuse existing flow
//                    driverFoundID = acceptedDriver;
//                    driverFound = true;
//                    getDriverLocation();
//                    getDriverInfo();
//                    getHasRideEnded();
//                    mRequest.setText("Driver Assigned");
//                    // optionally show driver_info_card
//                } else if ("searching".equals(status)) {
//                    mRequest.setText("Searching for Ambulance...");
//                }
//            }
//            @Override public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_customer_map);
//
//        // view bindings
//        driverInfoCard = findViewById(R.id.driver_info_card);
//        cardDriverName = findViewById(R.id.card_driver_name);
//        cardDriverVehicle = findViewById(R.id.card_driver_vehicle);
//        cardDriverPhone = findViewById(R.id.card_driver_phone);
//        callDriverBtn = findViewById(R.id.btn_call_driver);
//
//        mLogout = findViewById(R.id.logout);
//        mRequest = findViewById(R.id.request);
//        mSettings = findViewById(R.id.settings);
//        mHistory = findViewById(R.id.history);
//        mDriverInfo = findViewById(R.id.driverInfo);
//        mDriverName = findViewById(R.id.driverName);
//        mDriverPhone = findViewById(R.id.driverPhone);
//        mDriverCar = findViewById(R.id.driverCar);
//        mRatingBar = findViewById(R.id.ratingBar);
//
//        mFromLocation = findViewById(R.id.fromLocation);
//        mToLocation = findViewById(R.id.toLocation);
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        // Places initialization - using your original pattern (no new resource keys)
//        if (!Places.isInitialized()) {
//            // NOTE: keeping the same literal you had in working code; do not change unless you know what you do.
//            Places.initialize(getApplicationContext(), "AIzaSyCPwOM_929IDlkLCOs3N7u2OsWVISeCuKU");
//        }
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        if (mapFragment != null) mapFragment.getMapAsync(this);
//
//        mFromLocation.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_FROM_REQUEST_CODE));
//        mToLocation.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_TO_REQUEST_CODE));
//
//        mLogout.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(CustomerMapActivity.this, Welcome_Activity.class));
//            finish();
//        });
//
//        mSettings.setOnClickListener(v -> startActivity(new Intent(CustomerMapActivity.this, CustomerSettingsActivity.class)));
//        mHistory.setOnClickListener(v -> {
//            Intent intent = new Intent(CustomerMapActivity.this, HistoryActivity.class);
//            intent.putExtra("customerOrDriver", "Customers");
//            startActivity(intent);
//        });
//
//        mRequest.setOnClickListener(v -> {
//            if (requestBol) {
//                endRide();
//            } else {
//                if (mLastLocation == null) {
//                    Toast.makeText(this, "Please wait for location...", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                requestBol = true;
//                dummyShownForCurrentRequest = false;
//                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
//                new GeoFire(ref).setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//
//                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                pickupMarker = mMap.addMarker(new MarkerOptions()
//                        .position(pickupLocation)
//                        .title("Pickup Here")
//                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));
//
//                // show drivers (dummy quickly, then registered & available)
//                showAllAvailableDrivers();
//
//                mRequest.setText("Getting Your Ambulance...");
//                getClosestDriver();
//            }
//        });
//
//        // debug: long press request to simulate ride end & rating prompt (remove later)
//        mRequest.setOnLongClickListener(v -> {
////            if (driverFoundID != null && !driverFoundID.isEmpty()) {
////                Toast.makeText(CustomerMapActivity.this, "Simulating ride end for: " + driverFoundID, Toast.LENGTH_SHORT).show();
////                promptForRating(driverFoundID);
////            } else {
////                Toast.makeText(CustomerMapActivity.this, "No driver assigned yet.", Toast.LENGTH_SHORT).show();
////            }
////            return true;
//            // --- create canonical ride request ---
//            DatabaseReference rideReqRef = FirebaseDatabase.getInstance().getReference("RideRequests").push();
//            String requestId = rideReqRef.getKey();
//            long ts = System.currentTimeMillis();
//
//            RideRequest rr = new RideRequest(requestId,
//                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                    mLastLocation.getLatitude(), mLastLocation.getLongitude(),
//                    destinationLatlng != null ? destinationLatlng.latitude : 0,
//                    destinationLatlng != null ? destinationLatlng.longitude : 0,
//                    destination != null ? destination : "",
//                    "searching",
//                    ts
//            );
//
//
//            rideReqRef.setValue(rr);
//            watchRequestAssignment(requestId);
//
//// store locally so other methods can refer to it
//            this.currentRequestId = requestId;
//
//// add GeoFire entry for customer (keeps old code)
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
//            new GeoFire(ref).setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                    new com.firebase.geofire.GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//
//// notify nearby drivers using GeoQuery
//            DatabaseReference driverLocationRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
//            com.firebase.geofire.GeoFire geoFire = new com.firebase.geofire.GeoFire(driverLocationRef);
//            GeoQuery q = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
//            q.addGeoQueryEventListener(new GeoQueryEventListener() {
//                @Override
//                public void onKeyEntered(String driverId, GeoLocation location) {
//                    // write a notification entry for each nearby driver
//                    DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("DriverNotifications")
//                            .child(driverId).child(requestId);
//                    Map<String,Object> notif = new HashMap<>();
//                    notif.put("requestId", requestId);
//                    notif.put("customerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    notif.put("pickupLat", pickupLocation.latitude);
//                    notif.put("pickupLng", pickupLocation.longitude);
//                    notif.put("destination", destination != null ? destination : "");
//                    notif.put("status", "pending"); // pending/accepted/rejected
//                    notif.put("timestamp", ts);
//                    notifRef.setValue(notif);
//                }
//                @Override public void onKeyExited(String key) {}
//                @Override public void onKeyMoved(String key, GeoLocation location) {}
//                @Override public void onGeoQueryReady() {
//                    // optionally expand radius in your getClosestDriver flow; we keep your existing getClosestDriver() call
//                }
//                @Override public void onGeoQueryError(DatabaseError error) {}
//            });
//            return true;
//
//        });
//
//        // call button uses ACTION_DIAL (no CALL_PHONE permission)
//        callDriverBtn.setOnClickListener(v -> {
//            if (selectedDriverPhone == null || selectedDriverPhone.trim().isEmpty()) {
//                Toast.makeText(CustomerMapActivity.this, "Phone number not available", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
//            dialIntent.setData(Uri.parse("tel:" + selectedDriverPhone));
//            startActivity(dialIntent);
//        });
//    }
//
//    private void openAutocomplete(int requestCode) {
//        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
//        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
//                .setTypeFilter(TypeFilter.ADDRESS)
//                .setCountry("IN")
//                .build(this);
//        startActivityForResult(intent, requestCode);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE || requestCode == AUTOCOMPLETE_TO_REQUEST_CODE) {
//            if (resultCode == RESULT_OK && data != null) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE) {
//                    mFromLocation.setText(place.getAddress());
//                } else {
//                    mToLocation.setText(place.getAddress());
//                    destination = place.getName();
//                    destinationLatlng = place.getLatLng();
//                }
//                if (place.getLatLng() != null && mMap != null) {
//                    mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
//                }
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Show dummy drivers + all registered drivers + realtime driversAvailable updates
//    private void showAllAvailableDrivers() {
//        if (!dummyShownForCurrentRequest) {
//            showInitialDummyDrivers();
//            dummyShownForCurrentRequest = true;
//        }
//
//        // 1) show all registered drivers (Users/Drivers) if they have location stored
//        DatabaseReference allDriversRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers");
//        allDriversRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot driverSnap : snapshot.getChildren()) {
//                    final String driverId = driverSnap.getKey();
//                    if (driverId == null) continue;
//
//                    // try driversAvailable first
//                    DatabaseReference availRef = FirebaseDatabase.getInstance().getReference("driversAvailable").child(driverId);
//                    availRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapAvail) {
//                            if (snapAvail.exists() && snapAvail.child("l").exists()) {
//                                List<Object> loc = (List<Object>) snapAvail.child("l").getValue();
//                                double lat = Double.parseDouble(loc.get(0).toString());
//                                double lng = Double.parseDouble(loc.get(1).toString());
//                                placeDriverMarker(driverId, lat, lng);
//                            } else if (driverSnap.child("l").exists()) {
//                                List<Object> loc2 = (List<Object>) driverSnap.child("l").getValue();
//                                double lat = Double.parseDouble(loc2.get(0).toString());
//                                double lng = Double.parseDouble(loc2.get(1).toString());
//                                placeDriverMarker(driverId, lat, lng);
//                            } else {
//                                // no location found; skip
//                            }
//                        }
//                        @Override public void onCancelled(@NonNull DatabaseError error) {}
//                    });
//                }
//            }
//            @Override public void onCancelled(@NonNull DatabaseError error) {}
//        });
//
//        // 2) realtime updates from driversAvailable
//        driversAvailableRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
//        if (driversAvailableRefListener == null) {
//            driversAvailableRefListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot child : snapshot.getChildren()) {
//                        String id = child.getKey();
//                        if (id == null) continue;
//
//                        if (child.child("l").exists()) {
//                            List<Object> loc = (List<Object>) child.child("l").getValue();
//                            double lat = Double.parseDouble(loc.get(0).toString());
//                            double lng = Double.parseDouble(loc.get(1).toString());
//                            if (availableDriverMarkers.containsKey(id)) {
//                                availableDriverMarkers.get(id).setPosition(new LatLng(lat, lng));
//                                availableDriverMarkers.get(id).setTag(id);
//                            } else {
//                                placeDriverMarker(id, lat, lng);
//                            }
//                        } else {
//                            if (availableDriverMarkers.containsKey(id)) {
//                                availableDriverMarkers.get(id).remove();
//                                availableDriverMarkers.remove(id);
//                            }
//                        }
//                    }
//                }
//                @Override public void onCancelled(@NonNull DatabaseError error) {}
//            };
//            driversAvailableRef.addValueEventListener(driversAvailableRefListener);
//        }
//    }
//
//    private void placeDriverMarker(String driverId, double lat, double lng) {
//        if (mMap == null) return;
//        LatLng pos = new LatLng(lat, lng);
//        Marker marker = mMap.addMarker(new MarkerOptions()
//                .position(pos)
//                .title("Available Ambulance")
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//        if (marker != null) {
//            marker.setTag(driverId);
//            availableDriverMarkers.put(driverId, marker);
//        }
//    }
//
//    private void showInitialDummyDrivers() {
//        List<String> toRemove = new ArrayList<>();
//        for (String key : new ArrayList<>(availableDriverMarkers.keySet())) {
//            if (key.startsWith("DUMMY_")) {
//                Marker m = availableDriverMarkers.get(key);
//                if (m != null) m.remove();
//                toRemove.add(key);
//            }
//        }
//        for (String k : toRemove) availableDriverMarkers.remove(k);
//
//        LatLng base = pickupLocation != null ? pickupLocation : (mLastLocation != null ? new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) : new LatLng(28.6139, 77.2090));
//        LatLng d1 = new LatLng(base.latitude + 0.004, base.longitude + 0.003);
//        LatLng d2 = new LatLng(base.latitude - 0.0035, base.longitude + 0.002);
//        LatLng d3 = new LatLng(base.latitude + 0.0025, base.longitude - 0.0035);
//
//        Marker m1 = mMap.addMarker(new MarkerOptions().position(d1).title("Available Ambulance").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//        if (m1 != null) { m1.setTag("DUMMY_1"); availableDriverMarkers.put("DUMMY_1", m1); }
//        Marker m2 = mMap.addMarker(new MarkerOptions().position(d2).title("Available Ambulance").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//        if (m2 != null) { m2.setTag("DUMMY_2"); availableDriverMarkers.put("DUMMY_2", m2); }
//        Marker m3 = mMap.addMarker(new MarkerOptions().position(d3).title("Available Ambulance").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//        if (m3 != null) { m3.setTag("DUMMY_3"); availableDriverMarkers.put("DUMMY_3", m3); }
//
//        List<String> dumInfo = Arrays.asList("🚑 Driver A\n📞 9999999999\n⭐ 4.5", "🚑 Driver B\n📞 8888888888\n⭐ 4.2", "🚑 Driver C\n📞 7777777777\n⭐ 4.8");
//        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
//        builder.setTitle("Available Drivers (nearby)");
//        builder.setItems(dumInfo.toArray(new String[0]), (dialog, which) -> Toast.makeText(CustomerMapActivity.this, "Selected:\n" + dumInfo.get(which), Toast.LENGTH_LONG).show());
//        builder.setPositiveButton("Close", null);
//        builder.show();
//    }
//
//    private void getClosestDriver() {
//        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
//        GeoFire geoFire = new GeoFire(driverLocation);
//        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
//        geoQuery.removeAllListeners();
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                if (!driverFound && requestBol) {
//                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
//                            .child("Users").child("Drivers").child(key);
//                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                driverFound = true;
//                                driverFoundID = dataSnapshot.getKey();
//
//                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
//                                        .child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
//                                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                HashMap map = new HashMap();
//                                map.put("customerRideId", customerId);
//                                map.put("destination", destination);
//                                if (destinationLatlng != null) {
//                                    map.put("destinationLat", destinationLatlng.latitude);
//                                    map.put("destinationLng", destinationLatlng.longitude);
//                                }
//                                driverRef.updateChildren(map);
//
//                                getDriverLocation();
//                                getDriverInfo();
//                                getHasRideEnded();
//                                mRequest.setText("Looking for Driver's Location...");
//                            }
//                        }
//
//                        @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
//                    });
//                }
//            }
//
//            @Override public void onKeyExited(String key) {}
//            @Override public void onKeyMoved(String key, GeoLocation location) {}
//            @Override public void onGeoQueryReady() {
//                if (!driverFound) {
//                    if (radius < 50) { radius++; getClosestDriver(); }
//                    else Toast.makeText(CustomerMapActivity.this, "No drivers found within 50 km.", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override public void onGeoQueryError(DatabaseError error) {}
//        });
//    }
//
//    private void getDriverLocation() {
//        driverLocationRef = FirebaseDatabase.getInstance().getReference()
//                .child("driversWorking").child(driverFoundID).child("l");
//        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists() && requestBol) {
//                    List<Object> map = (List<Object>) dataSnapshot.getValue();
//                    double LocationLat = 0;
//                    double LocationLng = 0;
//                    if (map.get(0) != null) LocationLat = Double.parseDouble(map.get(0).toString());
//                    if (map.get(1) != null) LocationLng = Double.parseDouble(map.get(1).toString());
//
//                    LatLng driverLatLng = new LatLng(LocationLat, LocationLng);
//                    if (mDriverMarker != null) mDriverMarker.remove();
//
//                    Location loc1 = new Location("");
//                    loc1.setLatitude(pickupLocation.latitude);
//                    loc1.setLongitude(pickupLocation.longitude);
//
//                    Location loc2 = new Location("");
//                    loc2.setLatitude(driverLatLng.latitude);
//                    loc2.setLongitude(driverLatLng.longitude);
//
//                    float distance = loc1.distanceTo(loc2);
//                    if (distance < 100) {
//                        mRequest.setText("Ambulance Arrived");
//                    } else {
//                        int dis = (int) distance / 1000;
//                        mRequest.setText("Ambulance Found: " + dis + " Kms away...");
//                    }
//
//                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng)
//                            .title("Your Ambulance")
//                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//                }
//            }
//
//            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
//        });
//    }
//
//    private void getDriverInfo() {
//        mDriverInfo.setVisibility(View.VISIBLE);
//        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
//                .child("Users").child("Drivers").child(driverFoundID);
//        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    if (dataSnapshot.child("Name").getValue() != null)
//                        mDriverName.setText(dataSnapshot.child("Name").getValue().toString());
//                    if (dataSnapshot.child("Phone").getValue() != null) {
//                        mDriverPhone.setText(dataSnapshot.child("Phone").getValue().toString());
//                        selectedDriverPhone = dataSnapshot.child("Phone").getValue().toString();
//                    }
//                    if (dataSnapshot.child("Car").getValue() != null)
//                        mDriverCar.setText(dataSnapshot.child("Car").getValue().toString());
//
//                    if (dataSnapshot.child("rating").exists()) {
//                        int ratingSum = 0;
//                        float ratingsCount = 0f;
//                        for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
//                            try {
//                                ratingSum += Integer.parseInt(child.getValue().toString());
//                                ratingsCount++;
//                            } catch (NumberFormatException e) { }
//                        }
//                        if (ratingsCount > 0f) {
//                            float ratingsAvg = (float) ratingSum / ratingsCount;
//                            mRatingBar.setRating(ratingsAvg);
//                        } else {
//                            mRatingBar.setRating(0f);
//                        }
//                    } else {
//                        mRatingBar.setRating(0f);
//                    }
//                }
//            }
//
//            @Override public void onCancelled(DatabaseError databaseError) {}
//        });
//    }
//
//    private void getHasRideEnded() {
//        driveHasEndedRef = FirebaseDatabase.getInstance().getReference()
//                .child("Users").child("Drivers").child(driverFoundID)
//                .child("customerRequest").child("customerRideId");
//
//        if (driveHasEndedRefListener != null && driveHasEndedRef != null) {
//            try { driveHasEndedRef.removeEventListener(driveHasEndedRefListener); } catch (Exception ignored) {}
//        }
//
//        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
//            @Override public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean exists = dataSnapshot.exists();
//                Toast.makeText(CustomerMapActivity.this, "ride node exists=" + exists, Toast.LENGTH_SHORT).show();
//                Toast.makeText(CustomerMapActivity.this, "driverFoundID=" + driverFoundID, Toast.LENGTH_SHORT).show();
//
//                if (!exists) {
//                    if (driverFoundID != null && !driverFoundID.isEmpty()) {
//                        new Handler(Looper.getMainLooper()).postDelayed(() -> promptForRating(driverFoundID), 200);
//                    } else {
//                        endRide();
//                    }
//                }
//            }
//            @Override public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(CustomerMapActivity.this, "Ended-listener cancelled: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void promptForRating(String driverId) {
//        if (driverId == null) { endRide(); return; }
//
//        Toast.makeText(this, "Please rate driver: " + driverId, Toast.LENGTH_SHORT).show();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Rate your Ambulance Service");
//
//        final RatingBar ratingBar = new RatingBar(this);
//        ratingBar.setNumStars(5);
//        ratingBar.setStepSize(0.5f);
//        ratingBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//        LinearLayout container = new LinearLayout(this);
//        container.setPadding(50, 20, 50, 20);
//        container.setGravity(Gravity.CENTER);
//        container.addView(ratingBar);
//
//        builder.setView(container);
//
//        builder.setPositiveButton("Submit", (dialog, which) -> {
//            float rating = ratingBar.getRating();
//            int intRating = Math.max(1, Math.round(rating));
//            saveDriverRating(driverId, intRating);
//            saveRideRecord(driverId, intRating);
//            endRide();
//        });
//
//        builder.setNegativeButton("Skip", (dialog, which) -> {
//            saveRideRecord(driverId, -1);
//            endRide();
//        });
//
//        builder.setCancelable(false);
//        builder.show();
//    }
//
//    private void saveDriverRating(String driverId, int rating) {
//        if (driverId == null || rating < 0) return;
//
//        DatabaseReference ratingRef = FirebaseDatabase.getInstance()
//                .getReference().child("Users").child("Drivers").child(driverId).child("rating");
//
//        ratingRef.push().setValue(rating).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) Toast.makeText(CustomerMapActivity.this, "Thanks for rating!", Toast.LENGTH_SHORT).show();
//            else Toast.makeText(CustomerMapActivity.this, "Couldn't save rating — try again later.", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    private void saveRideRecord(String driverId, int rating) {
//        try {
//            String rideId = UUID.randomUUID().toString();
//            String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            long ts = System.currentTimeMillis();
//
//            Map<String, Object> ride = new HashMap<>();
//            ride.put("rideId", rideId);
//            ride.put("driverId", driverId);
//            ride.put("customerId", customerId);
//            ride.put("timestamp", ts);
//            ride.put("pickupLat", pickupLocation != null ? pickupLocation.latitude : null);
//            ride.put("pickupLng", pickupLocation != null ? pickupLocation.longitude : null);
//            ride.put("destination", destination != null ? destination : "");
//            if (rating >= 0) ride.put("rating", rating);
//
//            DatabaseReference ridesRef = FirebaseDatabase.getInstance().getReference("rides").child(rideId);
//            ridesRef.setValue(ride);
//        } catch (Exception ignored) {}
//    }
//
//    private void endRide() {
//        requestBol = false;
//        if (geoQuery != null) geoQuery.removeAllListeners();
//
//        if (driverLocationRefListener != null && driverLocationRef != null) {
//            driverLocationRef.removeEventListener(driverLocationRefListener);
//            driverLocationRefListener = null;
//        }
//        if (driveHasEndedRefListener != null && driveHasEndedRef != null) {
//            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
//            driveHasEndedRefListener = null;
//        }
//
//        if (driverFoundID != null) {
//            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
//                    .child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
//            driverRef.removeValue();
//            driverFoundID = null;
//        }
//
//        driverFound = false;
//        radius = 1;
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        new GeoFire(FirebaseDatabase.getInstance().getReference("customerRequest")).removeLocation(userId);
//
//        if (pickupMarker != null) { pickupMarker.remove(); pickupMarker = null; }
//        if (mDriverMarker != null) { mDriverMarker.remove(); mDriverMarker = null; }
//
//        for (Marker marker : availableDriverMarkers.values()) {
//            if (marker != null) marker.remove();
//        }
//        availableDriverMarkers.clear();
//
//        if (driversAvailableRef != null && driversAvailableRefListener != null) {
//            driversAvailableRef.removeEventListener(driversAvailableRefListener);
//            driversAvailableRefListener = null;
//            driversAvailableRef = null;
//        }
//
//        mRequest.setText("Request An Ambulance");
//        mDriverInfo.setVisibility(View.GONE);
//        mDriverName.setText("");
//        mDriverPhone.setText("");
//        mDriverCar.setText("");
//        selectedDriverPhone = "";
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
//            try { mMap.setMyLocationEnabled(true); } catch (SecurityException ignored) {}
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//
//        mMap.setOnMarkerClickListener(marker -> {
//            Object tag = marker.getTag();
//            if (tag != null && tag instanceof String) {
//                String driverId = (String) tag;
//
//                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(driverId);
//                driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String name = snapshot.child("Name").getValue(String.class);
//                        String phone = snapshot.child("Phone").getValue(String.class);
//                        String vehicle = snapshot.child("VehicleNumber").getValue(String.class);
//                        double rating = 0.0;
//                        if (snapshot.child("rating").exists()) {
//                            int sum = 0, count = 0;
//                            for (DataSnapshot r : snapshot.child("rating").getChildren()) {
//                                try { sum += Integer.parseInt(r.getValue().toString()); count++; } catch (Exception ignored) {}
//                            }
//                            if (count > 0) rating = (double) sum / count;
//                        }
//                        String title = (name != null ? name : "Ambulance");
//                        String body = "Vehicle: " + (vehicle != null ? vehicle : "N/A") + "\nPhone: " + (phone != null ? phone : "N/A") + "\nRating: " + String.format("%.1f", rating);
//                        AlertDialog.Builder b = new AlertDialog.Builder(CustomerMapActivity.this);
//                        b.setTitle(title);
//                        b.setMessage(body);
//                        b.setPositiveButton("Request This Ambulance", (d, w) -> {
//                            String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                            DatabaseReference rRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(driverId).child("customerRequest");
//                            Map<String, Object> map = new HashMap<>();
//                            map.put("customerRideId", customerId);
//                            map.put("destination", destination != null ? destination : "");
//                            if (destinationLatlng != null) {
//                                map.put("destinationLat", destinationLatlng.latitude);
//                                map.put("destinationLng", destinationLatlng.longitude);
//                            }
//                            rRef.updateChildren(map);
//
//                            driverFoundID = driverId;
//                            driverFound = true;
//                            getDriverLocation();
//                            getDriverInfo();
//                            getHasRideEnded();
//                            mRequest.setText("Ride Requested: Waiting for Ambulance...");
//                            Toast.makeText(CustomerMapActivity.this, "Requested ambulance: " + title, Toast.LENGTH_SHORT).show();
//                        });
//                        b.setNeutralButton("Call", (d, w) -> {
//                            if (phone != null && !phone.isEmpty()) {
//                                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
//                                startActivity(dialIntent);
//                            } else Toast.makeText(CustomerMapActivity.this, "Phone not available", Toast.LENGTH_SHORT).show();
//                        });
//                        b.setNegativeButton("Close", null);
//                        b.show();
//                    }
//                    @Override public void onCancelled(@NonNull DatabaseError error) {}
//                });
//            }
//            return false;
//        });
//
//        // show drivers on map ASAP
//        showAllAvailableDrivers();
//    }
//
//    LocationCallback locationCallback = new LocationCallback() {
//        @Override public void onLocationResult(LocationResult locationResult) {
//            for (Location location : locationResult.getLocations()) {
//                mLastLocation = location;
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                if (mMap != null) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//            }
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            if (driverLocationRefListener != null && driverLocationRef != null) {
//                driverLocationRef.removeEventListener(driverLocationRefListener);
//                driverLocationRefListener = null;
//            }
//            if (driveHasEndedRefListener != null && driveHasEndedRef != null) {
//                driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
//                driveHasEndedRefListener = null;
//            }
//            if (driversAvailableRef != null && driversAvailableRefListener != null) {
//                driversAvailableRef.removeEventListener(driversAvailableRefListener);
//                driversAvailableRefListener = null;
//                driversAvailableRef = null;
//            }
//            if (mFusedLocationClient != null && locationCallback != null) {
//                mFusedLocationClient.removeLocationUpdates(locationCallback);
//            }
//        } catch (Exception ignored) {}
//    }
//}
package saksham.medrescue.saksham;

import sachdeva.saksham.medrescue.R;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * CustomerMapActivity (customer / patient side)
 * - Creates RideRequests entries and notifies nearby drivers (DriverNotifications/{driverId}/{requestId})
 * - Watches RideRequests/{requestId} for assignments and updates UI via watchRequestAssignment()
 * - Shows available drivers (dummy + registered + realtime driversAvailable)
 * - Supports guest/demo customer if no Firebase user present
 */
public class CustomerMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LinearLayout driverInfoCard;
    private TextView cardDriverName, cardDriverVehicle, cardDriverPhone;
    private Button callDriverBtn;
    private String selectedDriverPhone = "";

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private Button mLogout, mRequest, mSettings, mHistory;
    private LinearLayout mDriverInfo;
    private TextView mDriverName, mDriverPhone, mDriverCar;
    private RatingBar mRatingBar;

    private EditText mFromLocation, mToLocation;

    private LatLng pickupLocation;
    private Boolean requestBol = false;
    private Marker pickupMarker;
    private Marker mDriverMarker;
    private String currentRequestId = null;

    private String destination;
    private LatLng destinationLatlng;

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    private GeoQuery geoQuery;
    private DatabaseReference driverLocationRef, driveHasEndedRef;
    private ValueEventListener driverLocationRefListener, driveHasEndedRefListener;

    private DatabaseReference driversAvailableRef;
    private ValueEventListener driversAvailableRefListener;

    private static final int AUTOCOMPLETE_FROM_REQUEST_CODE = 1001;
    private static final int AUTOCOMPLETE_TO_REQUEST_CODE = 1002;

    // To store multiple available driver markers
    private HashMap<String, Marker> availableDriverMarkers = new HashMap<>();

    // show dummy drivers once per request
    private boolean dummyShownForCurrentRequest = false;
    private DatabaseReference currentRideRef;
    private ValueEventListener currentRideListener;

    // Watch assignment - reused from your earlier message
    private void watchRequestAssignment(String requestId) {
        if (requestId == null) return;
        if (currentRideRef != null && currentRideListener != null) {
            try { currentRideRef.removeEventListener(currentRideListener); } catch (Exception ignored) {}
        }
        currentRideRef = FirebaseDatabase.getInstance().getReference("RideRequests").child(requestId);
        currentRideListener = currentRideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                String acceptedDriver = snapshot.child("acceptedDriver").getValue(String.class);
                String status = snapshot.child("status").getValue(String.class);
                if (acceptedDriver != null && !acceptedDriver.isEmpty()) {
                    // driver assigned — reuse existing flow
                    driverFoundID = acceptedDriver;
                    driverFound = true;
                    getDriverLocation();
                    getDriverInfo();
                    getHasRideEnded();
                    if (mRequest != null) mRequest.setText("Driver Assigned");
                    // optionally show driver_info_card
                } else if ("searching".equals(status)) {
                    if (mRequest != null) mRequest.setText("Searching for Ambulance...");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);

        // view bindings
        driverInfoCard = findViewById(R.id.driver_info_card);
        cardDriverName = findViewById(R.id.card_driver_name);
        cardDriverVehicle = findViewById(R.id.card_driver_vehicle);
        cardDriverPhone = findViewById(R.id.card_driver_phone);
        callDriverBtn = findViewById(R.id.btn_call_driver);

        mLogout = findViewById(R.id.logout);
        mRequest = findViewById(R.id.request);
        mSettings = findViewById(R.id.settings);
        mHistory = findViewById(R.id.history);
        mDriverInfo = findViewById(R.id.driverInfo);
        mDriverName = findViewById(R.id.driverName);
        mDriverPhone = findViewById(R.id.driverPhone);
        mDriverCar = findViewById(R.id.driverCar);
        mRatingBar = findViewById(R.id.ratingBar);

        mFromLocation = findViewById(R.id.fromLocation);
        mToLocation = findViewById(R.id.toLocation);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Places initialization - keep your existing API key (do not change lightly)
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCPwOM_929IDlkLCOs3N7u2OsWVISeCuKU");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        mFromLocation.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_FROM_REQUEST_CODE));
        mToLocation.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_TO_REQUEST_CODE));

        mLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(CustomerMapActivity.this, Welcome_Activity.class));
            finish();
        });

        mSettings.setOnClickListener(v -> startActivity(new Intent(CustomerMapActivity.this, CustomerSettingsActivity.class)));
        mHistory.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerMapActivity.this, HistoryActivity.class);
            intent.putExtra("customerOrDriver", "Customers");
            startActivity(intent);
        });

        mRequest.setOnClickListener(v -> {
            if (requestBol) {
                // user cancels/ends the request
                endRide();
            } else {
                // start a canonical ride request (creates RideRequests entry + DriverNotifications)
                if (mLastLocation == null) {
                    Toast.makeText(this, "Please wait for location...", Toast.LENGTH_SHORT).show();
                    return;
                }

                requestBol = true;
                dummyShownForCurrentRequest = false;

                // determine a customer id (use real user if signed in, otherwise guest id)
                String customerId;
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                } else {
                    customerId = "GUEST_" + UUID.randomUUID().toString();
                    Toast.makeText(this, "Guest booking: " + customerId, Toast.LENGTH_SHORT).show();
                }

                // place GeoFire entry for customer (keeps old code compatibility)
                DatabaseReference geoRef = FirebaseDatabase.getInstance().getReference("customerRequest");
                new GeoFire(geoRef).setLocation(customerId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                if (pickupMarker != null) { pickupMarker.remove(); pickupMarker = null; }
                pickupMarker = mMap.addMarker(new MarkerOptions()
                        .position(pickupLocation)
                        .title("Pickup Here")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));

                // create canonical RideRequests/{requestId}
                DatabaseReference rideReqRef = FirebaseDatabase.getInstance().getReference("RideRequests").push();
                final String requestId = rideReqRef.getKey();
                long ts = System.currentTimeMillis();

                Map<String, Object> rr = new HashMap<>();
                rr.put("requestId", requestId);
                rr.put("customerId", customerId);
                rr.put("pickupLat", pickupLocation.latitude);
                rr.put("pickupLng", pickupLocation.longitude);
                rr.put("destLat", destinationLatlng != null ? destinationLatlng.latitude : 0);
                rr.put("destLng", destinationLatlng != null ? destinationLatlng.longitude : 0);
                rr.put("destination", destination != null ? destination : "");
                rr.put("status", "searching"); // searching -> driver_assigned
                rr.put("timestamp", ts);

                // write ride request
                rideReqRef.setValue(rr);
                // store locally
                currentRequestId = requestId;

                // watch assignment so UI updates when a driver accepts
                watchRequestAssignment(requestId);

                // show drivers on map (dummy quickly, then registered & available)
                showAllAvailableDrivers();
                mRequest.setText("Getting Your Ambulance...");

                // Notify nearby drivers via GeoQuery on driversAvailable
                DatabaseReference driverLocationRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
                GeoFire notifyGeoFire = new GeoFire(driverLocationRef);

                GeoQuery q = notifyGeoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
                q.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String driverId, GeoLocation location) {
                        // write a notification entry for each nearby driver
                        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("DriverNotifications")
                                .child(driverId).child(requestId);
                        Map<String,Object> notif = new HashMap<>();
                        notif.put("requestId", requestId);
                        notif.put("customerId", customerId);
                        notif.put("pickupLat", pickupLocation.latitude);
                        notif.put("pickupLng", pickupLocation.longitude);
                        notif.put("destination", destination != null ? destination : "");
                        notif.put("status", "pending"); // pending/accepted/rejected
                        notif.put("timestamp", ts);
                        notifRef.setValue(notif);
                    }

                    @Override public void onKeyExited(String key) {}
                    @Override public void onKeyMoved(String key, GeoLocation location) {}
                    @Override public void onGeoQueryReady() {
                        // if needed, expand radius logic can be applied here (kept separate from getClosestDriver)
                    }
                    @Override public void onGeoQueryError(DatabaseError error) {}
                });

            }
        });

        // long-press debug retained but does nothing critical (kept to avoid accidental removal)
        mRequest.setOnLongClickListener(v -> {
            Toast.makeText(CustomerMapActivity.this, "Long-press ignored in normal flow.", Toast.LENGTH_SHORT).show();
            return true;
        });

        // call button uses ACTION_DIAL (no CALL_PHONE permission)
        callDriverBtn.setOnClickListener(v -> {
            if (selectedDriverPhone == null || selectedDriverPhone.trim().isEmpty()) {
                Toast.makeText(CustomerMapActivity.this, "Phone number not available", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + selectedDriverPhone));
            startActivity(dialIntent);
        });
    }

    private void openAutocomplete(int requestCode) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ADDRESS)
                .setCountry("IN")
                .build(this);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE || requestCode == AUTOCOMPLETE_TO_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE) {
                    mFromLocation.setText(place.getAddress());
                } else {
                    mToLocation.setText(place.getAddress());
                    destination = place.getName();
                    destinationLatlng = place.getLatLng();
                }
                if (place.getLatLng() != null && mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Show dummy drivers + all registered drivers + realtime driversAvailable updates
    private void showAllAvailableDrivers() {
        if (!dummyShownForCurrentRequest) {
            showInitialDummyDrivers();
            dummyShownForCurrentRequest = true;
        }

        // 1) show all registered drivers (Users/Drivers) if they have location stored
        DatabaseReference allDriversRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers");
        allDriversRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot driverSnap : snapshot.getChildren()) {
                    final String driverId = driverSnap.getKey();
                    if (driverId == null) continue;

                    // try driversAvailable first
                    DatabaseReference availRef = FirebaseDatabase.getInstance().getReference("driversAvailable").child(driverId);
                    availRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapAvail) {
                            if (snapAvail.exists() && snapAvail.child("l").exists()) {
                                List<Object> loc = (List<Object>) snapAvail.child("l").getValue();
                                double lat = Double.parseDouble(loc.get(0).toString());
                                double lng = Double.parseDouble(loc.get(1).toString());
                                placeDriverMarker(driverId, lat, lng);
                            } else if (driverSnap.child("l").exists()) {
                                List<Object> loc2 = (List<Object>) driverSnap.child("l").getValue();
                                double lat = Double.parseDouble(loc2.get(0).toString());
                                double lng = Double.parseDouble(loc2.get(1).toString());
                                placeDriverMarker(driverId, lat, lng);
                            } else {
                                // no location found; skip
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 2) realtime updates from driversAvailable
        driversAvailableRef = FirebaseDatabase.getInstance().getReference("driversAvailable");
        if (driversAvailableRefListener == null) {
            driversAvailableRefListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String id = child.getKey();
                        if (id == null) continue;

                        if (child.child("l").exists()) {
                            List<Object> loc = (List<Object>) child.child("l").getValue();
                            double lat = Double.parseDouble(loc.get(0).toString());
                            double lng = Double.parseDouble(loc.get(1).toString());
                            if (availableDriverMarkers.containsKey(id)) {
                                availableDriverMarkers.get(id).setPosition(new LatLng(lat, lng));
                                availableDriverMarkers.get(id).setTag(id);
                            } else {
                                placeDriverMarker(id, lat, lng);
                            }
                        } else {
                            if (availableDriverMarkers.containsKey(id)) {
                                availableDriverMarkers.get(id).remove();
                                availableDriverMarkers.remove(id);
                            }
                        }
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            };
            driversAvailableRef.addValueEventListener(driversAvailableRefListener);
        }
    }

    private void placeDriverMarker(String driverId, double lat, double lng) {
        if (mMap == null) return;
        LatLng pos = new LatLng(lat, lng);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(pos)
                .title("Available Ambulance")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        if (marker != null) {
            marker.setTag(driverId);
            availableDriverMarkers.put(driverId, marker);
        }
    }
    private String readFirstString(DataSnapshot snap, String... keys) {
        for (String k : keys) {
            if (snap.child(k).exists() && snap.child(k).getValue() != null) {
                try {
                    return snap.child(k).getValue().toString();
                } catch (Exception ignored) {}
            }
        }
        return null;
    }


    private void showInitialDummyDrivers() {
        List<String> toRemove = new ArrayList<>();
        for (String key : new ArrayList<>(availableDriverMarkers.keySet())) {
            if (key.startsWith("DUMMY_")) {
                Marker m = availableDriverMarkers.get(key);
                if (m != null) m.remove();
                toRemove.add(key);
            }
        }
        for (String k : toRemove) availableDriverMarkers.remove(k);

        LatLng base = pickupLocation != null ? pickupLocation : (mLastLocation != null ? new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) : new LatLng(28.6139, 77.2090));
        LatLng d1 = new LatLng(base.latitude + 0.004, base.longitude + 0.003);
        LatLng d2 = new LatLng(base.latitude - 0.0035, base.longitude + 0.002);
        LatLng d3 = new LatLng(base.latitude + 0.0025, base.longitude - 0.0035);

        Marker m1 = mMap.addMarker(new MarkerOptions().position(d1).title("Available Ambulance").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        if (m1 != null) { m1.setTag("DUMMY_1"); availableDriverMarkers.put("DUMMY_1", m1); }
        Marker m2 = mMap.addMarker(new MarkerOptions().position(d2).title("Available Ambulance").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        if (m2 != null) { m2.setTag("DUMMY_2"); availableDriverMarkers.put("DUMMY_2", m2); }
        Marker m3 = mMap.addMarker(new MarkerOptions().position(d3).title("Available Ambulance").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        if (m3 != null) { m3.setTag("DUMMY_3"); availableDriverMarkers.put("DUMMY_3", m3); }

        List<String> dumInfo = Arrays.asList("🚑 Driver A\n📞 9999999999\n⭐ 4.5", "🚑 Driver B\n📞 8888888888\n⭐ 4.2", "🚑 Driver C\n📞 7777777777\n⭐ 4.8");
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
        builder.setTitle("Available Drivers (nearby)");
        builder.setItems(dumInfo.toArray(new String[0]), (dialog, which) -> Toast.makeText(CustomerMapActivity.this, "Selected:\n" + dumInfo.get(which), Toast.LENGTH_LONG).show());
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    // (Kept getClosestDriver for compatibility but primary flow uses RideRequests + DriverNotifications)
    private void getClosestDriver() {
        if (pickupLocation == null) {
            Toast.makeText(this, "Pickup not set", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol) {
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                driverFound = true;
                                driverFoundID = dataSnapshot.getKey();

                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                String customerId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "GUEST";
                                HashMap map = new HashMap();
                                map.put("customerRideId", customerId);
                                map.put("destination", destination);
                                if (destinationLatlng != null) {
                                    map.put("destinationLat", destinationLatlng.latitude);
                                    map.put("destinationLng", destinationLatlng.longitude);
                                }
                                driverRef.updateChildren(map);

                                getDriverLocation();
                                getDriverInfo();
                                getHasRideEnded();
                                if (mRequest != null) mRequest.setText("Looking for Driver's Location...");
                            }
                        }

                        @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }

            @Override public void onKeyExited(String key) {}
            @Override public void onKeyMoved(String key, GeoLocation location) {}
            @Override public void onGeoQueryReady() {
                if (!driverFound) {
                    if (radius < 50) { radius++; getClosestDriver(); }
                    else Toast.makeText(CustomerMapActivity.this, "No drivers found within 50 km.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onGeoQueryError(DatabaseError error) {}
        });
    }

    private void getDriverLocation() {
        if (driverFoundID == null) return;
        driverLocationRef = FirebaseDatabase.getInstance().getReference()
                .child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestBol) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double LocationLat = 0;
                    double LocationLng = 0;
                    if (map.get(0) != null) LocationLat = Double.parseDouble(map.get(0).toString());
                    if (map.get(1) != null) LocationLng = Double.parseDouble(map.get(1).toString());

                    LatLng driverLatLng = new LatLng(LocationLat, LocationLng);
                    if (mDriverMarker != null) mDriverMarker.remove();

                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
                    if (distance < 100) {
                        mRequest.setText("Ambulance Arrived");
                    } else {
                        int dis = (int) distance / 1000;
                        mRequest.setText("Ambulance Found: " + dis + " Kms away...");
                    }

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng)
                            .title("Your Ambulance")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void getDriverInfo() {
        if (driverFoundID == null) return;
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Drivers").child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("Name").getValue() != null)
                        mDriverName.setText(dataSnapshot.child("Name").getValue().toString());
                    if (dataSnapshot.child("Phone").getValue() != null) {
                        mDriverPhone.setText(dataSnapshot.child("Phone").getValue().toString());
                        selectedDriverPhone = dataSnapshot.child("Phone").getValue().toString();
                    }
                    if (dataSnapshot.child("Car").getValue() != null)
                        mDriverCar.setText(dataSnapshot.child("Car").getValue().toString());

                    if (dataSnapshot.child("rating").exists()) {
                        int ratingSum = 0;
                        float ratingsCount = 0f;
                        for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
                            try {
                                ratingSum += Integer.parseInt(child.getValue().toString());
                                ratingsCount++;
                            } catch (NumberFormatException e) { }
                        }
                        if (ratingsCount > 0f) {
                            float ratingsAvg = (float) ratingSum / ratingsCount;
                            mRatingBar.setRating(ratingsAvg);
                        } else {
                            mRatingBar.setRating(0f);
                        }
                    } else {
                        mRatingBar.setRating(0f);
                    }
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void getHasRideEnded() {
        if (driverFoundID == null) return;

        driveHasEndedRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Drivers").child(driverFoundID)
                .child("customerRequest").child("customerRideId");

        if (driveHasEndedRefListener != null && driveHasEndedRef != null) {
            try { driveHasEndedRef.removeEventListener(driveHasEndedRefListener); } catch (Exception ignored) {}
        }

        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();

                if (!exists) {
                    if (driverFoundID != null && !driverFoundID.isEmpty()) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> promptForRating(driverFoundID), 200);
                    } else {
                        endRide();
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerMapActivity.this, "Ended-listener cancelled: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void promptForRating(String driverId) {
        if (driverId == null) { endRide(); return; }

        Toast.makeText(this, "Please rate driver: " + driverId, Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate your Ambulance Service");

        final RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout container = new LinearLayout(this);
        container.setPadding(50, 20, 50, 20);
        container.setGravity(Gravity.CENTER);
        container.addView(ratingBar);

        builder.setView(container);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            float rating = ratingBar.getRating();
            int intRating = Math.max(1, Math.round(rating));
            saveDriverRating(driverId, intRating);
            saveRideRecord(driverId, intRating);
            endRide();
        });

        builder.setNegativeButton("Skip", (dialog, which) -> {
            saveRideRecord(driverId, -1);
            endRide();
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void saveDriverRating(String driverId, int rating) {
        if (driverId == null || rating < 0) return;

        DatabaseReference ratingRef = FirebaseDatabase.getInstance()
                .getReference().child("Users").child("Drivers").child(driverId).child("rating");

        ratingRef.push().setValue(rating).addOnCompleteListener(task -> {
            if (task.isSuccessful()) Toast.makeText(CustomerMapActivity.this, "Thanks for rating!", Toast.LENGTH_SHORT).show();
            else Toast.makeText(CustomerMapActivity.this, "Couldn't save rating — try again later.", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveRideRecord(String driverId, int rating) {
        try {
            String rideId = UUID.randomUUID().toString();
            String customerId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "GUEST";
            long ts = System.currentTimeMillis();

            Map<String, Object> ride = new HashMap<>();
            ride.put("rideId", rideId);
            ride.put("driverId", driverId);
            ride.put("customerId", customerId);
            ride.put("timestamp", ts);
            ride.put("pickupLat", pickupLocation != null ? pickupLocation.latitude : null);
            ride.put("pickupLng", pickupLocation != null ? pickupLocation.longitude : null);
            ride.put("destination", destination != null ? destination : "");
            if (rating >= 0) ride.put("rating", rating);

            DatabaseReference ridesRef = FirebaseDatabase.getInstance().getReference("rides").child(rideId);
            ridesRef.setValue(ride);
        } catch (Exception ignored) {}
    }

    private void endRide() {
        requestBol = false;
        if (geoQuery != null) geoQuery.removeAllListeners();

        if (driverLocationRefListener != null && driverLocationRef != null) {
            driverLocationRef.removeEventListener(driverLocationRefListener);
            driverLocationRefListener = null;
        }
        if (driveHasEndedRefListener != null && driveHasEndedRef != null) {
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
            driveHasEndedRefListener = null;
        }

        if (driverFoundID != null) {
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;
        }

        driverFound = false;
        radius = 1;

        // remove GeoFire entry for customer
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            new GeoFire(FirebaseDatabase.getInstance().getReference("customerRequest")).removeLocation(userId);
        }

        // remove the canonical RideRequests node and notify removal to drivers (best-effort)
        if (currentRequestId != null) {
            DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("RideRequests").child(currentRequestId);
            try { rideRef.removeValue(); } catch (Exception ignored) {}

            // best-effort: remove notifications under each driver
            DatabaseReference notifRoot = FirebaseDatabase.getInstance().getReference("DriverNotifications");
            notifRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot driverNode : snapshot.getChildren()) {
                        if (driverNode.child(currentRequestId).exists()) {
                            driverNode.child(currentRequestId).getRef().removeValue();
                        }
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });

            // remove watch listener
            if (currentRideRef != null && currentRideListener != null) {
                try { currentRideRef.removeEventListener(currentRideListener); } catch (Exception ignored) {}
                currentRideRef = null;
                currentRideListener = null;
            }

            currentRequestId = null;
        }

        if (pickupMarker != null) { pickupMarker.remove(); pickupMarker = null; }
        if (mDriverMarker != null) { mDriverMarker.remove(); mDriverMarker = null; }

        for (Marker marker : availableDriverMarkers.values()) {
            if (marker != null) marker.remove();
        }
        availableDriverMarkers.clear();

        if (driversAvailableRef != null && driversAvailableRefListener != null) {
            driversAvailableRef.removeEventListener(driversAvailableRefListener);
            driversAvailableRefListener = null;
            driversAvailableRef = null;
        }

        if (mRequest != null) mRequest.setText("Request An Ambulance");
        if (mDriverInfo != null) mDriverInfo.setVisibility(View.GONE);
        if (mDriverName != null) mDriverName.setText("");
        if (mDriverPhone != null) mDriverPhone.setText("");
        if (mDriverCar != null) mDriverCar.setText("");
        selectedDriverPhone = "";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
            try { mMap.setMyLocationEnabled(true); } catch (SecurityException ignored) {}
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mMap.setOnMarkerClickListener(marker -> {
//            Object tag = marker.getTag();
//            if (tag != null && tag instanceof String) {
//                String driverId = (String) tag;
//
//                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(driverId);
//                driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String name = snapshot.child("Name").getValue(String.class);
//                        String phone = snapshot.child("Phone").getValue(String.class);
//                        String vehicle = snapshot.child("VehicleNumber").getValue(String.class);
//                        double rating = 0.0;
//                        if (snapshot.child("rating").exists()) {
//                            int sum = 0, count = 0;
//                            for (DataSnapshot r : snapshot.child("rating").getChildren()) {
//                                try { sum += Integer.parseInt(r.getValue().toString()); count++; } catch (Exception ignored) {}
//                            }
//                            if (count > 0) rating = (double) sum / count;
//                        }
//                        String title = (name != null ? name : "Ambulance");
//                        String body = "Vehicle: " + (vehicle != null ? vehicle : "N/A") + "\nPhone: " + (phone != null ? phone : "N/A") + "\nRating: " + String.format("%.1f", rating);
//                        AlertDialog.Builder b = new AlertDialog.Builder(CustomerMapActivity.this);
//                        b.setTitle(title);
//                        b.setMessage(body);
//                        b.setPositiveButton("Request This Ambulance", (d, w) -> {
//                            String customerId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "GUEST";
//                            DatabaseReference rRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(driverId).child("customerRequest");
//                            Map<String, Object> map = new HashMap<>();
//                            map.put("customerRideId", customerId);
//                            map.put("destination", destination != null ? destination : "");
//                            if (destinationLatlng != null) {
//                                map.put("destinationLat", destinationLatlng.latitude);
//                                map.put("destinationLng", destinationLatlng.longitude);
//                            }
//                            rRef.updateChildren(map);
//
//                            driverFoundID = driverId;
//                            driverFound = true;
//                            getDriverLocation();
//                            getDriverInfo();
//                            getHasRideEnded();
//                            if (mRequest != null) mRequest.setText("Ride Requested: Waiting for Ambulance...");
//                            Toast.makeText(CustomerMapActivity.this, "Requested ambulance: " + title, Toast.LENGTH_SHORT).show();
//                        });
//                        b.setNeutralButton("Call", (d, w) -> {
//                            if (phone != null && !phone.isEmpty()) {
//                                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
//                                startActivity(dialIntent);
//                            } else Toast.makeText(CustomerMapActivity.this, "Phone not available", Toast.LENGTH_SHORT).show();
//                        });
//                        b.setNegativeButton("Close", null);
//                        b.show();
//                    }
//                    @Override public void onCancelled(@NonNull DatabaseError error) {}
//                });
//            }
//            return false;
            Object tag = marker.getTag();
            if (tag != null && tag instanceof String) {
                final String driverId = (String) tag;

                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(driverId);
                driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // helper to read multiple possible field names
                        String name = readFirstString(snapshot, "Name", "name", "driverName");
                        String phone = readFirstString(snapshot, "Phone", "phone", "PhoneNumber", "contact");
                        String vehicle = readFirstString(snapshot, "Car", "CarNumber", "VehicleNumber", "vehicle", "vehicle_no");
                        if (name == null) name = "Ambulance";
                        if (phone == null) phone = "Not Available";
                        if (vehicle == null) vehicle = "N/A";

                        // compute rating safely - some DBs store floats, others store ints or child nodes
                        double rating = 0.0;
                        if (snapshot.child("rating").exists()) {
                            int sum = 0;
                            int count = 0;
                            for (DataSnapshot r : snapshot.child("rating").getChildren()) {
                                try {
                                    // rating might be number or string
                                    String v = r.getValue().toString();
                                    sum += Integer.parseInt(v);
                                    count++;
                                } catch (Exception e) {
                                    try {
                                        double dv = Double.parseDouble(r.getValue().toString());
                                        sum += Math.round(dv);
                                        count++;
                                    } catch (Exception ignored) {}
                                }
                            }
                            if (count > 0) rating = (double) sum / count;
                        } else {
                            // maybe single numeric rating field
                            String rStr = readFirstString(snapshot, "rating", "Rating", "avg_rating");
                            if (rStr != null) {
                                try { rating = Double.parseDouble(rStr); } catch (Exception ignored) {}
                            }
                        }

                        String title = name;
                        String body = "Vehicle: " + vehicle + "\nPhone: " + phone + "\nRating: " + String.format("%.1f", rating);

                        AlertDialog.Builder b = new AlertDialog.Builder(CustomerMapActivity.this);
                        b.setTitle(title);
                        b.setMessage(body);
                        b.setPositiveButton("Request This Ambulance", (d, w) -> {
                            String customerId = FirebaseAuth.getInstance().getCurrentUser() != null
                                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                                    : "GUEST";
                            DatabaseReference rRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(driverId).child("customerRequest");
                            Map<String, Object> map = new HashMap<>();
                            map.put("customerRideId", customerId);
                            map.put("destination", destination != null ? destination : "");
                            if (destinationLatlng != null) {
                                map.put("destinationLat", destinationLatlng.latitude);
                                map.put("destinationLng", destinationLatlng.longitude);
                            }
                            rRef.updateChildren(map);

                            driverFoundID = driverId;
                            driverFound = true;
                            getDriverLocation();
                            getDriverInfo();
                            getHasRideEnded();
                            if (mRequest != null) mRequest.setText("Ride Requested: Waiting for Ambulance...");
                            Toast.makeText(CustomerMapActivity.this, "Requested ambulance: " + title, Toast.LENGTH_SHORT).show();
                        });
                        String finalPhone = phone;
                        b.setNeutralButton("Call", (d, w) -> {
                            if (finalPhone != null && !finalPhone.isEmpty() && !finalPhone.equals("Not Available")) {
                                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + finalPhone));
                                startActivity(dialIntent);
                            } else Toast.makeText(CustomerMapActivity.this, "Phone not available", Toast.LENGTH_SHORT).show();
                        });
                        b.setNegativeButton("Close", null);
                        b.show();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            return false;

        });

        // show drivers on map ASAP
        showAllAvailableDrivers();
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                mLastLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (mMap != null) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (driverLocationRefListener != null && driverLocationRef != null) {
                driverLocationRef.removeEventListener(driverLocationRefListener);
                driverLocationRefListener = null;
            }
            if (driveHasEndedRefListener != null && driveHasEndedRef != null) {
                driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
                driveHasEndedRefListener = null;
            }
            if (driversAvailableRef != null && driversAvailableRefListener != null) {
                driversAvailableRef.removeEventListener(driversAvailableRefListener);
                driversAvailableRefListener = null;
                driversAvailableRef = null;
            }
            if (currentRideRef != null && currentRideListener != null) {
                currentRideRef.removeEventListener(currentRideListener);
                currentRideRef = null;
                currentRideListener = null;
            }
            if (mFusedLocationClient != null && locationCallback != null) {
                mFusedLocationClient.removeLocationUpdates(locationCallback);
            }
        } catch (Exception ignored) {}
    }
}
