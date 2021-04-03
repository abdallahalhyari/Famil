package com.example.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    FirebaseAuth fAuth;
    int i, i1 = 0;
    LocationManager locationManager;
    ArrayList<User> dataList;
    ArrayList<String> listlocation;
    DatabaseReference databaseReference;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    String ido;
    String ida;
    String id, id2, parentid;
    String name;
    FirebaseDatabase firebaseDatabase;
    static GeoPoint geoPoint;

    BroadcastReceiver br;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dataList = new ArrayList<>();
        listlocation = new ArrayList<>();
        br = new background_process();

       /* IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);*/
        firebaseDatabase = FirebaseDatabase.getInstance();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mythread mythrea = new mythread();
        mythrea.start();
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(),listEmail.class));
                        overridePendingTransition(0,0);

                        return true;
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(),profile.class));
                        overridePendingTransition(0,0);

                        return true;
                }
                return false;
            }
        });
    }

    class mythread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.clear();
                           requestNewLocationData();
                            getlocaiondatabase();
                        }
                    });

                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void getlocaiondatabase() {

        id2 = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("user").document(id2);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    id = value.getString("parentid");
                }

                databaseReference = firebaseDatabase.getReference().child(id);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dataList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataList.add(dataSnapshot.getValue(User.class));
                        }
                        fAuth = FirebaseAuth.getInstance();
                        id2 = fAuth.getCurrentUser().getUid();
                        for (i = 0; i < dataList.size(); i++) {
                            ida = dataList.get(i).getLocationla();
                            ido = dataList.get(i).getLocationlo();
                            parentid = dataList.get(i).getId();
                            name = dataList.get(i).getName();

                            LatLng sydney = new LatLng(Double.parseDouble(ida), Double.parseDouble(ido));
                            mMap.addMarker(new MarkerOptions().position(sydney).title(name));
                            if (i1 == 0) {
                                i1 = 1;
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void requestNewLocationData() {


        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

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
            if (fAuth.getCurrentUser() != null) {
                firebaseDatabase.getReference().child(id).child(id2).child("locationla").setValue(String.valueOf(mLastLocation.getLatitude()));
                firebaseDatabase.getReference().child(id).child(id2).child("locationlo").setValue(String.valueOf(mLastLocation.getLongitude()));
            }

        }
    };

}


