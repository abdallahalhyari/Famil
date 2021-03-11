package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class profile extends AppCompatActivity {
    private static final int GALLERY_INTENT_CODE = 1023;
    TextView fullName, email, phone, verifyMsg, userID, id;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button resendCode;
    Button resetPassLocal, changeProfileImage, addmember;
    FirebaseUser user;
    ImageView profileImage, id_image;
    StorageReference storageReference;
    DocumentReference documentReference;
   static Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        phone = findViewById(R.id.profilePhone);
        fullName = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        resetPassLocal = findViewById(R.id.resetPasswordLocal);
        userID = findViewById(R.id.id_number);
        profileImage = findViewById(R.id.profileImage);
        changeProfileImage = findViewById(R.id.changeProfile);
        addmember = findViewById(R.id.Add_member);
        id = findViewById(R.id.id);
        fAuth = FirebaseAuth.getInstance();
        id_image = findViewById(R.id.id_m);
        user = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        documentReference = fStore.collection("user").document(user.getUid());

        StorageReference profileRef = storageReference.child("users/" + user.getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
              image=uri;
            }
        });


        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);





        documentReference = fStore.collection("user").document(user.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    fullName.setText(documentSnapshot.getString("location"));
                    fullName.setText(documentSnapshot.getString("name"));
                    email.setText(documentSnapshot.getString("email"));
                    phone.setText(documentSnapshot.getString("phone"));
                    userID.setText(documentSnapshot.getString("Id"));

                    if (Integer.parseInt(userID.getText().toString()) != 1) {

                        userID.setVisibility(View.VISIBLE);
                        id_image.setVisibility(View.VISIBLE);
                        id.setVisibility(View.VISIBLE);
                        addmember.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d("tag", "onEvent: Document do not exists");
                }

            }

        });
         /*else {
            Toast.makeText(this, "11111111111111111", Toast.LENGTH_SHORT).show();
            documentReference = fStore.collection("user").document(user.getUid());
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        fullName.setText(documentSnapshot.getString("location"));
                        fullName.setText(documentSnapshot.getString("name"));
                        email.setText(documentSnapshot.getString("email"));
                        phone.setText(documentSnapshot.getString("phone"));
                        userID.setText(documentSnapshot.getString("Id"));

                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

        }*/


        resetPassLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetPassword = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(profile.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });

                passwordResetDialog.create().show();

            }
        });

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent i = new Intent(v.getContext(), edit_profile.class);
                i.putExtra("fullName", fullName.getText().toString());
                i.putExtra("email", email.getText().toString());
                i.putExtra("phone", phone.getText().toString());
                startActivity(i);


            }
        });


    }


    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout

        startActivity(new Intent(getApplicationContext(), login.class));
        finish();
    }


    public void Go_ADD(View view) {
        Intent intent = new Intent(view.getContext(), listEmail.class);
        startActivity(intent);
    }
}