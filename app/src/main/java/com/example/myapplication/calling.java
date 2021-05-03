package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;


public class calling extends AppCompatActivity {

    private String username;

    private String friendsUsername;
    private boolean isPeerConnected = false;
    private boolean isAudio = true;
    private boolean isVideo = true;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button callBtn;
    ListView recview;
    Adapter adapter;
    TextView friendNameEdit;
    ImageView toggleAudioBtn, toggleVideoBtn;
    WebView webView;
    ArrayList<String> datalist;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    String id;
    int i = 0;
    ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        webView = findViewById(R.id.webView);
        recview = (ListView) findViewById(R.id.recycler);
        datalist = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
        recview.setAdapter(itemsAdapter);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        fStore = FirebaseFirestore.getInstance();
        recview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                friendsUsername = friendNameEdit.getText().toString();
               friendsUsername= recview.getAdapter().getItem(position).toString();
                sendCallRequest();
            }
        });

        documentReference = fStore.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    username = value.getString("name");
                    id = value.getString("parentid");
                    DatabaseReference databaseReference = firebaseDatabase.getReference().child(id);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            datalist.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                datalist.add(dataSnapshot.getValue(User.class).getName());
                            }

                            for (; datalist.size()> i; i++) {
                                if (datalist.get(i).equals(username)) {
                                    datalist.remove(i);
                                }
                            }

                            itemsAdapter.notifyDataSetChanged();
                            
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

        toggleAudioBtn = findViewById(R.id.toggleAudioBtn);
        toggleAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAudio = !isAudio;
                callJavascriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");

                if (isAudio) {
                    toggleAudioBtn.setImageResource(R.drawable.ic_baseline_mic_24);
                } else
                    toggleAudioBtn.setImageResource(R.drawable.ic_baseline_mic_off_24);

            }
        });
        toggleVideoBtn = findViewById(R.id.toggleVideoBtn);
        toggleVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideo = !isVideo;
                callJavascriptFunction("javascript:toggleVideo(\"" + isVideo + "\")");
                if (isVideo) {
                    toggleVideoBtn.setImageResource(R.drawable.ic_baseline_videocam_24);
                } else toggleVideoBtn.setImageResource(R.drawable.ic_baseline_videocam_off_24);
            }
        });


        setupWebView();
    }

    private final void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show();
            return;
        }

        databaseReference.child(friendsUsername).child("incoming").setValue(username);
        databaseReference.child(friendsUsername).child("isAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    listenForConnId();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private final void listenForConnId() {
        databaseReference.child(friendsUsername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null)
                    return;
                switchToControls();
                callJavascriptFunction("javascript:startCall(\"" + snapshot.getValue() + "\")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("JavascriptInterface")
    private final void setupWebView() {

        webView.setWebChromeClient((WebChromeClient) (new WebChromeClient() {
            public void onPermissionRequest(@Nullable PermissionRequest request) {
                if (request != null) {
                    request.grant(request.getResources());
                }

            }
        }));

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new javasctiptinterface(this), "Android");
        loadVideoCall();
    }

    private final void loadVideoCall() {
        String filePath = "file:android_asset/call.html";
        webView.loadUrl(filePath);
        webView.setWebViewClient((WebViewClient) (new WebViewClient() {
            public void onPageFinished(@Nullable WebView view, @Nullable String url) {
                initializePeer();
            }
        }));

    }

    String uniqueId = "";

    private final void initializePeer() {
        uniqueId = getUniqueID();
        callJavascriptFunction("javascript:init(\"" + uniqueId + "\")");
        databaseReference.child(username).child("incoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onCallRequest(String.valueOf(snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private final void onCallRequest(String caller) {
        RelativeLayout callLayout = findViewById(R.id.callLayout);
        TextView incomingCallTxt = findViewById(R.id.incomingCallTxt);
        ImageView acceptBtn = findViewById(R.id.acceptBtn);
        ImageView rejectBtn = findViewById(R.id.rejectBtn);
        if (!caller.equals("null")) {
            callLayout.setVisibility(View.VISIBLE);
            incomingCallTxt.setText(caller + " is calling...");

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child(username).child("connId").setValue(uniqueId);
                    databaseReference.child(username).child("isAvailable").setValue(true);
                    callLayout.setVisibility(View.GONE);
                    switchToControls();
                }
            });
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child(username).child("incoming").setValue(null);
                    callLayout.setVisibility(View.GONE);
                }
            });

        } else {
            return;
        }
    }

    private final void switchToControls() {
        RelativeLayout inputLayout = findViewById(R.id.inputLayout);
        LinearLayout callControlLayout = findViewById(R.id.callControlLayout);
        inputLayout.setVisibility(View.GONE);
        callControlLayout.setVisibility(View.VISIBLE);
        recview.setVisibility(View.GONE);
    }


    private final String getUniqueID() {
        return UUID.randomUUID().toString();
    }

    private final void callJavascriptFunction(String functionString) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(functionString, null);
            }
        });
    }


    public final void onPeerConnected() {
        isPeerConnected = true;
    }

    public void onBackPressed() {
        this.finish();
    }

    protected void onDestroy() {
        databaseReference.child(username).setValue(null);
        webView.loadUrl("about:blank");
        super.onDestroy();
    }
}
