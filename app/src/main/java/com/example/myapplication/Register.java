package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Register extends AppCompatActivity implements LocationListener {
    public static final String TAG = "TAG";
    EditText mFullName, mEmail, mPassword, mPhone, id_fa;
    Button mRegisterBtn;
    RadioButton childRadio, parentRadio;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    LocationManager locationManager;
    ArrayList<User> datalist, listfound;
    FirebaseUser firebaseUser;
    String key = "";
    public static String userID;
    private static long idCounter = 2021;
    String id_f;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String password;
    String email;
    String fullName;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.createText);
        fAuth = FirebaseAuth.getInstance();
        firebaseUser = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        childRadio = findViewById(R.id.radio_child);
        parentRadio = findViewById(R.id.radio_parent);
        id_fa = findViewById(R.id.id_Father);


        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified()) {
                startActivity(new Intent(Register.this, profile.class));
                finish();
            } else {
                startActivity(new Intent(Register.this, login.class));
                finish();
            }
        }


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
                                        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                                        String location = locationManager.toString();
                                        Toast.makeText(Register.this, key, Toast.LENGTH_SHORT).show();

                                        Map<String, Object> user = new HashMap<>();
                                        user.put("name", fullName);
                                        user.put("email", email);
                                        user.put("phone", phone);
                                        user.put("Id", "1");
                                        user.put("location", location);
                                        databaseReference.push().setValue(user);
                                        Toast.makeText(Register.this, "User Created."+key, Toast.LENGTH_SHORT).show();

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
                                    return;
                                }

                            } else {


                                userID = createID();
                                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                                String location = locationManager.toString();
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference().child("parent" + userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", fullName);
                                user.put("email", email);
                                user.put("phone", phone);
                                user.put("Id", userID);
                                user.put("location", location);
                                databaseReference.push().setValue(user);
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

        if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Register.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        getlocation();
    }

    @SuppressLint("MissingPermission")
    private void getlocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, Register.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(Register.this, "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(Register.this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addressList.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    public static synchronized String createID() {
        idCounter = idCounter + 5;
        return String.valueOf(idCounter);
    }
}