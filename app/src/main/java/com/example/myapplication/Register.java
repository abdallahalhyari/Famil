package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName, mEmail, mPassword, mPhone, id_fa;
    Button mRegisterBtn;
    RadioButton childRadio, parentRadio;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    LocationManager locationManager;
    FirebaseUser firebaseUser;
    private boolean mLocationPermissionGranted = false;
    String key = "";
    public String userID;
    int PERMISSION_ID = 44;
    private static long idCounter = 2021;
    public static String id_f;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String password;
    String email;
    SharedPreferences sharedPreferences;
    String fullName;
    String phone;
    DocumentReference documentReference;
    DatabaseReference databaseReference;
    Location currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    GeoPoint geoPoint;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fAuth = FirebaseAuth.getInstance();
        firebaseUser = fAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);
        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified()) {
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(Register.this, profile.class));
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(Register.this, login.class));

            }
        }

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.createText);
        fStore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("id_f", MODE_PRIVATE);
        childRadio = findViewById(R.id.radio_child);
        parentRadio = findViewById(R.id.radio_parent);
        id_fa = findViewById(R.id.id_Father);
        getLastLocation();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = mEmail.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                fullName = mFullName.getText().toString();
                phone = mPhone.getText().toString();
                id_f = id_fa.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                if (id_fa.getVisibility() == View.VISIBLE) {
                    if (TextUtils.isEmpty(id_f)) {
                        id_fa.setError("ID is Required.");
                        return;
                    }
                }


                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            if (fuser != null) {
                                fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Register.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                    }
                                });
                            }


                            if (id_fa.getVisibility() == View.VISIBLE) {

                                DatabaseReference databaseReference = firebaseDatabase.getReference().child("parent" + id_f);
                                final Query userQuery = databaseReference.orderByChild(id_f);

                                userQuery.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            key = child.getKey();

                                        }
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                if (key != null) {

                                    if (fuser != null) {


                                        String string1 = String.valueOf(geoPoint.getLatitude());
                                        String string2 = String.valueOf(geoPoint.getLongitude());
                                        Toast.makeText(Register.this, key, Toast.LENGTH_SHORT).show();
                                        Map<String, Object> user;
                                        user = new HashMap<>();

                                        user.put("name", fullName);
                                        user.put("email", email);
                                        user.put("phone", phone);
                                        user.put("Id", "1");
                                        user.put("locationla", string1);
                                        user.put("locationlo", string2);
                                        user.put("parentid", "parent" + id_f);
                                        databaseReference.child(fuser.getUid()).setValue(user);
                                        Toast.makeText(Register.this, "User Created." + key, Toast.LENGTH_SHORT).show();

                                        DocumentReference documentReference = fStore.collection("user").document(fAuth.getCurrentUser().getUid());
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: user Profile is created for ");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: " + e.toString());
                                            }
                                        });
                                        progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(Register.this, login.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Register.this, "ID Failure", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        fuser.delete();
                                    }
                                } else {
                                    Toast.makeText(Register.this, "ID Failure", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            } else {

                                userID = fuser.getUid();
                                String string1 = String.valueOf(geoPoint.getLatitude());
                                String string2 = String.valueOf(geoPoint.getLongitude());
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference().child("parent" + fuser.getUid());
                                Map<String, Object> user, location;
                                user = new HashMap<>();
                                location = new HashMap<>();
                                user.put("name", fullName);
                                user.put("email", email);
                                user.put("phone", phone);
                                user.put("Id", userID);
                                user.put("locationla", string1);
                                user.put("locationlo", string2);
                                user.put("parentid", "parent" + userID);
                                databaseReference.child(fuser.getUid()).setValue(user);
                                firebaseDatabase.getReference().child("location").child(fuser.getUid()).setValue(location);
                                Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();

                                DocumentReference documentReference = fStore.collection("user").document(fAuth.getCurrentUser().getUid());
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: user Profile is created for ");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.toString());
                                    }
                                });

                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(Register.this, login.class);
                                intent.putExtra("id", userID);
                                startActivity(intent);
                            }


                        } else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }


        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });


    }


    public void check_radio1(View view) {

        if (childRadio.isChecked()) {
            id_fa.setVisibility(View.VISIBLE);


        }

    }

    public void check_radio2(View view) {
        if (parentRadio.isChecked()) {
            id_fa.setVisibility(View.INVISIBLE);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }


    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            geoPoint = new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    public static synchronized String createID() {
        return String.valueOf(idCounter++);
    }
}