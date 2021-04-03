package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

class background_process extends BroadcastReceiver {
    FirebaseDatabase firebaseDatabase;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    String id2;
    Location location1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context, "Boot completed", Toast.LENGTH_SHORT).show();
        }
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
            Toast.makeText(context, "Connectivity changed", Toast.LENGTH_SHORT).show();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d("TAG", log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        Toast.makeText(context, "jjjjjjjjjjjj", Toast.LENGTH_LONG).show();

        Thread thread1 = new Thread() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                try {
                    LocationRequest mLocationRequest = new LocationRequest();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(5);
                    mLocationRequest.setFastestInterval(0);
                    mLocationRequest.setNumUpdates(1);


                    LocationCallback mLocationCallback = new LocationCallback() {

                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            Location mLastLocation = locationResult.getLastLocation();
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                firebaseDatabase.getReference().child(id2).child(id2).child("locationla").setValue(String.valueOf(mLastLocation.getLatitude()));
                                firebaseDatabase.getReference().child(id2).child(id2).child("locationlo").setValue(String.valueOf(mLastLocation.getLongitude()));
                            }

                        }
                    };
                    fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());


                    sleep(6000);


                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.start();

    }


}
