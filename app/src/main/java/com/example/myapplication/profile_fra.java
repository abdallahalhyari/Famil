package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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

import java.security.Permission;

import javax.annotation.Nullable;

public class profile_fra extends Fragment {
    private static final int GALLERY_INTENT_CODE = 1023;
    TextView fullName, email, phone, verifyMsg, userID, id;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button resendCode;
    String name;
    Button resetPassLocal, changeProfileImage, addmember, logout, member;
    FirebaseUser user;
    ImageView profileImage, id_image;
    StorageReference storageReference;
    DocumentReference documentReference;
    static Uri image;
    Intent intent;
    EditText ed;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public profile_fra() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_fra, container, false);
        phone = v.findViewById(R.id.profilePhone);
        fullName = v.findViewById(R.id.profileName);
        email = v.findViewById(R.id.profileEmail);
        resetPassLocal = v.findViewById(R.id.resetPasswordLocal);
        userID = v.findViewById(R.id.id_number);
        profileImage = v.findViewById(R.id.profileImage);
        changeProfileImage = v.findViewById(R.id.changeProfile);
        addmember = v.findViewById(R.id.Add_member);
        id = v.findViewById(R.id.id);
        member = v.findViewById(R.id.Add_member);
        logout = v.findViewById(R.id.logout);
        fAuth = FirebaseAuth.getInstance();
        id_image = v.findViewById(R.id.id_m);
        user = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        StorageReference profileRef = storageReference.child("users/" + user.getEmail() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });


        resendCode = v.findViewById(R.id.resendCode);
        verifyMsg = v.findViewById(R.id.verifyMsg);

        documentReference = fStore.collection("user").document(user.getUid());
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    fullName.setText(documentSnapshot.getString("name"));
                    email.setText(documentSnapshot.getString("email"));
                    phone.setText(documentSnapshot.getString("phone"));
                    userID.setText(documentSnapshot.getString("Id"));
                    name = documentSnapshot.getString("name");
                    if (!userID.getText().toString().equals("1")) {

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
        resetPassLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
  //           passwordResetDialog.create().show();
                showDialog(getActivity(), "Reset Password ?","Enter New Password > 6 Characters long.");
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Intent intent = new Intent(v.getContext(), Register.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), listEmail.class);
                startActivity(intent);
            }
        });


        return v;
    }

    public void showDialog(Activity activity, String msg,String msg2) {
        FirebaseAuth fAuth;
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.round_corner);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        TextView text2 = (TextView) dialog.findViewById(R.id.text_dialog2);
        text2.setText(msg2);
        ed = dialog.findViewById(R.id.ema);
        Button dialogButton1 = (Button) dialog.findViewById(R.id.btn1);
        dialogButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = ed.getText().toString().trim();
                user.updatePassword(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                    }
                });


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
