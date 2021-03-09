package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;

public class listEmail extends AppCompatActivity {
   static RecyclerView recview;
    static ArrayList<User> datalist, listfound, listcheck;
    Adapter adapter;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
    String IdUser;
    HashSet<User> hashSet;
    int x = 0;


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
        adapter = new Adapter(listfound, this);
        recview.setAdapter(adapter);
        hashSet = new HashSet<>();

    }


   /* public void Add_an_individual(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(listEmail.this);
        alertDialog.setTitle("Add Member");
        alertDialog.setMessage("Enter the email of your family member ");
        EditText input = new EditText(this);
        alertDialog.setView(input);
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        IdUser = firebaseAuth.getCurrentUser().getUid();
        alertDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        datalist.clear();
                        listfound.clear();
                        String inpu = input.getText().toString();
                        //if (inpu != null) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            datalist.add(dataSnapshot.getValue(User.class));
                        }


                        for (int i = 0; datalist.size() > i; i++) {
                            if (datalist.get(i).getId().equalsIgnoreCase(inpu) && !inpu.equals(IdUser)) {
                                listfound.add(datalist.get(i));
                                Toast.makeText(view.getContext(), "added", Toast.LENGTH_SHORT).show();
                            }
                        }

                        for (int i = 0; listcheck.size() > i; i++) {
                            for (int a = 0; listfound.size() > a; a++) {
                                if (listcheck.get(i)==(listfound.get(a))) {
                                    listfound.remove(listcheck.get(i));
                                    Toast.makeText(view.getContext(), "delete", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }
                        //  hashSet.addAll(listfound);
                        //hashSet.clear();
                        //listfound.addAll(hashSet);

                        adapter.notifyDataSetChanged();

                    }

                    //        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        alertDialog.show();
    }*/


}
