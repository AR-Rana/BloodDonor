package com.example.rana.blooddonordiu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

/**
 * Created by User on 1/2/2018.
 */

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ConversationActivity";

    //private RecyclerView msgConversationRecyclerView;
    private EditText msgMessageEt;
    private ImageButton msgSendBtn;


    private String msgPersonDpLink;
    private String msgPersonName;
    private String msgPersonUid;

    private String conversationId;

    //vars
    private ArrayList<String> senderId = new ArrayList<>();
    private ArrayList<String> receiverId = new ArrayList<>();
    private ArrayList<String> message = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    String myUid;
    private int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ((AppCompatActivity) this).getSupportActionBar().hide();
        Log.d(TAG, "onCreate: started.");

        //msgConversationRecyclerView = findViewById(R.id.msgConversationRecyclerView);
        msgMessageEt = findViewById(R.id.msgMessageEt);
        msgSendBtn = findViewById(R.id.msgSendMsgBtn);

        getIncomingIntent();
        setBackgroundImage();

        msgSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (msgMessageEt.getText().toString().isEmpty()){
                    Toast.makeText(ConversationActivity.this,"Cann't send empty message",Toast.LENGTH_SHORT).show();
                }
                else {
                    sendMessage();
                }

                /*senderId.clear();
                receiverId.clear();
                message.clear();*/

                //getAllMessage();
                //startRecyclerView();
                msgMessageEt.setText("");
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        //to get firebase current user id
        myUid = mAuth.getCurrentUser().getUid();

        //conversationId = myUid+"_"+msgPersonUid;

        checkFriend();
        //getAllMessage();
        //startRecyclerView();

        dataChange();


    }


    private void checkFriend() {

        myRef.child("Users").child(myUid).child("friends").child(msgPersonUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    conversationId = String.valueOf(snapshot.child("conversationId").getValue());

                    Toast.makeText(ConversationActivity.this,conversationId,Toast.LENGTH_LONG).show();

                    if (conversationId.equals(myUid+"_"+msgPersonUid) || conversationId.equals(msgPersonUid+"_"+myUid)){
                        return;
                    }
                    else {
                        conversationId = myUid+"_"+msgPersonUid;
                        makeFriend();
                    }

                } catch (Exception e) {
                    Log.e("GetInfoError", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void dataChange() {
        try {
            myRef.child("Users").child(myUid).child("newMessageDataChange").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {

                        if (i!=0){
                            Toast.makeText(ConversationActivity.this,
                                    "New Message",Toast.LENGTH_LONG).show();
                            getAllMessage();
                            startRecyclerView();
                        }
                        else {
                            getAllMessage();
                            startRecyclerView();
                        }


                    } catch (Exception e) {
                        Log.e("GetInfoError", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }catch (Exception e){
            Toast.makeText(ConversationActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void makeFriend() {
        //to get current date and time
        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance().format(calendar.getTime());
        Date currentTime = Calendar.getInstance().getTime();
        String time = currentTime.toString();

        myRef.child("Users").child(myUid).child("friends").child(msgPersonUid).child("date").setValue(date);
        myRef.child("Users").child(myUid).child("friends").child(msgPersonUid).child("time").setValue(time);
        myRef.child("Users").child(myUid).child("friends").child(msgPersonUid).child("conversationId").setValue(conversationId);


        myRef.child("Users").child(msgPersonUid).child("friends").child(myUid).child("date").setValue(date);
        myRef.child("Users").child(msgPersonUid).child("friends").child(myUid).child("time").setValue(time);
        myRef.child("Users").child(msgPersonUid).child("friends").child(myUid).child("conversationId").setValue(conversationId);

        //myRef.child("allConversation").child()

    }

    private void sendMessage(){

        //to get current date and time
        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance().format(calendar.getTime());
        Date currentTime = Calendar.getInstance().getTime();
        String time = currentTime.toString();

        String message = msgMessageEt.getText().toString();

        if (!message.isEmpty()){
            myRef.child("allConversation").child(conversationId).child(time).child("sender").setValue(myUid);
            myRef.child("allConversation").child(conversationId).child(time).child("receiver").setValue(msgPersonUid);
            myRef.child("allConversation").child(conversationId).child(time).child("message").setValue(message);
            myRef.child("allConversation").child(conversationId).child(time).child("time").setValue(time);
            myRef.child("allConversation").child(conversationId).child(time).child("date").setValue(date);

            /*Random rand = new Random();
            int n = rand.nextInt(50);
            myRef.child("allConversation").child(conversationId).child("refresh").setValue("refresh"+n);*/

            myRef.child("Users").child(myUid).child("newMessageDataChange").setValue(time);
            myRef.child("Users").child(msgPersonUid).child("newMessageDataChange").setValue(time);
        }
    }


    private void getAllMessage(){

        senderId.clear();
        receiverId.clear();
        message.clear();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.child("allConversation").child(conversationId).getChildren()) {
                    Log.e(snap.getKey(), snap.getChildrenCount() + "");
                    final String id = snap.getKey();

                    //Toast.makeText(MyPostsActivity.this, id, Toast.LENGTH_SHORT).show();

                    myRef.child("allConversation").child(conversationId).child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {
                                senderId.add(String.valueOf(snapshot.child("sender").getValue()));
                                receiverId.add(String.valueOf(snapshot.child("receiver").getValue()));
                                message.add(String.valueOf(snapshot.child("message").getValue()));
                            } catch (Exception e) {
                                Log.e("GetInfoError", e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        startRecyclerView();
    }

    private void startRecyclerView() {

        Collections.reverse(senderId);
        Collections.reverse(receiverId);
        Collections.reverse(message);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressDialog.show();
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();

                RecyclerView recyclerView = findViewById(R.id.msgConversationRecyclerView);
                recyclerView.scrollToPosition(message.size()-2);
                ConversationRecyclerViewAdapter adapter = new ConversationRecyclerViewAdapter(ConversationActivity.this, senderId, receiverId, message);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ConversationActivity.this));

            }
        }.start();
        progressDialog.dismiss();
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("imageLink") && getIntent().hasExtra("personName") && getIntent().hasExtra("personUid")){
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            msgPersonDpLink = getIntent().getStringExtra("imageLink");
            msgPersonName = getIntent().getStringExtra("personName");
            msgPersonUid = getIntent().getStringExtra("personUid");

        }
    }


    private void setBackgroundImage(){
        Log.d(TAG, "setImage: setting te image and name to widgets.");

        ImageView image = findViewById(R.id.msgBackgroundImage);
        Glide.with(this)
                .asBitmap()
                .load(msgPersonDpLink)
                .into(image);
    }

}
