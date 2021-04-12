package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

class background_process extends BroadcastReceiver {
    FirebaseDatabase firebaseDatabase;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    String id2;
    Location location1;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public void onReceive(Context context, Intent intent) {

        //id2=intent.getStringExtra("id");
        Toast.makeText(context, "brodcast ", Toast.LENGTH_SHORT).show();

/*        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);


        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());




    }

    LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                         firebaseDatabase.getReference().child(id2).child(id2).child("locationla").setValue(String.valueOf(mLastLocation.getLatitude()));
                                        firebaseDatabase.getReference().child(id2).child(id2).child("locationlo").setValue(String.valueOf(mLastLocation.getLongitude()));
            }

        }
    };*/
    }
}
