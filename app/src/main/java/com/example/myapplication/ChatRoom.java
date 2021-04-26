package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatRoom extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference root;
    private String temp_key;
    private Button btn_send_msg;
    private EditText input_msg;
    private ListView listView_chat;
    ArrayList<String> list_chat;
    Adapter_Chat arrayAdapter;
    ArrayList<String> datalist;
    private String name;
    FirebaseFirestore fStore;
    Button send;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    DocumentReference documentReference;
    String id;
    View head;
    FirebaseUser user;
    ImageView imageView;
    Toolbar toolbar;
    Intent intent;
    BottomNavigationView bottomNavigationView;
    private DrawerLayout drawer;
    String token;
    int i;

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
                                message_root.updateChildren(map2);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        datalist.clear();


                    }
                });


                root.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Add_Chat(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Add_Chat(dataSnapshot);

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        token = Objects.requireNonNull(task.getResult()).getToken();
                        FirebaseDatabase.getInstance().getReference("Tokens").child(id).child(fAuth.getCurrentUser().getUid()).setValue(token);
                    }
                });


    }


    private String chat_msg, chat_user_name;

    private void Add_Chat(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        input_msg.setText("");
        while (i.hasNext()) {

            chat_user_name = (String) ((DataSnapshot) i.next()).getValue();

            list_chat.add(chat_user_name);
            arrayAdapter.notifyDataSetChanged();
            listView_chat.setSelection(list_chat.size());
        }


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
            ;
        }
    }

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
            toolbar.setTitle("Home");
            Intent intent = new Intent(getApplicationContext(), Register.class);
            fAuth.signOut();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id == R.id.nav_call) {
            toolbar.setTitle("Home");
            drawer.close();
            Intent intent = new Intent(getApplicationContext(), calling.class);
            startActivity(intent);

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


}