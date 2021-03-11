package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import javax.annotation.Nullable;

public class listEmail extends AppCompatActivity {
    static RecyclerView recview;
    static ArrayList<User> datalist, listfound, listcheck;
    Adapter adapter;

    String IdUser;
    HashSet<User> hashSet;
    FirebaseFirestore fStore;
    String check;
    FirebaseAuth fAuth;
    FirebaseUser user;
    DocumentReference documentReference;
    String id,ID;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        fAuth=FirebaseAuth.getInstance();
        hashSet = new HashSet<>();
        fStore = FirebaseFirestore.getInstance();
        ID= Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        documentReference = fStore.collection("user").document(ID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                   id=documentSnapshot.getString("Id");
                    if (Integer.parseInt(id) != 1) {

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference().child("parent" + id);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                datalist.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    datalist.add(dataSnapshot.getValue(User.class));
                                }

                                for (; datalist.size() > i; i++) {
                                    if (!datalist.get(i).getId().equals("1")) {
                                        datalist.remove(i);
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
}

