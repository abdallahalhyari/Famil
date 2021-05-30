package com.example.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonParseException;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    FirebaseAuth fAuth;
    int i, i1 = 0;
    Spinner spinner;
    LocationManager locationManager;
    ArrayList<User> dataList;
    ArrayList<String> listlocation;
    DatabaseReference databaseReference;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    String ido;
    String ida;
    static ArrayList<String> datalist2;
    String id, id2, parentid, ID;
    String name;
    FirebaseDatabase firebaseDatabase;
    static GeoPoint geoPoint;
    Intent intent;
    BroadcastReceiver br;
    Boolean therad = false;
    int z=0;
    double sp;
    double spp;
    String token,id3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startService(new Intent(this, com.example.myapplication.LocationServices.class));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.Maps);
        mapFragment.getMapAsync(this);
        dataList = new ArrayList<>();
        listlocation = new ArrayList<>();
        br = new background_process();
        datalist2 = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_notifications:
                        therad = true;
                        intent = new Intent(getApplicationContext(), Help_Press.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.navigation_dashboard:
                        therad = true;
                        intent = new Intent(getApplicationContext(), ChatRoom.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;
                }
                return false;
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        fAuth = FirebaseAuth.getInstance();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                        DocumentReference documentReference = fStore.collection("user").document(fAuth.getCurrentUser().getUid());
                        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                id3 = value.getString("parentid");
                                token = Objects.requireNonNull(task.getResult()).getToken();

                            }

                        });

                    }
                });

        //getlocaiondatabase();
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
                    ID=value.getString("Id");
                }
                databaseReference = firebaseDatabase.getReference().child("sp" + id);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            if (!spinner.getItemAtPosition(Integer.parseInt(dataSnapshot.getValue().toString())).equals("Choose the safety distance (km)")) {
                                sp = Double.parseDouble(dataSnapshot.getValue().toString());
                                spinner.setSelection((int) sp);
                                spp = Double.parseDouble(spinner.getItemAtPosition((int) sp).toString()) * 1000;

                            } else {
                              if (!ID.equals("1")) {
                                  sp = Double.parseDouble(dataSnapshot.getValue().toString());
                                  spinner.setSelection(0);
                                  spp = 0;
                              }else{spp = 0;}
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                    if (!ID.equals(id2)){spinner.setVisibility(View.GONE);}
                databaseReference = firebaseDatabase.getReference().child(id);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mMap.clear();
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
                            if (!dataList.get(i).getId().equals("1")) {
                                if (i1 == 0) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
                                    i1 = 1;
                                }
  
                                for (int x = 0; x < dataList.size(); x++) {
                                    if (!dataList.get(i).getId().equals(dataList.get(x).getId())) {
                                        //  Toast.makeText(getBaseContext(), spp + "meter", Toast.LENGTH_LONG).show();
                                        double dis = meterDistanceBetweenPoints(Float.parseFloat(dataList.get(i).getLocationla()), Float.parseFloat(dataList.get(i).getLocationlo()), Float.parseFloat(dataList.get(x).getLocationla()), Float.parseFloat(dataList.get(x).getLocationlo()));
                                        if (dis > spp) {
                                            if (spp!=0){
                                                //Toast.makeText(getBaseContext(), token, Toast.LENGTH_LONG).show();
                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                DatabaseReference databaseReference = firebaseDatabase.getReference().child("Tokens").child(id3);
                                                databaseReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                            datalist2.add(dataSnapshot.getValue(String.class));
                                                        }
                                                        if("1".equals(ID)) {
                                                            for (i = 0; datalist2.size() > i; i++) {
                                                                if (!token.equals(datalist2.get(i))) {
                                                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(datalist2.get(i), name, "The Need Help", getApplicationContext(), MapsActivity.this);
                                                                    notificationsSender.SendNotifications();

                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }

                                                });

                                                datalist2.clear();
                                                z=1;
                                            }

                                        }
                                    }
                                }
                                LatLng mLatLng = new LatLng(Double.parseDouble(ida), Double.parseDouble(ido));
                                CircleOptions circleOptions = new CircleOptions()
                                        .center(mLatLng)   //set center
                                        .radius(spp)   //set radius in meters
                                        .fillColor(Color.parseColor("#CCFB2323"))  //default
                                        .strokeColor(Color.BLACK)
                                        .strokeWidth(5);
                                Circle myCircle6 = mMap.addCircle(circleOptions);
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

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("sp" + id3);
        databaseReference.child("sp").setValue(spinner.getSelectedItemPosition());
        getlocaiondatabase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


