package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class ChatRoom extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference root;
    private String temp_key;
    private Button btn_send_msg;
    ImageView ivImage;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private EditText input_msg;
    private ListView listView_chat;
    ArrayList<String> list_chat;
    String na;
    Adapter_Chat arrayAdapter;
    ArrayList<String> datalist;
    private String name;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    DocumentReference documentReference;
    String id;
    View head;
    FirebaseUser user;
    ImageView imageView;
    Toolbar toolbar;
    Intent intent;
    Button send;
    BottomNavigationView bottomNavigationView;
    private DrawerLayout drawer;
    String token;
    int i;
    String ida;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        toolbar = findViewById(R.id.toolbar);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getColor(R.color.toggle));
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        datalist = new ArrayList<>();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.navigation_notifications:
                        intent = new Intent(getApplicationContext(), Help_Press.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.navigation_dashboard:
                        return true;
                }
                return false;
            }
        });

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        update();

        documentReference = fStore.collection("user").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                id = value.getString("parentid");
                root = FirebaseDatabase.getInstance().getReference().child("ChatRoom").child(id);
                listView_chat = (ListView) findViewById(R.id.View_chat);
                input_msg = (EditText) findViewById(R.id.input_msg);
                btn_send_msg = (Button) findViewById(R.id.btn_send_msg);
                list_chat = new ArrayList<>();
                name = value.getString("name");
                arrayAdapter = new Adapter_Chat(list_chat, ChatRoom.this);
                listView_chat.setAdapter(arrayAdapter);
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                token = task.getResult().getToken();
                                FirebaseDatabase.getInstance().getReference("Tokens").child(id).child(fAuth.getCurrentUser().getUid()).setValue(token);
                            }
                        });
                btn_send_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Map<String, Object> map = new HashMap<String, Object>();
                        temp_key = root.push().getKey();
                        root.updateChildren(map);

                        DatabaseReference message_root = root.child(temp_key);
                        Map<String, Object> map2 = new HashMap<String, Object>();
                        map2.put("name", name + " : " + input_msg.getText().toString());
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Tokens").child(id);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    datalist.add(dataSnapshot.getValue(String.class));
                                }
                                for (i = 0; datalist.size() > i; i++) {
                                    if (!token.equals(datalist.get(i))) {
                                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(datalist.get(i), name, input_msg.getText().toString(), getApplicationContext(), ChatRoom.this);
                                        notificationsSender.SendNotifications();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        message_root.updateChildren(map2);
                        datalist.clear();


                    }
                });


                root.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Add_Chat(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                         Add_Chat(dataSnapshot);

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        });
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }

            }

        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            askPermissions();
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectImage();
            }
        });
    }


    private String chat_msg;

    private void Add_Chat(DataSnapshot dataSnapshot) {

        Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator();
        input_msg.setText("");
        while (i.hasNext()) {
            String chat_user_name = (String) (i.next()).getValue();
            list_chat.add(chat_user_name);
        }
        arrayAdapter.notifyDataSetChanged();
        listView_chat.setSelection(list_chat.size());

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            UIUtil.hideKeyboard(this);
        } else {
            intent = new Intent(getApplicationContext(), ChatRoom.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            toolbar.setTitle("Profile");
            drawer.close();
            //     bottomNavigationView.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_chate_room, new profile_fra()).commit();
        }
        if (id == R.id.nav_message) {
            toolbar.setTitle("Home");
            drawer.close();
            intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        if (id == R.id.nav_logout) {
          showDialog(ChatRoom.this,"Are you sure to exit? ","");

        }
        if (id == R.id.nav_call) {
            toolbar.setTitle("Home");
            drawer.close();
            Intent intent = new Intent(getApplicationContext(), calling.class);
            startActivity(intent);

        }
        if (id == R.id.nav_share) {
            //  File im=new File(getApplicationContext().getApplicationInfo().sourceDir);
            drawer.close();
            Intent shareAPkIntent = new Intent();
            shareAPkIntent.setAction(Intent.ACTION_SEND);
            shareAPkIntent.setType("text/plain");
            shareAPkIntent.putExtra(Intent.EXTRA_SUBJECT, "RaqipJo");
            shareAPkIntent.putExtra(Intent.EXTRA_TEXT, "Download this Application now:-https://drive.google.com/file/d/1A7NVMaCN6zJVqNKbq-k_VrgdQGGw4TpZ/view?usp=sharing");
            //BuildConfig.APPLICATION_ID + ".provider", im));

            //  shareAPkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            this.startActivity(Intent.createChooser(shareAPkIntent, "Share APK"));

        }
        if (id == R.id.nav_send) {
            toolbar.setTitle("Home");
            drawer.close();
            String recipientList = "Raqibjo@gmail.com";
            String[] recipients = recipientList.split(",");
            String subject = "";
            String message = "";
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("malito"));
            intent.setType("text/plain");
            intent.setPackage("com.google.android.gm");
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(Intent.createChooser(intent, "Choose an email client"));

        }
        return true;
    }

    public void update() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        head = navigationView.getHeaderView(0);
        TextView email = head.findViewById(R.id.nav_email);
        imageView = head.findViewById(R.id.nav_image);
        email.setText(fAuth.getCurrentUser().getEmail());
        StorageReference profileRef = storageReference.child("users/" + user.getEmail() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //     Picasso.get().load(uri).into(imageView);
                Glide.with(ChatRoom.this).load(uri).into(imageView);
            }
        });
        documentReference = fStore.collection("user").document(user.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                TextView name = head.findViewById(R.id.nav_name);
                name.setText(value.getString("name"));
            }
        });

    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1);
    }

    private void SelectImage() {

        final CharSequence[] items = {"Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Gallery")) {

                    @SuppressLint("IntentReset")
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                //firebaseDatabase.getReference().child("name").child( id).setValue(na);

                DatabaseReference databaseReference = firebaseDatabase.getReference().child("Tokens").child(id);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            datalist.add(dataSnapshot.getValue(String.class));
                        }
                        for (i = 0; datalist.size() > i; i++) {
                            if (!token.equals(datalist.get(i))) {
                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(datalist.get(i), name, "sent image", getApplicationContext(), ChatRoom.this);
                                notificationsSender.SendNotifications();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                String id = getUniqueID();
                Uri selectedImageUri = data.getData();
                final StorageReference fileRef = storageReference.child("users/" + id + "/profile.jpg");
                fileRef.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        temp_key = root.push().getKey();
                        root.updateChildren(map);
                        DatabaseReference message_root = root.child(temp_key);
                        Map<String, Object> map2 = new HashMap<String, Object>();
                        map2.put("name", "users/" + id + "/profile.jpg");
                        documentReference = fStore.collection("user").document(fAuth.getCurrentUser().getUid());
                        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("name").child(id);
                                databaseReference2.setValue(value.getString("name"));
//.child("users/" + id + "/profile.jpg")
                            }
                        });




                        message_root.updateChildren(map2);
                        datalist.clear();
                    }
                });

            }

        }
    }

    private final String getUniqueID() {
        return UUID.randomUUID().toString();
    }
    public void showDialog(Activity activity, String msg, String msg2) {

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
                fAuth.signOut();
                finish();
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

