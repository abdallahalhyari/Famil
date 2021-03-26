package com.example.myapplication;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.WindowManager;

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
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Set<String> mChatroomIds = new HashSet<>();
    private ListenerRegistration mChatroomEventListener;
    private FirebaseFirestore mDb;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private static final String TAG = "MainActivity";
    FirebaseAuth fAuth;
    User user;
    int i;
    LocationManager locationManager;
    ArrayList<User> dataList;
    ArrayList<String> listlocation;
    GeoPoint currentLocation;
    DatabaseReference databaseReference;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    String ido;
    String ida;
    String id, id2;
    String name;
    SharedPreferences sharedPreferences;
    FirebaseDatabase firebaseDatabase;
   static GeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDb = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dataList = new ArrayList<>();
        listlocation = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mythread mythrea = new mythread();
        mythrea.start();

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
                            name = dataList.get(i).getName();
                        /*    if (id.equals("parent" + id2)) {
                                LatLng sydney = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(sydney).title("My Location" + geoPoint.getLatitude() + "  " + geoPoint.getLongitude()));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                            }
                            if (!id.equals("parent" + id2)){

                            }*/
                            LatLng sydney = new LatLng(Double.parseDouble(ida), Double.parseDouble(ido));
                            mMap.addMarker(new MarkerOptions().position(sydney).title(name));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
            if (fAuth.getCurrentUser()!=null){
                firebaseDatabase.getReference().child(id).child(id2).child("locationla").setValue(String.valueOf(mLastLocation.getLatitude()));
                firebaseDatabase.getReference().child(id).child(id2).child("locationlo").setValue(String.valueOf(mLastLocation.getLongitude()));
            }

        }
    };

}


