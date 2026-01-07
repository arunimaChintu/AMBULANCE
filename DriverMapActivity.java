//////package saksham.medrescue.saksham;
//////import android.Manifest;
//////import android.app.AlertDialog;
//////import android.content.DialogInterface;
//////import android.content.Intent;
//////import android.content.pm.PackageManager;
//////import android.location.Location;
//////import android.os.Build;
//////import android.os.Bundle;
//////import android.os.Looper;
//////import androidx.annotation.NonNull;
//////import androidx.core.app.ActivityCompat;
//////import androidx.fragment.app.FragmentActivity;
//////import androidx.core.content.ContextCompat;
//////import android.view.View;
//////import android.widget.Button;
//////import android.widget.CompoundButton;
//////import android.widget.LinearLayout;
//////import android.widget.Switch;
//////import android.widget.TextView;
//////import android.widget.Toast;
//////import sachdeva.saksham.medrescue.R; // ✅ CORRECT
//////
//////import com.directions.route.AbstractRouting;
//////import com.directions.route.Route;
//////import com.directions.route.RouteException;
//////import com.directions.route.Routing;
//////import com.directions.route.RoutingListener;
//////import com.firebase.geofire.GeoFire;
//////import com.firebase.geofire.GeoLocation;
//////import com.google.android.gms.location.FusedLocationProviderClient;
//////import com.google.android.gms.location.LocationCallback;
//////import com.google.android.gms.location.LocationRequest;
//////import com.google.android.gms.location.LocationResult;
//////import com.google.android.gms.location.LocationServices;
//////import com.google.android.gms.maps.CameraUpdateFactory;
//////import com.google.android.gms.maps.GoogleMap;
//////import com.google.android.gms.maps.OnMapReadyCallback;
//////import com.google.android.gms.maps.SupportMapFragment;
//////import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//////import com.google.android.gms.maps.model.LatLng;
//////import com.google.android.gms.maps.model.Marker;
//////import com.google.android.gms.maps.model.MarkerOptions;
//////import com.google.android.gms.maps.model.Polyline;
//////import com.google.android.gms.maps.model.PolylineOptions;
//////import com.google.firebase.auth.FirebaseAuth;
//////import com.google.firebase.database.DataSnapshot;
//////import com.google.firebase.database.DatabaseError;
//////import com.google.firebase.database.DatabaseReference;
//////import com.google.firebase.database.FirebaseDatabase;
//////import com.google.firebase.database.ValueEventListener;
//////
//////import java.util.ArrayList;
//////import java.util.HashMap;
//////import java.util.List;
//////import java.util.Map;
//////
//////public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback,RoutingListener {
//////
//////    private GoogleMap mMap;
//////    Location mLastLocation ;
//////    LocationRequest mLocationRequest;
//////    SupportMapFragment mapFragment;
//////    private LatLng myLocation;
//////    private Marker myMarker;
//////
//////    private LinearLayout mCustomerInfo;
//////    private FirebaseAuth mAuth;
//////    private DatabaseReference mCustomerDatabase;
//////
//////    private FusedLocationProviderClient mFusedLoactionClient;
//////
//////    private Button mLogout,mSettings,mrideStatus,mHistory;
//////
//////    private Switch mworkingSwitch;
//////
//////    private int status = 0;
//////
//////    private float rideDistance;
//////
//////    private boolean isLoggingOut = false;
//////
//////    private TextView mCustomerName,mCustomerPhone,mCustomerDestination;
//////    private String customerId="",destination;
//////
//////    private LatLng destinationLatLng;
//////
//////    @Override
//////    protected void onCreate(Bundle savedInstanceState) {
//////        super.onCreate(savedInstanceState);
//////        setContentView(R.layout.activity_driver_map);
//////        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//////
//////        mFusedLoactionClient = LocationServices.getFusedLocationProviderClient(this);
//////
//////        polylines = new ArrayList<>();
//////        mapFragment = (SupportMapFragment) getSupportFragmentManager()
//////                .findFragmentById(R.id.map);
//////
//////        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);
//////
//////        mCustomerName= (TextView) findViewById(R.id.customerName);
//////        mCustomerPhone= (TextView) findViewById(R.id.customerPhone);
//////        mCustomerDestination= (TextView) findViewById(R.id.customerDestination);
//////
//////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//////            ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//////        }else {
//////            mapFragment.getMapAsync(this);
//////        }
//////
//////
//////        mSettings= (Button) findViewById(R.id.settings);
//////        mLogout= (Button) findViewById(R.id.logout);
//////        mrideStatus= (Button) findViewById(R.id.rideStatus);
//////        mworkingSwitch = (Switch) findViewById(R.id.workingSwitch);
//////        mHistory = (Button) findViewById(R.id.history);
//////
//////
//////        mworkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//////            @Override
//////            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//////                if(isChecked){
//////                    connectDriver();
//////                }else{
//////                    disconnectDriver();
//////                }
//////            }
//////        });
//////
//////
//////
//////        mrideStatus.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View v) {
//////                switch(status)
//////                {
//////                    case 1:
//////                        status = 2;
//////                        erasePolylines();
//////                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
//////                            if (mLastLocation == null) return;
//////
//////                            getRouteToMarker(destinationLatLng);
//////                        }
//////                        mrideStatus.setText("Patient Assigned ! Please pickup the patient.");
//////                        break;
//////
//////                    case 2:
//////                        recordRide();
//////                        endRide();
//////                        break;
//////                }
//////            }
//////        });
//////
//////
//////        mLogout.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View v) {
//////                isLoggingOut = true;
//////                disconnectDriver();
//////
//////                FirebaseAuth.getInstance().signOut();
//////                Intent intent=new Intent(DriverMapActivity.this,Welcome_Activity.class);
//////                startActivity(intent);
//////                finish();
//////                return;
//////            }
//////
//////        });
//////        mSettings.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View view) {
//////                Intent intent=new Intent(DriverMapActivity.this,DriverSettingActivity.class);
//////                startActivity(intent);
//////                finish();
//////                return;
//////            }
//////        });
//////
//////        mHistory.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View v) {
//////                Intent intent = new Intent(DriverMapActivity.this, HistoryActivity.class);
//////                intent.putExtra("customerOrDriver", "Drivers");
//////                startActivity(intent);
//////                return;
//////            }
//////        });
//////
//////        getAssignedCustomer();
//////
//////    }
//////
//////
//////    private void endRide()
//////    {
//////
//////        mrideStatus.setText("Pick Patient");
//////        erasePolylines();
//////            String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
//////            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
//////            driverRef.removeValue();
//////
//////
//////        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
//////        GeoFire geoFire = new GeoFire(ref);
//////        geoFire.removeLocation(customerId);
//////        customerId = "";
//////        rideDistance=0;
//////        if( pickupMarker!=null){
//////            pickupMarker.remove();
//////        }
//////
//////    }
//////
//////    private void recordRide(){
//////        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//////        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
//////        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
//////        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("History");
//////        String requestId = historyRef.push().getKey();
//////        driverRef.child(requestId).setValue(true);
//////        customerRef.child(requestId).setValue(true);
//////
//////        HashMap map = new HashMap();
//////        map.put("driver", userId);
//////        map.put("customer", customerId);
//////        map.put("rating", 0);
//////        map.put("timestamp", getCurrentTimestamp());
//////        map.put("destination", destination);
//////        map.put("location/from/lat", myLocation.latitude);
//////        map.put("location/from/lng", myLocation.longitude);
//////        map.put("location/to/lat", destinationLatLng.latitude);
//////        map.put("location/to/lng", destinationLatLng.longitude);
//////        map.put("distance", rideDistance);
//////        historyRef.child(requestId).updateChildren(map);
//////    }
//////    private Long getCurrentTimestamp() {
//////        Long timestamp= System.currentTimeMillis()/1000;
//////        return timestamp;
//////    }
//////
//////
//////    private void getAssignedCustomer(){
//////        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//////        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
//////        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//////                if(dataSnapshot.exists()){
//////                    status = 1;
//////                    customerId = dataSnapshot.getValue().toString();
//////                    getAssignedCustomerPickupLocation();
//////                    getAssignedCustomerDestination();
//////                    getAssignedCustomerInfo();
//////
//////                }else{
//////
//////
//////
//////                    endRide();
//////                }
//////
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError databaseError) {
//////
//////            }
//////        });
//////
//////    }
//////
//////    private void getAssignedCustomerInfo() {
//////
//////        mCustomerInfo.setVisibility(View.VISIBLE);
//////        DatabaseReference mCustomerDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
//////
//////        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//////                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//////                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//////                    if (map.get("Name") != null) {
//////
//////                        mCustomerName.setText(map.get("Name").toString());
//////                    }
//////                    if (map.get("Phone") != null) {
//////
//////                        mCustomerPhone.setText(map.get("Phone").toString());
//////                    }
//////
//////                }
//////
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError databaseError) {
//////
//////            }
//////        });
//////    }
//////
//////    private void getAssignedCustomerDestination(){
//////        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//////        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
//////
//////
//////        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//////                if(dataSnapshot.exists()){
//////                    Map<String , Object> map = (Map<String , Object> ) dataSnapshot.getValue();
//////                    if(map.get("destination")!=null){
//////                        destination = map.get("destination").toString();
//////                        mCustomerDestination.setText("destination: "+destination);
//////                    }
//////                    else{
//////                        mCustomerDestination.setText("destination: ---");
//////                    }
//////                    double destinationLat = 0.0;
//////                    double destinationLng = 0.0;
//////
//////                    if(map.get("destinationLat")!=null){
//////                        destinationLat= Double.valueOf(map.get("destinationLat").toString());
//////                    }
//////
//////                    if(map.get("destinationLng")!=null){
//////                        destinationLng= Double.valueOf(map.get("destinationLng").toString());
//////                        destinationLatLng = new LatLng(destinationLat,destinationLng);
//////                    }
//////
//////                }
//////
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError databaseError) {
//////
//////            }
//////        });
//////
//////    }
//////
//////    Marker pickupMarker;
//////    private DatabaseReference assignedCustomerPickupLocationRef;
//////    private ValueEventListener assignedCustomerPickupLocationRefListener;
//////    private void getAssignedCustomerPickupLocation(){
//////        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
//////        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//////                if(dataSnapshot.exists() && !customerId.equals("")){
//////                    List <Object> map = (List<Object>) dataSnapshot.getValue();
//////                    double LocationLat = 0;
//////                    double LocationLng = 0;
//////                    if(map.get(0)!= null){
//////                        LocationLat = Double.parseDouble(map.get(0).toString());
//////                    }
//////                    if(map.get(1)!= null){
//////                        LocationLng = Double.parseDouble(map.get(1).toString());
//////                    }
//////                    LatLng pickupLatLng = new LatLng(LocationLat, LocationLng);
//////
//////                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));
//////                    getRouteToMarker(pickupLatLng);
//////                }
//////
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError databaseError) {
//////
//////            }
//////        });
//////
//////
//////    }
//////
//////    private void getRouteToMarker(LatLng pickupLatLng) {
//////        Routing routing = new Routing.Builder()
//////                .travelMode(AbstractRouting.TravelMode.DRIVING)
//////                .withListener(this)
//////                .alternativeRoutes(false)
//////                .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), pickupLatLng)
//////                .build();
//////        routing.execute();
//////    }
//////
//////
//////    @Override
//////    public void onMapReady(GoogleMap googleMap) {
//////        mMap = googleMap;
//////
//////        mLocationRequest = new LocationRequest();
//////        mLocationRequest.setInterval(1000);
//////        mLocationRequest.setFastestInterval(1000);
//////        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//////
//////        // Check permission correctly
//////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//////                != PackageManager.PERMISSION_GRANTED
//////                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//////                != PackageManager.PERMISSION_GRANTED) {
//////
//////            ActivityCompat.requestPermissions(
//////                    DriverMapActivity.this,
//////                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//////                    1
//////            );
//////            return;
//////        }
//////
//////        // Permission already granted → start location updates
//////        mFusedLoactionClient.requestLocationUpdates(
//////                mLocationRequest,
//////                mLocationCallback,
//////                Looper.myLooper()
//////        );
//////
//////        mMap.setMyLocationEnabled(true);
//////    }
//////
//////
//////    LocationCallback mLocationCallback = new LocationCallback(){
//////        @Override
//////        public void onLocationResult(LocationResult locationResult) {
//////            for(Location location : locationResult.getLocations()){
//////                if(getApplicationContext()!=null){
//////
//////                    if(!customerId.equals("") && mLastLocation!=null && location != null){
//////                        rideDistance += mLastLocation.distanceTo(location)/1000;
//////                    }
//////                    mLastLocation = location;
//////
//////
//////                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//////                    myLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
//////                    if (myMarker != null) myMarker.remove();
//////                    myMarker = mMap.addMarker(new MarkerOptions()
//////                            .position(myLocation)
//////                            .title("Your Ambulance")
//////                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//////
//////                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//////                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//////
//////                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//////                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
//////                    DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
//////                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
//////                    GeoFire geoFireWorking = new GeoFire(refWorking);
//////
//////                    switch (customerId){
//////                        case "":
//////                            geoFireWorking.removeLocation(userId);
//////                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
//////                            break;
//////
//////                        default:
//////                            geoFireAvailable.removeLocation(userId);
//////                            geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
//////                            break;
//////                    }
//////                }
//////            }
//////        }
//////    };
//////
//////    private void checkLocationPermission() {
//////        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//////            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
//////                new AlertDialog.Builder(this)
//////                        .setTitle("Please give permission...")
//////                        .setMessage("Please give permission...")
//////                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//////                            @Override
//////                            public void onClick(DialogInterface dialog, int which) {
//////                                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//////
//////                            }
//////                        })
//////                        .create()
//////                        .show();
//////            }
//////            else{
//////                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//////
//////            }
//////        }
//////    }
//////
//////    @Override
//////    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//////        switch (requestCode){
//////            case 1:{
//////                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
//////                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
//////                        mFusedLoactionClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//////                        mMap.setMyLocationEnabled(true);
//////                    }
//////                }else {
//////                    Toast.makeText(getApplicationContext(),"Please provide the permission...",Toast.LENGTH_LONG).show();
//////                }
//////                break;
//////            }
//////
//////        }
//////    }
//////    private void connectDriver(){
//////        checkLocationPermission();
//////        mFusedLoactionClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//////        mMap.setMyLocationEnabled(true);
//////
//////    }
//////    private void disconnectDriver(){
//////        if(mFusedLoactionClient!=null){
//////            mFusedLoactionClient.removeLocationUpdates(mLocationCallback);
//////        }
//////
//////        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//////        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");
//////
//////        GeoFire geoFire = new GeoFire(ref);
//////        geoFire.removeLocation(userId);
//////    }
//////
//////
//////
//////    @Override
//////    protected void onStop() {
//////        super.onStop();
//////        if(!isLoggingOut){
//////            disconnectDriver();
//////
//////        }
//////    }
//////    private List<Polyline> polylines;
//////    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
//////    @Override
//////    public void onRoutingFailure(RouteException e) {
//////        if(e != null) {
//////            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//////        }else {
//////            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
//////        }
//////    }
//////    @Override
//////    public void onRoutingStart() {
//////
//////    }
//////
//////    @Override
//////    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
//////        if(polylines.size()>0) {
//////            for (Polyline poly : polylines) {
//////                poly.remove();
//////            }
//////        }
//////
//////        polylines = new ArrayList<>();
//////        //add route(s) to the map.
//////        for (int i = 0; i <route.size(); i++) {
//////
//////            //In case of more than 5 alternative routes
//////            int colorIndex = i % COLORS.length;
//////
//////            PolylineOptions polyOptions = new PolylineOptions();
//////            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
//////            polyOptions.width(10 + i * 3);
//////            polyOptions.addAll(route.get(i).getPoints());
//////            Polyline polyline = mMap.addPolyline(polyOptions);
//////            polylines.add(polyline);
//////
//////            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
//////        }
//////    }
//////
//////    @Override
//////    public void onRoutingCancelled() {
//////
//////    }
//////
//////    private void erasePolylines(){
//////        for(Polyline line : polylines){
//////            line.remove();
//////        }
//////        polylines.clear();
//////    }
//////}
//////package saksham.medrescue.saksham;
//////
//////import android.Manifest;
//////import android.app.AlertDialog;
//////import android.content.DialogInterface;
//////import android.content.Intent;
//////import android.content.pm.PackageManager;
//////import android.location.Location;
//////import android.os.Build;
//////import android.os.Bundle;
//////import android.os.Looper;
//////import androidx.annotation.NonNull;
//////import androidx.core.app.ActivityCompat;
//////import androidx.fragment.app.FragmentActivity;
//////import androidx.core.content.ContextCompat;
//////import android.view.View;
//////import android.widget.Button;
//////import android.widget.CompoundButton;
//////import android.widget.LinearLayout;
//////import android.widget.Switch;
//////import android.widget.TextView;
//////import android.widget.Toast;
//////import sachdeva.saksham.medrescue.R;
//////
//////import com.directions.route.AbstractRouting;
//////import com.directions.route.Route;
//////import com.directions.route.RouteException;
//////import com.directions.route.Routing;
//////import com.directions.route.RoutingListener;
//////import com.firebase.geofire.GeoFire;
//////import com.firebase.geofire.GeoLocation;
//////import com.google.android.gms.location.FusedLocationProviderClient;
//////import com.google.android.gms.location.LocationCallback;
//////import com.google.android.gms.location.LocationRequest;
//////import com.google.android.gms.location.LocationResult;
//////import com.google.android.gms.location.LocationServices;
//////import com.google.android.gms.maps.CameraUpdateFactory;
//////import com.google.android.gms.maps.GoogleMap;
//////import com.google.android.gms.maps.OnMapReadyCallback;
//////import com.google.android.gms.maps.SupportMapFragment;
//////import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//////import com.google.android.gms.maps.model.LatLng;
//////import com.google.android.gms.maps.model.Marker;
//////import com.google.android.gms.maps.model.MarkerOptions;
//////import com.google.android.gms.maps.model.Polyline;
//////import com.google.android.gms.maps.model.PolylineOptions;
//////import com.google.firebase.auth.FirebaseAuth;
//////import com.google.firebase.auth.FirebaseUser;
//////import com.google.firebase.database.DataSnapshot;
//////import com.google.firebase.database.DatabaseError;
//////import com.google.firebase.database.DatabaseReference;
//////import com.google.firebase.database.FirebaseDatabase;
//////import com.google.firebase.database.ValueEventListener;
//////
//////import java.util.ArrayList;
//////import java.util.HashMap;
//////import java.util.List;
//////import java.util.Map;
//////
//////public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {
//////
//////    private GoogleMap mMap;
//////    Location mLastLocation;
//////    LocationRequest mLocationRequest;
//////    SupportMapFragment mapFragment;
//////    private LatLng myLocation;
//////    private Marker myMarker;
//////
//////    private LinearLayout mCustomerInfo;
//////    private FirebaseAuth mAuth;
//////    private DatabaseReference mCustomerDatabase;
//////
//////    private FusedLocationProviderClient mFusedLoactionClient;
//////
//////    private Button mLogout, mSettings, mrideStatus, mHistory;
//////
//////    private Switch mworkingSwitch;
//////
//////    private int status = 0;
//////
//////    private float rideDistance;
//////
//////    private boolean isLoggingOut = false;
//////
//////    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;
//////    private String customerId = "", destination;
//////
//////    private LatLng destinationLatLng;
//////
//////    // defensive flags
//////    private boolean isDemoNoUser = false;   // true when there's no Firebase user (demo mode)
//////
//////    @Override
//////    protected void onCreate(Bundle savedInstanceState) {
//////        super.onCreate(savedInstanceState);
//////        setContentView(R.layout.activity_driver_map);
//////
//////        // init firebase auth and check user
//////        mAuth = FirebaseAuth.getInstance();
//////        FirebaseUser currentUser = mAuth.getCurrentUser();
//////        if (currentUser == null) {
//////            // Demo/no-login mode — avoid calling Firebase getUid() etc.
//////            isDemoNoUser = true;
//////            Toast.makeText(this, "Demo mode: no user signed in. Map will work, Firebase features disabled.", Toast.LENGTH_LONG).show();
//////        } else {
//////            isDemoNoUser = false;
//////        }
//////
//////        mFusedLoactionClient = LocationServices.getFusedLocationProviderClient(this);
//////
//////        polylines = new ArrayList<>();
//////        mapFragment = (SupportMapFragment) getSupportFragmentManager()
//////                .findFragmentById(R.id.map);
//////
//////        // Bind UI (your layout already contains these IDs)
//////        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);
//////        mCustomerName = (TextView) findViewById(R.id.customerName);
//////        mCustomerPhone = (TextView) findViewById(R.id.customerPhone);
//////        mCustomerDestination = (TextView) findViewById(R.id.customerDestination);
//////
//////        // Request permission or load the map
//////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//////                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//////            ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//////        } else {
//////            if (mapFragment != null) {
//////                mapFragment.getMapAsync(this);
//////            } else {
//////                Toast.makeText(this, "Map fragment not found.", Toast.LENGTH_SHORT).show();
//////            }
//////        }
//////
//////        // Buttons and switch (layout provides them)
//////        mSettings = (Button) findViewById(R.id.settings);
//////        mLogout = (Button) findViewById(R.id.logout);
//////        mrideStatus = (Button) findViewById(R.id.rideStatus);
//////        mworkingSwitch = (Switch) findViewById(R.id.workingSwitch);
//////        mHistory = (Button) findViewById(R.id.history);
//////
//////        // protect listeners in case some views are missing
//////        if (mworkingSwitch != null) {
//////            mworkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//////                @Override
//////                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//////                    if (isChecked) {
//////                        connectDriver();
//////                    } else {
//////                        disconnectDriver();
//////                    }
//////                }
//////            });
//////        }
//////
//////        if (mrideStatus != null) {
//////            mrideStatus.setOnClickListener(new View.OnClickListener() {
//////                @Override
//////                public void onClick(View v) {
//////                    switch (status) {
//////                        case 1:
//////                            status = 2;
//////                            erasePolylines();
//////                            if (destinationLatLng != null && mLastLocation != null) {
//////                                getRouteToMarker(destinationLatLng);
//////                            }
//////                            mrideStatus.setText("Patient Assigned ! Please pickup the patient.");
//////                            break;
//////
//////                        case 2:
//////                            recordRide();
//////                            endRide();
//////                            break;
//////                    }
//////                }
//////            });
//////        }
//////
//////        if (mLogout != null) {
//////            mLogout.setOnClickListener(new View.OnClickListener() {
//////                @Override
//////                public void onClick(View v) {
//////                    isLoggingOut = true;
//////                    disconnectDriver();
//////
//////                    // sign out only if user exists
//////                    if (!isDemoNoUser) {
//////                        FirebaseAuth.getInstance().signOut();
//////                    }
//////                    Intent intent = new Intent(DriverMapActivity.this, Welcome_Activity.class);
//////                    startActivity(intent);
//////                    finish();
//////                }
//////            });
//////        }
//////
//////        if (mSettings != null) {
//////            mSettings.setOnClickListener(new View.OnClickListener() {
//////                @Override
//////                public void onClick(View view) {
//////                    Intent intent = new Intent(DriverMapActivity.this, DriverSettingActivity.class);
//////                    startActivity(intent);
//////                    finish();
//////                }
//////            });
//////        }
//////
//////        if (mHistory != null) {
//////            mHistory.setOnClickListener(new View.OnClickListener() {
//////                @Override
//////                public void onClick(View v) {
//////                    Intent intent = new Intent(DriverMapActivity.this, HistoryActivity.class);
//////                    intent.putExtra("customerOrDriver", "Drivers");
//////                    startActivity(intent);
//////                }
//////            });
//////        }
//////
//////        // Only try to read assigned customer if we have a signed-in user
//////        if (!isDemoNoUser) {
//////            try {
//////                getAssignedCustomer();
//////            } catch (Exception e) {
//////                // protect against any unexpected NPE from Firebase
//////                e.printStackTrace();
//////            }
//////        }
//////    }
//////
//////    private void endRide() {
//////        if (mrideStatus != null) mrideStatus.setText("Pick Patient");
//////        erasePolylines();
//////
//////        if (isDemoNoUser) {
//////            // nothing to remove in demo mode
//////            customerId = "";
//////            rideDistance = 0;
//////            if (pickupMarker != null) pickupMarker.remove();
//////            return;
//////        }
//////
//////        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//////        if (user == null) return;
//////
//////        String userId = user.getUid();
//////        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
//////        driverRef.removeValue();
//////
//////        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
//////        GeoFire geoFire = new GeoFire(ref);
//////        geoFire.removeLocation(customerId);
//////        customerId = "";
//////        rideDistance = 0;
//////        if (pickupMarker != null) {
//////            pickupMarker.remove();
//////        }
//////    }
//////
//////    private void recordRide() {
//////        if (isDemoNoUser) return;
//////
//////        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//////        if (user == null) return;
//////
//////        String userId = user.getUid();
//////        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
//////        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
//////        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("History");
//////        String requestId = historyRef.push().getKey();
//////        if (requestId == null) return;
//////        driverRef.child(requestId).setValue(true);
//////        customerRef.child(requestId).setValue(true);
//////
//////        HashMap map = new HashMap();
//////        map.put("driver", userId);
//////        map.put("customer", customerId);
//////        map.put("rating", 0);
//////        map.put("timestamp", getCurrentTimestamp());
//////        map.put("destination", destination);
//////        if (myLocation != null) {
//////            map.put("location/from/lat", myLocation.latitude);
//////            map.put("location/from/lng", myLocation.longitude);
//////        }
//////        if (destinationLatLng != null) {
//////            map.put("location/to/lat", destinationLatLng.latitude);
//////            map.put("location/to/lng", destinationLatLng.longitude);
//////        }
//////        map.put("distance", rideDistance);
//////        historyRef.child(requestId).updateChildren(map);
//////    }
//////
//////    private Long getCurrentTimestamp() {
//////        Long timestamp = System.currentTimeMillis() / 1000;
//////        return timestamp;
//////    }
//////
//////    private void getAssignedCustomer() {
//////        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//////        if (user == null) return;
//////
//////        String driverId = user.getUid();
//////        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
//////        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//////                try {
//////                    if (dataSnapshot.exists()) {
//////                        status = 1;
//////                        customerId = dataSnapshot.getValue().toString();
//////                        getAssignedCustomerPickupLocation();
//////                        getAssignedCustomerDestination();
//////                        getAssignedCustomerInfo();
//////                    } else {
//////                        endRide();
//////                    }
//////                } catch (Exception e) {
//////                    e.printStackTrace();
//////                }
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError databaseError) {
//////
//////            }
//////        });
//////
//////    }
//////
//////    private void getAssignedCustomerInfo() {
//////
//////        if (mCustomerInfo != null) mCustomerInfo.setVisibility(View.VISIBLE);
//////
//////        if (isDemoNoUser) return;
//////
//////        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
//////
//////        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//////                try {
//////                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//////                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//////                        if (map.get("Name") != null) {
//////                            mCustomerName.setText(map.get("Name").toString());
//////                        }
//////                        if (map.get("Phone") != null) {
//////                            mCustomerPhone.setText(map.get("Phone").toString());
//////                        }
//////                    }
//////                } catch (Exception e) {
//////                    e.printStackTrace();
//////                }
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError databaseError) {
//////
//////            }
//////        });
//////    }
//////
//////    private void getAssignedCustomerDestination() {
//////        if (isDemoNoUser) return;
//////
//////        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//////        if (user == null) return;
//////
//////        String driverId = user.getUid();
//////        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
//////
//////        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//////                try {
//////                    if (dataSnapshot.exists()) {
//////                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//////                        if (map.get("destination") != null) {
//////                            destination = map.get("destination").toString();
//////                            if (mCustomerDestination != null) mCustomerDestination.setText("destination: " + destination);
//////                        } else {
//////                            if (mCustomerDestination != null) mCustomerDestination.setText("destination: ---");
//////                        }
//////                        double destinationLat = 0.0;
//////                        double destinationLng = 0.0;
//////
//////                        if (map.get("destinationLat") != null) {
//////                            destinationLat = Double.valueOf(map.get("destinationLat").toString());
//////                        }
//////
//////                        if (map.get("destinationLng") != null) {
//////                            destinationLng = Double.valueOf(map.get("destinationLng").toString());
//////                            destinationLatLng = new LatLng(destinationLat, destinationLng);
//////                        }
//////                    }
//////                } catch (Exception e) {
//////                    e.printStackTrace();
//////                }
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError databaseError) {
//////
//////            }
//////        });
//////
//////    }
//////
//////    Marker pickupMarker;
//////    private DatabaseReference assignedCustomerPickupLocationRef;
//////    private ValueEventListener assignedCustomerPickupLocationRefListener;
//////
//////    private void getAssignedCustomerPickupLocation() {
//////        if (isDemoNoUser) return;
//////
//////        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
//////        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//////                try {
//////                    if (dataSnapshot.exists() && !customerId.equals("")) {
//////                        List<Object> map = (List<Object>) dataSnapshot.getValue();
//////                        double LocationLat = 0;
//////                        double LocationLng = 0;
//////                        if (map.get(0) != null) {
//////                            LocationLat = Double.parseDouble(map.get(0).toString());
//////                        }
//////                        if (map.get(1) != null) {
//////                            LocationLng = Double.parseDouble(map.get(1).toString());
//////                        }
//////                        LatLng pickupLatLng = new LatLng(LocationLat, LocationLng);
//////
//////                        if (pickupMarker != null) pickupMarker.remove();
//////                        if (mMap != null) {
//////                            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));
//////                            getRouteToMarker(pickupLatLng);
//////                        }
//////                    }
//////                } catch (Exception e) {
//////                    e.printStackTrace();
//////                }
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError databaseError) {
//////
//////            }
//////        });
//////
//////
//////    }
//////
//////    private void getRouteToMarker(LatLng pickupLatLng) {
//////        if (mLastLocation == null || pickupLatLng == null) return;
//////
//////        Routing routing = new Routing.Builder()
//////                .travelMode(AbstractRouting.TravelMode.DRIVING)
//////                .withListener(this)
//////                .alternativeRoutes(false)
//////                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
//////                .build();
//////        routing.execute();
//////    }
//////
//////    @Override
//////    public void onMapReady(GoogleMap googleMap) {
//////        mMap = googleMap;
//////
//////        mLocationRequest = new LocationRequest();
//////        mLocationRequest.setInterval(1000);
//////        mLocationRequest.setFastestInterval(1000);
//////        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//////
//////        // Check permission correctly and request if not present
//////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//////                != PackageManager.PERMISSION_GRANTED
//////                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//////                != PackageManager.PERMISSION_GRANTED) {
//////
//////            ActivityCompat.requestPermissions(
//////                    DriverMapActivity.this,
//////                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//////                    1
//////            );
//////            return;
//////        }
//////
//////        // Permission already granted → start location updates
//////        try {
//////            mFusedLoactionClient.requestLocationUpdates(
//////                    mLocationRequest,
//////                    mLocationCallback,
//////                    Looper.myLooper()
//////            );
//////
//////            if (mMap != null) mMap.setMyLocationEnabled(true);
//////        } catch (SecurityException se) {
//////            se.printStackTrace();
//////        }
//////    }
//////
//////
//////    LocationCallback mLocationCallback = new LocationCallback() {
//////        @Override
//////        public void onLocationResult(LocationResult locationResult) {
//////            for (Location location : locationResult.getLocations()) {
//////                if (getApplicationContext() != null) {
//////
//////                    if (!customerId.equals("") && mLastLocation != null && location != null) {
//////                        rideDistance += mLastLocation.distanceTo(location) / 1000;
//////                    }
//////                    mLastLocation = location;
//////
//////                    if (mLastLocation == null) continue;
//////
//////                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//////                    myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//////
//////                    if (myMarker != null) myMarker.remove();
//////
//////                    if (mMap != null) {
//////                        try {
//////                            myMarker = mMap.addMarker(new MarkerOptions()
//////                                    .position(myLocation)
//////                                    .title("Your Ambulance")
//////                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//////                        } catch (Exception e) {
//////                            // resource missing or map issue
//////                            e.printStackTrace();
//////                        }
//////                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//////                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//////                    }
//////
//////                    // GeoFire updates only if user is signed in
//////                    if (!isDemoNoUser) {
//////                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//////                        if (user != null) {
//////                            String userId = user.getUid();
//////                            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
//////                            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
//////                            GeoFire geoFireAvailable = new GeoFire(refAvailable);
//////                            GeoFire geoFireWorking = new GeoFire(refWorking);
//////
//////                            if (customerId == null || customerId.equals("")) {
//////                                geoFireWorking.removeLocation(userId);
//////                                geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
//////                            } else {
//////                                geoFireAvailable.removeLocation(userId);
//////                                geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
//////                            }
//////                        }
//////                    }
//////                }
//////            }
//////        }
//////    };
//////
//////    private void checkLocationPermission() {
//////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//////            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//////                new AlertDialog.Builder(this)
//////                        .setTitle("Please give permission...")
//////                        .setMessage("Please give permission...")
//////                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//////                            @Override
//////                            public void onClick(DialogInterface dialog, int which) {
//////                                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//////
//////                            }
//////                        })
//////                        .create()
//////                        .show();
//////            } else {
//////                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//////
//////            }
//////        }
//////    }
//////
//////    @Override
//////    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//////        switch (requestCode) {
//////            case 1: {
//////                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//////                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//////                        try {
//////                            mFusedLoactionClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//////                            if (mMap != null) mMap.setMyLocationEnabled(true);
//////                        } catch (SecurityException se) {
//////                            se.printStackTrace();
//////                        }
//////                    }
//////                } else {
//////                    Toast.makeText(getApplicationContext(), "Please provide the permission...", Toast.LENGTH_LONG).show();
//////                }
//////                break;
//////            }
//////
//////        }
//////    }
//////
//////    private void connectDriver() {
//////        checkLocationPermission();
//////        try {
//////            mFusedLoactionClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//////            if (mMap != null) mMap.setMyLocationEnabled(true);
//////        } catch (SecurityException se) {
//////            se.printStackTrace();
//////        }
//////    }
//////
//////    private void disconnectDriver() {
//////        if (mFusedLoactionClient != null) {
//////            mFusedLoactionClient.removeLocationUpdates(mLocationCallback);
//////        }
//////
//////        if (isDemoNoUser) {
//////            // nothing to remove for demo
//////            return;
//////        }
//////
//////        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//////        if (user == null) return;
//////        String userId = user.getUid();
//////        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");
//////
//////        GeoFire geoFire = new GeoFire(ref);
//////        geoFire.removeLocation(userId);
//////    }
//////
//////    @Override
//////    protected void onStop() {
//////        super.onStop();
//////        if (!isLoggingOut) {
//////            disconnectDriver();
//////        }
//////    }
//////
//////    private List<Polyline> polylines;
//////    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
//////
//////    @Override
//////    public void onRoutingFailure(RouteException e) {
//////        if (e != null) {
//////            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//////        } else {
//////            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
//////        }
//////    }
//////
//////    @Override
//////    public void onRoutingStart() {
//////
//////    }
//////
//////    @Override
//////    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
//////        if (polylines.size() > 0) {
//////            for (Polyline poly : polylines) {
//////                poly.remove();
//////            }
//////        }
//////
//////        polylines = new ArrayList<>();
//////        // add route(s) to the map.
//////        for (int i = 0; i < route.size(); i++) {
//////
//////            // In case of more than 5 alternative routes
//////            int colorIndex = i % COLORS.length;
//////
//////            PolylineOptions polyOptions = new PolylineOptions();
//////            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
//////            polyOptions.width(10 + i * 3);
//////            polyOptions.addAll(route.get(i).getPoints());
//////            Polyline polyline = mMap.addPolyline(polyOptions);
//////            polylines.add(polyline);
//////
//////            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
//////        }
//////    }
//////
//////    @Override
//////    public void onRoutingCancelled() {
//////
//////    }
//////
//////    private void erasePolylines() {
//////        for (Polyline line : polylines) {
//////            line.remove();
//////        }
//////        polylines.clear();
//////    }
//////}
////package saksham.medrescue.saksham;
////
////import android.Manifest;
////import android.content.DialogInterface;
////import android.content.Intent;
////import android.content.pm.PackageManager;
////import android.location.Location;
////import android.os.Bundle;
////import android.os.Handler;
////import android.os.Looper;
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AlertDialog;
////import androidx.core.app.ActivityCompat;
////import androidx.fragment.app.FragmentActivity;
////import androidx.core.content.ContextCompat;
////import android.view.View;
////import android.widget.Button;
////import android.widget.CompoundButton;
////import android.widget.LinearLayout;
////import android.widget.Switch;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import sachdeva.saksham.medrescue.R;
////
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
////import com.google.android.gms.maps.model.Polyline;
////import com.google.android.gms.maps.model.PolylineOptions;
////
////import java.util.ArrayList;
////import java.util.List;
////
/////**
//// * Demo-only DriverMapActivity (Option B)
//// * - Always runs in demo mode (no Firebase)
//// * - Simulates a customer request, accept flow, route and pickup
//// * - Safe, stable for presentation
//// */
////public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback {
////
////    private GoogleMap mMap;
////    private FusedLocationProviderClient mFusedLocationClient;
////    private LocationRequest mLocationRequest;
////    private Location mLastLocation;
////
////    private Marker driverMarker;
////    private Marker pickupMarker;
////
////    private Button mLogout, mSettings, mrideStatus, mHistory;
////    private Switch mworkingSwitch;
////    private LinearLayout mCustomerInfo;
////    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;
////
////    private boolean demoRequestActive = false;
////    private boolean demoRideAccepted = false;
////    private LatLng demoPickupLatLng;
////    private List<Polyline> polylines = new ArrayList<>();
////
////    private final Handler handler = new Handler(Looper.getMainLooper());
////
////    @Override
//////    protected void onCreate(Bundle savedInstanceState) {
//////        Toast.makeText(this, "DriverMap started", Toast.LENGTH_SHORT).show();
//////
//////        super.onCreate(savedInstanceState);
//////        setContentView(R.layout.activity_driver_map);
//////
//////        // FORCE DEMO MODE (no Firebase at all)
//////        Toast.makeText(this, "Demo Mode ON (Firebase bypassed)", Toast.LENGTH_SHORT).show();
//////
//////        // init views
//////        mLogout = findViewById(R.id.logout);
//////        mSettings = findViewById(R.id.settings);
//////        mrideStatus = findViewById(R.id.rideStatus);
//////        mworkingSwitch = findViewById(R.id.workingSwitch);
//////        mHistory = findViewById(R.id.history);
//////
//////        mCustomerInfo = findViewById(R.id.customerInfo);
//////        mCustomerName = findViewById(R.id.customerName);
//////        mCustomerPhone = findViewById(R.id.customerPhone);
//////        mCustomerDestination = findViewById(R.id.customerDestination);
//////
//////        // initial UI state
//////        if (mCustomerInfo != null) mCustomerInfo.setVisibility(View.GONE);
//////        if (mrideStatus != null) mrideStatus.setText("Waiting for request");
//////
//////        // location client
//////        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//////
//////        // map fragment
//////        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//////
//////        // request permission or start map
//////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//////            ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//////        } else {
//////            if (mapFragment != null) mapFragment.getMapAsync(this);
//////        }
//////
//////        // listeners
//////        if (mworkingSwitch != null) {
//////            mworkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//////                @Override
//////                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//////                    if (isChecked) {
//////                        startLocationUpdates();
//////                    } else {
//////                        stopLocationUpdates();
//////                    }
//////                }
//////            });
//////        }
//////
//////        if (mLogout != null) {
//////            mLogout.setOnClickListener(v -> {
//////                stopLocationUpdates();
//////                // go back to welcome for demo
//////                startActivity(new Intent(DriverMapActivity.this, Welcome_Activity.class));
//////                finish();
//////            });
//////        }
//////
//////        if (mSettings != null) {
//////            mSettings.setOnClickListener(v -> {
//////                startActivity(new Intent(DriverMapActivity.this, DriverSettingActivity.class));
//////            });
//////        }
//////
//////        if (mHistory != null) {
//////            mHistory.setOnClickListener(v -> {
//////                startActivity(new Intent(DriverMapActivity.this, HistoryActivity.class));
//////            });
//////        }
//////
//////        if (mrideStatus != null) {
//////            mrideStatus.setOnClickListener(v -> {
//////                // If there is an active demo request, accept it. If accepted, second click ends ride.
//////                if (!demoRequestActive) {
//////                    // no request to accept — create a simulated one manually
//////                    createDemoRequestManually();
//////                } else if (!demoRideAccepted) {
//////                    acceptDemoRequest();
//////                } else {
//////                    // if already on ride -> finish it
//////                    completeDemoRide();
//////                }
//////            });
//////        }
//////    }
////
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        Toast.makeText(this, "DriverMap started", Toast.LENGTH_SHORT).show();
////        setContentView(R.layout.activity_driver_map);
////
////        // FORCE DEMO MODE (no Firebase at all)
////        Toast.makeText(this, "Demo Mode ON (Firebase bypassed)", Toast.LENGTH_SHORT).show();
////
////        // init views
////        mLogout = findViewById(R.id.logout);
////        mSettings = findViewById(R.id.settings);
////        mrideStatus = findViewById(R.id.rideStatus);
////        mworkingSwitch = findViewById(R.id.workingSwitch);
////        mHistory = findViewById(R.id.history);
////
////        mCustomerInfo = findViewById(R.id.customerInfo);
////        mCustomerName = findViewById(R.id.customerName);
////        mCustomerPhone = findViewById(R.id.customerPhone);
////        mCustomerDestination = findViewById(R.id.customerDestination);
////
////        if (mCustomerInfo != null) mCustomerInfo.setVisibility(View.GONE);
////        if (mrideStatus != null) mrideStatus.setText("Waiting for request");
////
////        // location client
////        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
////
////        // --- ALWAYS attach map async regardless of permission state ---
////        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
////        if (mapFragment != null) {
////            mapFragment.getMapAsync(this);
////        } else {
////            Toast.makeText(this, "Map fragment missing!", Toast.LENGTH_LONG).show();
////        }
////
////        // listeners (unchanged)
////        if (mworkingSwitch != null) {
////            mworkingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
////                if (isChecked) startLocationUpdates();
////                else stopLocationUpdates();
////            });
////        }
////
////        if (mLogout != null) {
////            mLogout.setOnClickListener(v -> {
////                stopLocationUpdates();
////                startActivity(new Intent(DriverMapActivity.this, Welcome_Activity.class));
////                finish();
////            });
////        }
////        if (mSettings != null) mSettings.setOnClickListener(v -> startActivity(new Intent(DriverMapActivity.this, DriverSettingActivity.class)));
////        if (mHistory != null) mHistory.setOnClickListener(v -> startActivity(new Intent(DriverMapActivity.this, HistoryActivity.class)));
////        if (mrideStatus != null) {
////            mrideStatus.setOnClickListener(v -> {
////                if (!demoRequestActive) createDemoRequestManually();
////                else if (!demoRideAccepted) acceptDemoRequest();
////                else completeDemoRide();
////            });
////        }
////    }
////
////    // Map ready
////    @Override
//////    public void onMapReady(GoogleMap googleMap) {
//////
//////        Toast.makeText(this, "Map is ready!", Toast.LENGTH_SHORT).show();
//////
//////        mMap = googleMap;
//////        LatLng india = new LatLng(28.6139, 77.2090); // Delhi sample
//////        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 15));
//////
//////        // prepare location request
//////        mLocationRequest = LocationRequest.create();
//////        mLocationRequest.setInterval(2000);
//////        mLocationRequest.setFastestInterval(1000);
//////        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//////
//////        // start location updates for demo by default if permission granted
//////        startLocationUpdates();
//////
//////        // after map is ready, spawn a simulated customer request after a small delay
//////        handler.postDelayed(this::spawnSimulatedRequest, 2500);
//////    }
////
////    public void onMapReady(GoogleMap googleMap) {
////        Toast.makeText(this, "Map is ready!", Toast.LENGTH_SHORT).show();
////        mMap = googleMap;
////
////        // Extra safety: set UI settings so map must render tiles
////        try {
////            mMap.getUiSettings().setZoomControlsEnabled(true);
////            mMap.getUiSettings().setCompassEnabled(true);
////            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////        // Move camera to a definite place so user sees map tiles even without location
////        LatLng fallback = new LatLng(20.5937, 78.9629); // India center
////        try {
////            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallback, 5f));
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////        // check location permission and enable my-location layer if allowed
////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
////                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
////            try {
////                mMap.setMyLocationEnabled(true);
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////            // create location request now
////            mLocationRequest = LocationRequest.create();
////            mLocationRequest.setInterval(2000);
////            mLocationRequest.setFastestInterval(1000);
////            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
////            startLocationUpdates();
////        } else {
////            // ask for permission; when granted onRequestPermissionsResult will call getMapAsync again
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
////        }
////
////        // finally spawn demo request shortly after map appears
////        handler.postDelayed(this::spawnSimulatedRequest, 2500);
////    }
////
////    // start fused location updates
////    private void startLocationUpdates() {
////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            return;
////        }
////
////        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
////            @Override
////            public void onLocationResult(@NonNull LocationResult locationResult) {
////                if (locationResult == null) return;
////
////                Location loc = locationResult.getLastLocation();
////                if (loc == null) return;
////                mLastLocation = loc;
////                LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
////
////                // update driver marker
////                if (driverMarker != null) {
////                    driverMarker.setPosition(pos);
////                } else if (mMap != null) {
////                    try {
////                        driverMarker = mMap.addMarker(new MarkerOptions()
////                                .position(pos)
////                                .title("Your Ambulance")
////                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
////                    } catch (Exception e) {
////                        // resource missing
////                        driverMarker = mMap.addMarker(new MarkerOptions().position(pos).title("Ambulance"));
////                    }
////                }
////
////                // move camera once
////                if (mMap != null) {
////                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f));
////                }
////            }
////        }, Looper.getMainLooper());
////    }
////
////    private void stopLocationUpdates() {
////        if (mFusedLocationClient != null) {
////            mFusedLocationClient.removeLocationUpdates(locationCallback);
////        }
////    }
////
////    private final LocationCallback locationCallback = new LocationCallback() {
////        // not used because we used inline callback above for simplicity
////    };
////
////    // Creates a simulated request near the driver's current location
////    private void spawnSimulatedRequest() {
////        if (mLastLocation == null || mMap == null || demoRequestActive) {
////            // try again shortly if location not yet available
////            handler.postDelayed(this::spawnSimulatedRequest, 1500);
////            return;
////        }
////
////        // place pickup slightly offset from driver (approx 500-800 meters)
////        double lat = mLastLocation.getLatitude();
////        double lng = mLastLocation.getLongitude();
////        double offsetLat = 0.004 + Math.random() * 0.006;   // ~400-600m
////        double offsetLng = 0.004 + Math.random() * 0.006;
////
////        demoPickupLatLng = new LatLng(lat + offsetLat, lng + offsetLng);
////
////        // create pickup marker
////        if (pickupMarker != null) pickupMarker.remove();
////        try {
////            pickupMarker = mMap.addMarker(new MarkerOptions()
////                    .position(demoPickupLatLng)
////                    .title("Patient pickup")
////                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));
////        } catch (Exception e) {
////            pickupMarker = mMap.addMarker(new MarkerOptions().position(demoPickupLatLng).title("Patient pickup"));
////        }
////
////        // show customer info panel
////        if (mCustomerInfo != null) {
////            mCustomerInfo.setVisibility(View.VISIBLE);
////            if (mCustomerName != null) mCustomerName.setText("Patient: Demo Person");
////            if (mCustomerPhone != null) mCustomerPhone.setText("Phone: +91-98765-43210");
////            if (mCustomerDestination != null) mCustomerDestination.setText("Destination: Nearby Hospital");
////        }
////
////        demoRequestActive = true;
////        demoRideAccepted = false;
////        if (mrideStatus != null) mrideStatus.setText("Accept Request");
////        Toast.makeText(this, "New patient request received (demo).", Toast.LENGTH_SHORT).show();
////
////        // optionally blink camera to pickup
////        handler.postDelayed(() -> {
////            if (mMap != null && demoPickupLatLng != null) {
////                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(demoPickupLatLng, 14f));
////                handler.postDelayed(() -> {
////                    if (mMap != null && mLastLocation != null)
////                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 13f));
////                }, 1600);
////            }
////        }, 400);
////    }
////
////    // manual creation (if driver pressed button when no request)
////    private void createDemoRequestManually() {
////        spawnSimulatedRequest();
////    }
////
////    // accept the demo request
////    private void acceptDemoRequest() {
////        if (!demoRequestActive || demoPickupLatLng == null || mLastLocation == null) {
////            Toast.makeText(this, "No request or location not available yet.", Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        demoRideAccepted = true;
////        if (mrideStatus != null) mrideStatus.setText("En route to pickup");
////
////        // draw a simple straight polyline between driver and pickup
////        drawRouteBetween(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), demoPickupLatLng);
////
////        // simulate movement toward pickup: every 1s move driver marker a bit closer
////        simulateDriveToPickup();
////    }
////
////    private void drawRouteBetween(LatLng from, LatLng to) {
////        clearPolylines();
////        PolylineOptions opts = new PolylineOptions().add(from).add(to).width(8f);
////        try {
////            opts.color(getResources().getColor(R.color.primary_dark_material_light));
////        } catch (Exception e) {
////            // ignore color issues
////        }
////        Polyline p = mMap.addPolyline(opts);
////        polylines.add(p);
////    }
////
////    private void clearPolylines() {
////        for (Polyline pl : polylines) {
////            try { pl.remove(); } catch (Exception e) { /* ignore */ }
////        }
////        polylines.clear();
////    }
////
////    // simulate moving towards pickup with steps
////    private void simulateDriveToPickup() {
////        if (mLastLocation == null || demoPickupLatLng == null) return;
////
////        final LatLng start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
////        final LatLng end = demoPickupLatLng;
////
////        final int steps = 20;
////        final long stepDelay = 700; // ms
////
////        final double latStep = (end.latitude - start.latitude) / steps;
////        final double lngStep = (end.longitude - start.longitude) / steps;
////
////        handler.post(new Runnable() {
////            int i = 1;
////            @Override
////            public void run() {
////                double newLat = start.latitude + latStep * i;
////                double newLng = start.longitude + lngStep * i;
////                LatLng newPos = new LatLng(newLat, newLng);
////
////                // move driver marker
////                if (driverMarker != null) driverMarker.setPosition(newPos);
////                if (mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPos, 14f));
////
////                // update polyline to use new position -> simple redraw
////                drawRouteBetween(newPos, end);
////
////                i++;
////                if (i <= steps) {
////                    handler.postDelayed(this, stepDelay);
////                } else {
////                    // arrived
////                    onArrivedAtPickup();
////                }
////            }
////        });
////    }
////
////    private void onArrivedAtPickup() {
////        if (mrideStatus != null) mrideStatus.setText("Pickup Complete - End Ride");
////        Toast.makeText(this, "Arrived at pickup (demo).", Toast.LENGTH_SHORT).show();
////
////        // simulate small wait then complete ride automatically
////        handler.postDelayed(this::completeDemoRide, 2000);
////    }
////
////    private void completeDemoRide() {
////        // clear marker & UI
////        if (pickupMarker != null) {
////            try { pickupMarker.remove(); } catch (Exception e) { /* ignore */ }
////            pickupMarker = null;
////        }
////        clearPolylines();
////        demoRequestActive = false;
////        demoRideAccepted = false;
////
////        if (mCustomerInfo != null) mCustomerInfo.setVisibility(View.GONE);
////        if (mrideStatus != null) mrideStatus.setText("Waiting for request");
////        Toast.makeText(this, "Demo ride finished.", Toast.LENGTH_SHORT).show();
////
////        // move camera back to driver location
////        if (mLastLocation != null && mMap != null) {
////            LatLng pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
////            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f));
////        }
////
////        // spawn another request automatically after a short delay to demonstrate repeated flow
////        handler.postDelayed(this::spawnSimulatedRequest, 4000);
////    }
////
////    // permission result
////    @Override
////    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        if (requestCode == 1) {
////            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
////                if (mapFragment != null) mapFragment.getMapAsync(this);
////                startLocationUpdates();
////            } else {
////                // permission denied: show friendly dialog
////                new AlertDialog.Builder(this)
////                        .setTitle("Location permission required")
////                        .setMessage("This demo uses location. Please grant permission in settings or allow to continue.")
////                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
////                        .show();
////            }
////        } else {
////            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        }
////    }
////
////    @Override
////    protected void onStop() {
////        super.onStop();
////        stopLocationUpdates();
////        handler.removeCallbacksAndMessages(null);
////    }
////}
//package saksham.medrescue.saksham;
//
//import android.Manifest;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.FragmentActivity;
//import androidx.core.content.ContextCompat;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.LinearLayout;
//import android.widget.Switch;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import sachdeva.saksham.medrescue.R;
//
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
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.database.Transaction;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Demo-only DriverMapActivity (Option B)
// * - Always runs in demo mode (no Firebase) OR partly-Firebase when driver logged in
// * - Simulates a customer request, accept flow, route and pickup
// * - Adds Firebase notification listening + FCFS accept transaction
// */
//public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//    private FusedLocationProviderClient mFusedLocationClient;
//    private LocationRequest mLocationRequest;
//    private Location mLastLocation;
//
//    private Marker driverMarker;
//    private Marker pickupMarker;
//
//    private Button mLogout, mSettings, mrideStatus, mHistory;
//    private Switch mworkingSwitch;
//    private LinearLayout mCustomerInfo;
//    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;
//
//    private boolean demoRequestActive = false;
//    private boolean demoRideAccepted = false;
//    private LatLng demoPickupLatLng;
//    private List<Polyline> polylines = new ArrayList<>();
//
//    private final Handler handler = new Handler(Looper.getMainLooper());
//
//    // --- NEW: Firebase notification/listener fields ---
//    private DatabaseReference driverNotifRef = null;
//    private ChildEventListener driverNotifListener = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Toast.makeText(this, "DriverMap started", Toast.LENGTH_SHORT).show();
//        setContentView(R.layout.activity_driver_map);
//
//        // FORCE DEMO MODE (no Firebase at all)
//        Toast.makeText(this, "Demo Mode ON (Firebase bypassed)", Toast.LENGTH_SHORT).show();
//
//        // init views
//        mLogout = findViewById(R.id.logout);
//        mSettings = findViewById(R.id.settings);
//        mrideStatus = findViewById(R.id.rideStatus);
//        mworkingSwitch = findViewById(R.id.workingSwitch);
//        mHistory = findViewById(R.id.history);
//
//        mCustomerInfo = findViewById(R.id.customerInfo);
//        mCustomerName = findViewById(R.id.customerName);
//        mCustomerPhone = findViewById(R.id.customerPhone);
//        mCustomerDestination = findViewById(R.id.customerDestination);
//
//        if (mCustomerInfo != null) mCustomerInfo.setVisibility(View.GONE);
//        if (mrideStatus != null) mrideStatus.setText("Waiting for request");
//
//        // location client
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        // --- ALWAYS attach map async regardless of permission state ---
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        } else {
//            Toast.makeText(this, "Map fragment missing!", Toast.LENGTH_LONG).show();
//        }
//
//        // listeners (unchanged)
//        if (mworkingSwitch != null) {
//            mworkingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (isChecked) startLocationUpdates();
//                else stopLocationUpdates();
//            });
//        }
//
//        if (mLogout != null) {
//            mLogout.setOnClickListener(v -> {
//                stopLocationUpdates();
//                startActivity(new Intent(DriverMapActivity.this, Welcome_Activity.class));
//                finish();
//            });
//        }
//        if (mSettings != null) mSettings.setOnClickListener(v -> startActivity(new Intent(DriverMapActivity.this, DriverSettingActivity.class)));
//        if (mHistory != null) mHistory.setOnClickListener(v -> startActivity(new Intent(DriverMapActivity.this, HistoryActivity.class)));
//        if (mrideStatus != null) {
//            mrideStatus.setOnClickListener(v -> {
//                if (!demoRequestActive) createDemoRequestManually();
//                else if (!demoRideAccepted) acceptDemoRequest();
//                else completeDemoRide();
//            });
//        }
//
//        // --- NEW: Start driver notification listener if a Firebase user is logged in ---
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            startDriverNotificationListener();
//        }
//    }
//
//    // Map ready
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(this, "Map is ready!", Toast.LENGTH_SHORT).show();
//        mMap = googleMap;
//
//        // Extra safety: set UI settings so map must render tiles
//        try {
//            mMap.getUiSettings().setZoomControlsEnabled(true);
//            mMap.getUiSettings().setCompassEnabled(true);
//            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Move camera to a definite place so user sees map tiles even without location
//        LatLng fallback = new LatLng(20.5937, 78.9629); // India center
//        try {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallback, 5f));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // check location permission and enable my-location layer if allowed
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                mMap.setMyLocationEnabled(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            // create location request now
//            mLocationRequest = LocationRequest.create();
//            mLocationRequest.setInterval(2000);
//            mLocationRequest.setFastestInterval(1000);
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            startLocationUpdates();
//        } else {
//            // ask for permission; when granted onRequestPermissionsResult will call getMapAsync again
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//
//        // finally spawn demo request shortly after map appears
//        handler.postDelayed(this::spawnSimulatedRequest, 2500);
//    }
//
//    // start fused location updates
//    private void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                if (locationResult == null) return;
//
//                Location loc = locationResult.getLastLocation();
//                if (loc == null) return;
//                mLastLocation = loc;
//                LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
//
//                // update driver marker
//                if (driverMarker != null) {
//                    driverMarker.setPosition(pos);
//                } else if (mMap != null) {
//                    try {
//                        driverMarker = mMap.addMarker(new MarkerOptions()
//                                .position(pos)
//                                .title("Your Ambulance")
//                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
//                    } catch (Exception e) {
//                        // resource missing
//                        driverMarker = mMap.addMarker(new MarkerOptions().position(pos).title("Ambulance"));
//                    }
//                }
//
//                // move camera once
//                if (mMap != null) {
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f));
//                }
//            }
//        }, Looper.getMainLooper());
//    }
//
//    private void stopLocationUpdates() {
//        if (mFusedLocationClient != null) {
//            mFusedLocationClient.removeLocationUpdates(locationCallback);
//        }
//    }
//
//    private final LocationCallback locationCallback = new LocationCallback() {
//        // not used because we used inline callback above for simplicity
//    };
//
//    // Creates a simulated request near the driver's current location
//    private void spawnSimulatedRequest() {
//        if (mLastLocation == null || mMap == null || demoRequestActive) {
//            // try again shortly if location not yet available
//            handler.postDelayed(this::spawnSimulatedRequest, 1500);
//            return;
//        }
//
//        // place pickup slightly offset from driver (approx 500-800 meters)
//        double lat = mLastLocation.getLatitude();
//        double lng = mLastLocation.getLongitude();
//        double offsetLat = 0.004 + Math.random() * 0.006;   // ~400-600m
//        double offsetLng = 0.004 + Math.random() * 0.006;
//
//        demoPickupLatLng = new LatLng(lat + offsetLat, lng + offsetLng);
//
//        // create pickup marker
//        if (pickupMarker != null) pickupMarker.remove();
//        try {
//            pickupMarker = mMap.addMarker(new MarkerOptions()
//                    .position(demoPickupLatLng)
//                    .title("Patient pickup")
//                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));
//        } catch (Exception e) {
//            pickupMarker = mMap.addMarker(new MarkerOptions().position(demoPickupLatLng).title("Patient pickup"));
//        }
//
//        // show customer info panel
//        if (mCustomerInfo != null) {
//            mCustomerInfo.setVisibility(View.VISIBLE);
//            if (mCustomerName != null) mCustomerName.setText("Patient: Demo Person");
//            if (mCustomerPhone != null) mCustomerPhone.setText("Phone: +91-98765-43210");
//            if (mCustomerDestination != null) mCustomerDestination.setText("Destination: Nearby Hospital");
//        }
//
//        demoRequestActive = true;
//        demoRideAccepted = false;
//        if (mrideStatus != null) mrideStatus.setText("Accept Request");
//        Toast.makeText(this, "New patient request received (demo).", Toast.LENGTH_SHORT).show();
//
//        // optionally blink camera to pickup
//        handler.postDelayed(() -> {
//            if (mMap != null && demoPickupLatLng != null) {
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(demoPickupLatLng, 14f));
//                handler.postDelayed(() -> {
//                    if (mMap != null && mLastLocation != null)
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 13f));
//                }, 1600);
//            }
//        }, 400);
//    }
//
//    // manual creation (if driver pressed button when no request)
//    private void createDemoRequestManually() {
//        spawnSimulatedRequest();
//    }
//
//    // accept the demo request
//    private void acceptDemoRequest() {
//        if (!demoRequestActive || demoPickupLatLng == null || mLastLocation == null) {
//            Toast.makeText(this, "No request or location not available yet.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        demoRideAccepted = true;
//        if (mrideStatus != null) mrideStatus.setText("En route to pickup");
//
//        // draw a simple straight polyline between driver and pickup
//        drawRouteBetween(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), demoPickupLatLng);
//
//        // simulate movement toward pickup: every 1s move driver marker a bit closer
//        simulateDriveToPickup();
//    }
//
//    private void drawRouteBetween(LatLng from, LatLng to) {
//        clearPolylines();
//        PolylineOptions opts = new PolylineOptions().add(from).add(to).width(8f);
//        try {
//            opts.color(getResources().getColor(R.color.primary_dark_material_light));
//        } catch (Exception e) {
//            // ignore color issues
//        }
//        Polyline p = mMap.addPolyline(opts);
//        polylines.add(p);
//    }
//
//    private void clearPolylines() {
//        for (Polyline pl : polylines) {
//            try { pl.remove(); } catch (Exception e) { /* ignore */ }
//        }
//        polylines.clear();
//    }
//
//    // simulate moving towards pickup with steps
//    private void simulateDriveToPickup() {
//        if (mLastLocation == null || demoPickupLatLng == null) return;
//
//        final LatLng start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//        final LatLng end = demoPickupLatLng;
//
//        final int steps = 20;
//        final long stepDelay = 700; // ms
//
//        final double latStep = (end.latitude - start.latitude) / steps;
//        final double lngStep = (end.longitude - start.longitude) / steps;
//
//        handler.post(new Runnable() {
//            int i = 1;
//            @Override
//            public void run() {
//                double newLat = start.latitude + latStep * i;
//                double newLng = start.longitude + lngStep * i;
//                LatLng newPos = new LatLng(newLat, newLng);
//
//                // move driver marker
//                if (driverMarker != null) driverMarker.setPosition(newPos);
//                if (mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPos, 14f));
//
//                // update polyline to use new position -> simple redraw
//                drawRouteBetween(newPos, end);
//
//                i++;
//                if (i <= steps) {
//                    handler.postDelayed(this, stepDelay);
//                } else {
//                    // arrived
//                    onArrivedAtPickup();
//                }
//            }
//        });
//    }
//
//    private void onArrivedAtPickup() {
//        if (mrideStatus != null) mrideStatus.setText("Pickup Complete - End Ride");
//        Toast.makeText(this, "Arrived at pickup (demo).", Toast.LENGTH_SHORT).show();
//
//        // simulate small wait then complete ride automatically
//        handler.postDelayed(this::completeDemoRide, 2000);
//    }
//
//    private void completeDemoRide() {
//        // clear marker & UI
//        if (pickupMarker != null) {
//            try { pickupMarker.remove(); } catch (Exception e) { /* ignore */ }
//            pickupMarker = null;
//        }
//        clearPolylines();
//        demoRequestActive = false;
//        demoRideAccepted = false;
//
//        if (mCustomerInfo != null) mCustomerInfo.setVisibility(View.GONE);
//        if (mrideStatus != null) mrideStatus.setText("Waiting for request");
//        Toast.makeText(this, "Demo ride finished.", Toast.LENGTH_SHORT).show();
//
//        // move camera back to driver location
//        if (mLastLocation != null && mMap != null) {
//            LatLng pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f));
//        }
//
//        // spawn another request automatically after a short delay to demonstrate repeated flow
//        handler.postDelayed(this::spawnSimulatedRequest, 4000);
//    }
//
//    // ------------------ NEW: Firebase notification & accept logic ------------------
//
//    /**
//     * Start listening for DriverNotifications/{driverId}
//     * When a child is added, show a dialog to accept/reject.
//     */
//    private void startDriverNotificationListener() {
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
//
//        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        driverNotifRef = FirebaseDatabase.getInstance().getReference("DriverNotifications").child(driverId);
//
//        // protect against double-adding
//        if (driverNotifListener != null) return;
//
//        driverNotifListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
//                if (snapshot == null || !snapshot.exists()) return;
//
//                final String requestId = snapshot.getKey();
//                Object raw = snapshot.getValue();
//                Map<String, Object> notif = new HashMap<>();
//                if (raw instanceof Map) {
//                    //noinspection unchecked
//                    notif = (Map<String, Object>) raw;
//                }
//
//                final String pickupLat = notif.get("pickupLat") != null ? notif.get("pickupLat").toString() : "unknown";
//                final String pickupLng = notif.get("pickupLng") != null ? notif.get("pickupLng").toString() : "unknown";
//                final String msg = "Pickup at: " + pickupLat + ", " + pickupLng;
//
//                runOnUiThread(() -> {
//                    new AlertDialog.Builder(DriverMapActivity.this)
//                            .setTitle("Ambulance Request")
//                            .setMessage(msg)
//                            .setCancelable(false)
//                            .setPositiveButton("Accept", (dialog, which) -> {
//                                acceptRequest(requestId);
//                            })
//                            .setNegativeButton("Reject", (dialog, which) -> {
//                                // mark this notification as rejected
//                                if (driverNotifRef != null && requestId != null) {
//                                    driverNotifRef.child(requestId).child("status").setValue("rejected");
//                                }
//                            }).show();
//                });
//            }
//
//            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) { }
//            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
//            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) { }
//            @Override public void onCancelled(@NonNull DatabaseError error) { }
//        };
//
//        driverNotifRef.addChildEventListener(driverNotifListener);
//    }
//
//    /**
//     * Accept the request using a transaction to ensure FCFS (first committer wins).
//     * On success, mirror assignment into Users/Drivers/{driverId}/customerRequest so older logic continues working.
//     */
//    private void acceptRequest(final String requestId) {
//        if (requestId == null || requestId.isEmpty()) return;
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//            Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        final String myDriverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        final DatabaseReference reqRef = FirebaseDatabase.getInstance().getReference("RideRequests").child(requestId);
//
//        reqRef.runTransaction(new Transaction.Handler() {
//            @NonNull
//            @Override
//            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//                if (currentData == null) return Transaction.success(currentData);
//
//                String acceptedDriver = null;
//                String status = null;
//                if (currentData.child("acceptedDriver").getValue() != null) {
//                    acceptedDriver = currentData.child("acceptedDriver").getValue(String.class);
//                }
//                if (currentData.child("status").getValue() != null) {
//                    status = currentData.child("status").getValue(String.class);
//                }
//
//                if (acceptedDriver == null || acceptedDriver.isEmpty()) {
//                    currentData.child("acceptedDriver").setValue(myDriverId);
//                    currentData.child("status").setValue("driver_assigned");
//                    return Transaction.success(currentData);
//                } else {
//                    // someone already accepted
//                    return Transaction.abort();
//                }
//            }
//
//            @Override
//            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot snapshot) {
//                if (committed && snapshot != null && snapshot.exists()) {
//                    // success — mirror data for backward compatibility
//                    String customerId = snapshot.child("customerId").getValue(String.class);
//                    String destination = snapshot.child("destination").getValue(String.class);
//
//                    DatabaseReference driverCustRef = FirebaseDatabase.getInstance().getReference()
//                            .child("Users").child("Drivers").child(myDriverId).child("customerRequest");
//
//                    Map<String, Object> mirror = new HashMap<>();
//                    mirror.put("customerRideId", customerId != null ? customerId : "");
//                    mirror.put("destination", destination != null ? destination : "");
//                    if (snapshot.child("destinationLat").exists()) mirror.put("destinationLat", snapshot.child("destinationLat").getValue());
//                    if (snapshot.child("destinationLng").exists()) mirror.put("destinationLng", snapshot.child("destinationLng").getValue());
//
//                    driverCustRef.updateChildren(mirror);
//
//                    // mark our notification as accepted (if exists)
//                    DatabaseReference myNotif = FirebaseDatabase.getInstance().getReference("DriverNotifications").child(myDriverId).child(requestId);
//                    myNotif.child("status").setValue("accepted");
//
//                    // best-effort: mark other driver notifications for this request as rejected
//                    markOtherNotificationsRejected(requestId, myDriverId);
//
//                    Toast.makeText(DriverMapActivity.this, "Request accepted", Toast.LENGTH_SHORT).show();
//                } else {
//                    // abort or failure
//                    Toast.makeText(DriverMapActivity.this, "Sorry — request was accepted by another driver.", Toast.LENGTH_LONG).show();
//                    // mark our notification as rejected (if exists)
//                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                        String d = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        DatabaseReference myNotif = FirebaseDatabase.getInstance().getReference("DriverNotifications").child(d).child(requestId);
//                        myNotif.child("status").setValue("rejected");
//                    }
//                }
//            }
//        });
//    }
//
//    /**
//     * Best-effort scan to mark other drivers' /DriverNotifications/*/{requestId}.status = "rejected"
//            */
//    private void markOtherNotificationsRejected(final String requestId, final String winnerDriverId) {
//        if (requestId == null) return;
//
//        DatabaseReference allNotifsRoot = FirebaseDatabase.getInstance().getReference("DriverNotifications");
//        allNotifsRoot.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot driverNode : snapshot.getChildren()) {
//                    String dId = driverNode.getKey();
//                    if (dId == null) continue;
//                    if (dId.equals(winnerDriverId)) continue;
//                    if (driverNode.child(requestId).exists()) {
//                        driverNode.child(requestId).getRef().child("status").setValue("rejected");
//                    }
//                }
//            }
//            @Override public void onCancelled(@NonNull DatabaseError error) { }
//        });
//    }
//
//    // ------------------ end new Firebase logic ------------------
//
//    // permission result
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 1) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//                if (mapFragment != null) mapFragment.getMapAsync(this);
//                startLocationUpdates();
//            } else {
//                // permission denied: show friendly dialog
//                new AlertDialog.Builder(this)
//                        .setTitle("Location permission required")
//                        .setMessage("This demo uses location. Please grant permission in settings or allow to continue.")
//                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                        .show();
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        stopLocationUpdates();
//        handler.removeCallbacksAndMessages(null);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // cleanup driver notification listener
//        try {
//            if (driverNotifRef != null && driverNotifListener != null) {
//                driverNotifRef.removeEventListener(driverNotifListener);
//            }
//        } catch (Exception ignored) {}
//        stopLocationUpdates();
//        handler.removeCallbacksAndMessages(null);
//    }
//}
package saksham.medrescue.saksham;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import sachdeva.saksham.medrescue.R;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DriverMapActivity with optional demo mode + Firebase notification listener & transaction-based accept (FCFS)
 */
