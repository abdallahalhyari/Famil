package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.internal.Sleeper;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

class Adapter_Chat extends ArrayAdapter<String> {
    private final ArrayList<String> dataSet;
    Context mContext;
    String na;

    public Adapter_Chat(ArrayList<String> arrayList, Context context) {
        super(context, R.layout.chating, arrayList);
        dataSet = arrayList;
        mContext = context;
    }


    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageView imageView;
    }


    private int lastPosition = -1;
    int i = 0;
    ViewHolder viewHolder;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @SuppressLint("RestrictedApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chating, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.imageView = convertView.findViewById(R.id.ima);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        if (!dataSet.get(position).contains("users/")) {
            lastPosition = position;
            viewHolder.txtName.setText(dataSet.get(position));
            viewHolder.imageView.setVisibility(View.GONE);

        } else {

            try {

                viewHolder.imageView.setVisibility(View.VISIBLE);

                StorageReference profileRef = storageReference.child(dataSet.get(position));
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DocumentReference documentReference;

                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d("TAG", "aEmail not sent");
                        Picasso.get().load(uri).into(viewHolder.imageView);
                        // viewHolder.imageView.setImageURI(uri);
                        //  DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child(dataSet.get(position));
                        // viewHolder.txtName.setText( databaseReference2.getKey());

                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("name");

                        databaseReference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String[] separated = dataSet.get(position).split("\\/");
                                String[] separated2 = separated[1].split("/");
                                    for (DataSnapshot Snapshot : snapshot.getChildren()) {
                                        if (Snapshot.getKey().equals(separated2[0])) {
                                        viewHolder.txtName.setText(Snapshot.getValue(String.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        return convertView;
    }
}