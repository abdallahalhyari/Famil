package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.google.firebase.storage.internal.Sleeper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import javax.annotation.Nullable;

public class listEmail extends AppCompatActivity {
    static RecyclerView recview;
    static ArrayList<User> datalist, listfound, listcheck;
    Adapter adapter;
    HashSet<User> hashSet;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    DocumentReference documentReference;
    String id, ID;
    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_email);
        recview = findViewById(R.id.recycler_view);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(this));
        listfound = new ArrayList<>();
        datalist = new ArrayList<>();
        listcheck = new ArrayList<>();
        adapter = new Adapter(datalist, this);
        recview.setAdapter(adapter);
        fAuth = FirebaseAuth.getInstance();
        hashSet = new HashSet<>();
        fStore = FirebaseFirestore.getInstance();
        ID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        if (i == 0) {

            documentReference = fStore.collection("user").document(ID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        id = documentSnapshot.getString("Id");
                        if (id != "1") {
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference().child("parent" + id);
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    datalist.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.getValue(User.class).getId().equals("1")) {
                                            datalist.add(dataSnapshot.getValue(User.class));
                                        }
                                    }


                                    adapter.notifyDataSetChanged();

                                }

                                //        }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }


                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }

                }

            });
        }
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                showDialog(listEmail.this, "Are you sure to remove it? ", "", position);


            }
        });
    }

    @SuppressLint("RestrictedApi")
    public void removeItem(int position) {
        datalist.remove(position);
        adapter.notifyItemRemoved(position);

    }

    public void showDialog(Activity activity, String msg, String msg2, int position) {

        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.round_corner);
        LottieAnimationView lottieAnimationView= dialog.findViewById(R.id.a);
        lottieAnimationView.setVisibility(View.GONE);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        TextView text2 = (TextView) dialog.findViewById(R.id.text_dialog2);
        text2.setText(msg2);
        EditText ed = dialog.findViewById(R.id.ema);
        ed.setVisibility(View.GONE);
        Button dialogButton1 = (Button) dialog.findViewById(R.id.btn1);
        dialogButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
                dialog.dismiss();

            }
        });

        Button dialogButton2 = (Button) dialog.findViewById(R.id.btn2);
        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}