public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "DriverMapActivity";

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private android.location.Location mLastLocation;

    private Marker driverMarker;
    private Marker pickupMarker;

    private Button mLogout, mSettings, mrideStatus, mHistory;
    private Switch mworkingSwitch;
    private LinearLayout mCustomerInfo;
    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;

    private boolean demoRequestActive = false;
    private boolean demoRideAccepted = false;
    private LatLng demoPickupLatLng;
    private List<Polyline> polylines = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper());

    // Firebase listener fields
    private DatabaseReference driverNotifRef;
    private ChildEventListener driverNotifListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        Toast.makeText(this, "DriverMap started", Toast.LENGTH_SHORT).show();

        // init views
        mLogout = findViewById(R.id.logout);
        mSettings = findViewById(R.id.settings);
        mrideStatus = findViewById(R.id.rideStatus);
        mworkingSwitch = findViewById(R.id.workingSwitch);
        mHistory = findViewById(R.id.history);

        mCustomerInfo = findViewById(R.id.customerInfo);
        mCustomerName = findViewById(R.id.customerName);
        mCustomerPhone = findViewById(R.id.customerPhone);
        mCustomerDestination = findViewById(R.id.customerDestination);

        if (mCustomerInfo != null) mCustomerInfo.setVisibility(View.GONE);
        if (mrideStatus != null) mrideStatus.setText("Waiting for request");

        // location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // attach map async
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Map fragment missing!", Toast.LENGTH_LONG).show();
        }

        // listeners
        if (mworkingSwitch != null) {
            mworkingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) startLocationUpdates();
                else stopLocationUpdates();
            });
        }

        if (mLogout != null) {
            mLogout.setOnClickListener(v -> {
                stopLocationUpdates();
                startActivity(new Intent(DriverMapActivity.this, Welcome_Activity.class));
                finish();
            });
        }

        if (mSettings != null) mSettings.setOnClickListener(v -> startActivity(new Intent(DriverMapActivity.this, DriverSettingActivity.class)));
        if (mHistory != null) mHistory.setOnClickListener(v -> startActivity(new Intent(DriverMapActivity.this, HistoryActivity.class)));
        if (mrideStatus != null) {
            mrideStatus.setOnClickListener(v -> {
                if (!demoRequestActive) createDemoRequestManually();
                else if (!demoRideAccepted) acceptDemoRequest();
                else completeDemoRide();
            });
        }

        // Start driver notification listener only if a Firebase user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startDriverNotificationListener();
        } else {
            Log.d(TAG, "No Firebase user signed in; driver notification listener not started (demo possible).");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LatLng fallback = new LatLng(20.5937, 78.9629); // India center
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallback, 5f));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                mMap.setMyLocationEnabled(true);
            } catch (Exception e) { e.printStackTrace(); }
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(2000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        handler.postDelayed(this::spawnSimulatedRequest, 2500);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;
                Location loc = locationResult.getLastLocation();
                if (loc == null) return;
                mLastLocation = loc;
                LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

                if (driverMarker != null) {
                    driverMarker.setPosition(pos);
                } else if (mMap != null) {
                    try {
                        driverMarker = mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title("Your Ambulance")
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
                    } catch (Exception e) {
                        driverMarker = mMap.addMarker(new MarkerOptions().position(pos).title("Ambulance"));
                    }
                }

                if (mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f));
                }
            }
        }, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        if (mFusedLocationClient != null) {
            try {
                mFusedLocationClient.removeLocationUpdates(locationCallback);
            } catch (Exception e) { /* ignore */ }
        }
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        // not used because inline callback in startLocationUpdates() used
    };

    // demo functions
    private void spawnSimulatedRequest() {
        if (mLastLocation == null || mMap == null || demoRequestActive) {
            handler.postDelayed(this::spawnSimulatedRequest, 1500);
            return;
        }

        double lat = mLastLocation.getLatitude();
        double lng = mLastLocation.getLongitude();
        double offsetLat = 0.004 + Math.random() * 0.006;
        double offsetLng = 0.004 + Math.random() * 0.006;

        demoPickupLatLng = new LatLng(lat + offsetLat, lng + offsetLng);

        if (pickupMarker != null) pickupMarker.remove();
        try {
            pickupMarker = mMap.addMarker(new MarkerOptions()
                    .position(demoPickupLatLng)
                    .title("Patient pickup")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));
        } catch (Exception e) {
            pickupMarker = mMap.addMarker(new MarkerOptions().position(demoPickupLatLng).title("Patient pickup"));
        }

        if (mCustomerInfo != null) {
            mCustomerInfo.setVisibility(View.VISIBLE);
            if (mCustomerName != null) mCustomerName.setText("Patient: Demo Person");
            if (mCustomerPhone != null) mCustomerPhone.setText("Phone: +91-98765-43210");
            if (mCustomerDestination != null) mCustomerDestination.setText("Destination: Nearby Hospital");
        }

        demoRequestActive = true;
        demoRideAccepted = false;
        if (mrideStatus != null) mrideStatus.setText("Accept Request");
        Toast.makeText(this, "New patient request received (demo).", Toast.LENGTH_SHORT).show();

        handler.postDelayed(() -> {
            if (mMap != null && demoPickupLatLng != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(demoPickupLatLng, 14f));
                handler.postDelayed(() -> {
                    if (mMap != null && mLastLocation != null)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 13f));
                }, 1600);
            }
        }, 400);
    }

    private void createDemoRequestManually() { spawnSimulatedRequest(); }

    private void acceptDemoRequest() {
        if (!demoRequestActive || demoPickupLatLng == null || mLastLocation == null) {
            Toast.makeText(this, "No request or location not available yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        demoRideAccepted = true;
        if (mrideStatus != null) mrideStatus.setText("En route to pickup");
        drawRouteBetween(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), demoPickupLatLng);
        simulateDriveToPickup();
    }

    private void drawRouteBetween(LatLng from, LatLng to) {
        clearPolylines();
        PolylineOptions opts = new PolylineOptions().add(from).add(to).width(8f);
        try { opts.color(getResources().getColor(R.color.primary_dark_material_light)); } catch (Exception e) { /* ignore */ }
        Polyline p = mMap.addPolyline(opts);
        polylines.add(p);
    }

    private void clearPolylines() {
        for (Polyline pl : polylines) {
            try { pl.remove(); } catch (Exception e) { /* ignore */ }
        }
        polylines.clear();
    }

    private void simulateDriveToPickup() {
        if (mLastLocation == null || demoPickupLatLng == null) return;

        final LatLng start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        final LatLng end = demoPickupLatLng;
        final int steps = 20;
        final long stepDelay = 700;
        final double latStep = (end.latitude - start.latitude) / steps;
        final double lngStep = (end.longitude - start.longitude) / steps;

        handler.post(new Runnable() {
            int i = 1;
            @Override
            public void run() {
                double newLat = start.latitude + latStep * i;
                double newLng = start.longitude + lngStep * i;
                LatLng newPos = new LatLng(newLat, newLng);
                if (driverMarker != null) driverMarker.setPosition(newPos);
                if (mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPos, 14f));
                drawRouteBetween(newPos, end);
                i++;
                if (i <= steps) handler.postDelayed(this, stepDelay);
                else onArrivedAtPickup();
            }
        });
    }

    private void onArrivedAtPickup() {
        if (mrideStatus != null) mrideStatus.setText("Pickup Complete - End Ride");
        Toast.makeText(this, "Arrived at pickup (demo).", Toast.LENGTH_SHORT).show();
        handler.postDelayed(this::completeDemoRide, 2000);
    }

    private void completeDemoRide() {
        if (pickupMarker != null) {
            try { pickupMarker.remove(); } catch (Exception e) { /* ignore */ }
            pickupMarker = null;
        }
        clearPolylines();
        demoRequestActive = false;
        demoRideAccepted = false;
        if (mCustomerInfo != null) mCustomerInfo.setVisibility(View.GONE);
        if (mrideStatus != null) mrideStatus.setText("Waiting for request");
        Toast.makeText(this, "Demo ride finished.", Toast.LENGTH_SHORT).show();
        if (mLastLocation != null && mMap != null) {
            LatLng pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f));
        }
        handler.postDelayed(this::spawnSimulatedRequest, 4000);
    }

    // --------------------
    // Firebase notification -> accept (FCFS)
    // --------------------

    private void startDriverNotificationListener() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        driverNotifRef = FirebaseDatabase.getInstance().getReference("DriverNotifications").child(driverId);

        driverNotifListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                if (!snapshot.exists()) return;
                final String requestId = snapshot.getKey();
                Object val = snapshot.getValue();
                String pickupInfo = "";
                if (snapshot.child("pickupLat").exists() && snapshot.child("pickupLng").exists()) {
                    pickupInfo = snapshot.child("pickupLat").getValue().toString() + ", " + snapshot.child("pickupLng").getValue().toString();
                } else if (snapshot.child("pickup").exists()) {
                    pickupInfo = snapshot.child("pickup").getValue(String.class);
                }
                final String capturedPickupInfo = pickupInfo;

                runOnUiThread(() -> {
                    new AlertDialog.Builder(DriverMapActivity.this)
                            .setTitle("Ambulance Request")
                            .setMessage("Pickup at: " + capturedPickupInfo + "\nRequest: " + requestId)
                            .setPositiveButton("Accept", (d, w) -> {
                                acceptRequest(requestId);
                            })
                            .setNegativeButton("Reject", (d, w) -> {
                                // mark this notification node as rejected
                                if (requestId != null) {
                                    snapshot.getRef().child("status").setValue("rejected");
                                }
                            })
                            .setCancelable(false)
                            .show();
                });
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Driver notif listener cancelled: " + error.getMessage());
            }
        };

        driverNotifRef.addChildEventListener(driverNotifListener);
    }

    private void acceptRequest(final String requestId) {
        if (requestId == null) {
            Toast.makeText(this, "Request id null", Toast.LENGTH_SHORT).show();
            return;
        }
        final String myDriverId = FirebaseAuth.getInstance().getCurrentUser() == null ? null : FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (myDriverId == null) {
            Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        final DatabaseReference reqRef = FirebaseDatabase.getInstance().getReference("RideRequests").child(requestId);

        reqRef.runTransaction(new com.google.firebase.database.Transaction.Handler() {
            @NonNull
            @Override
            public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData currentData) {
                if (currentData == null) return com.google.firebase.database.Transaction.success(currentData);
                String acceptedDriver = currentData.child("acceptedDriver").getValue(String.class);
                String status = currentData.child("status").getValue(String.class);
                if (acceptedDriver == null || acceptedDriver.isEmpty()) {
                    currentData.child("acceptedDriver").setValue(myDriverId);
                    currentData.child("status").setValue("driver_assigned");
                    return com.google.firebase.database.Transaction.success(currentData);
                } else {
                    // already assigned
                    return com.google.firebase.database.Transaction.abort();
                }
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot snapshot) {
                if (committed) {
                    // success — mirror to old path so existing code works (update driver's customerRequest)
                    try {
                        String customerId = snapshot.child("customerId").getValue(String.class);
                        String destination = snapshot.child("destination").getValue(String.class);

                        DatabaseReference driverCustRef = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child("Drivers").child(myDriverId).child("customerRequest");

                        Map<String, Object> map = new HashMap<>();
                        map.put("customerRideId", customerId);
                        map.put("destination", destination);
                        if (snapshot.child("destLat").exists()) map.put("destinationLat", snapshot.child("destLat").getValue());
                        if (snapshot.child("destLng").exists()) map.put("destinationLng", snapshot.child("destLng").getValue());
                        driverCustRef.updateChildren(map);

                        // update this driver's notification status to accepted
                        DatabaseReference myNotif = FirebaseDatabase.getInstance().getReference("DriverNotifications").child(myDriverId).child(requestId);
                        myNotif.child("status").setValue("accepted");

                        // mark other notified drivers as rejected (best-effort)
                        markOtherNotificationsRejected(requestId, myDriverId);

                        runOnUiThread(() -> Toast.makeText(DriverMapActivity.this, "Request accepted", Toast.LENGTH_SHORT).show());

                    } catch (Exception e) {
                        Log.e(TAG, "onComplete accepted -> error mirroring: " + e.getMessage());
                    }
                } else {
                    // failed (someone else already accepted)
                    runOnUiThread(() -> Toast.makeText(DriverMapActivity.this, "Sorry — request was accepted by another driver.", Toast.LENGTH_LONG).show());
                    // also mark our notif as rejected
                    DatabaseReference myNotif = FirebaseDatabase.getInstance().getReference("DriverNotifications").child(myDriverId).child(requestId);
                    myNotif.child("status").setValue("rejected");
                }
            }
        });
    }

    private void markOtherNotificationsRejected(final String requestId, final String winnerDriverId) {
        if (requestId == null) return;
        DatabaseReference allNotifsRoot = FirebaseDatabase.getInstance().getReference("DriverNotifications");
        allNotifsRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot driverNode : snapshot.getChildren()) {
                    String dId = driverNode.getKey();
                    if (dId == null || dId.equals(winnerDriverId)) continue;
                    if (driverNode.child(requestId).exists()) {
                        driverNode.child(requestId).getRef().child("status").setValue("rejected");
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (driverNotifRef != null && driverNotifListener != null) {
            try {
                driverNotifRef.removeEventListener(driverNotifListener);
            } catch (Exception e) { /* ignore */ }
        }
        stopLocationUpdates();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
        handler.removeCallbacksAndMessages(null);
    }
}
