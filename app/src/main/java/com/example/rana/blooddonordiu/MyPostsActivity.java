package com.example.rana.blooddonordiu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MyPostsActivity extends AppCompatActivity {

    private EditText postET;
    private ImageButton postImageBtn;
    private Button postBtn;
    private ImageView postImageView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String name;
    private String post;
    private String postImageLink = "empty";
    private String date;
    private String time;

    private String uid;


    private static final String TAG = "MsgPostActivity";

    //vars
    private ArrayList<String> personName = new ArrayList<>();
    private ArrayList<String> personPost = new ArrayList<>();
    private ArrayList<String> imageLink = new ArrayList<>();
    private ArrayList<String> postDate = new ArrayList<>();
    private ArrayList<String> postTime = new ArrayList<>();

    //for update image
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;


    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        ((AppCompatActivity) this).getSupportActionBar().hide();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MyPostsActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //to get firebase current user id
        uid = mAuth.getCurrentUser().getUid();

        postET = findViewById(R.id.myPostET);
        postImageBtn = findViewById(R.id.myPostImageBtn);
        postBtn = findViewById(R.id.myPostBtn);
        postImageView = findViewById(R.id.myPostImageIV);

        postImageView.setVisibility(View.GONE);


        //to get current user name
        myRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    name = snapshot.child(uid).child("fullName").getValue().toString();
                } catch (Exception e) {
                    Log.e("GetNameError", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        postImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
                postImageView.setVisibility(View.VISIBLE);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPostToFirebase();
            }
        });

        //sweep refresh
        swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.my_post_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.indigo, R.color.lime);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPost();
            }
        });


        Toast.makeText(MyPostsActivity.this, "MyPostActivity Start", Toast.LENGTH_SHORT).show();
        getAllPost();
    }


    private void uploadPostToFirebase() {

        post = postET.getText().toString();

        postET.setText("");

        //to get current date and time
        Calendar calendar = Calendar.getInstance();
        date = DateFormat.getDateInstance().format(calendar.getTime());
        Date currentTime = Calendar.getInstance().getTime();
        time = currentTime.toString();


        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("postImage/"+uid+time+".jpg");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.child("postImage/"+uid+time+".jpg").getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Got the download URL for 'users/me/profile.png'

                                            //postImageLink = String.valueOf(uri);
                                            myRef.child("allPost").child(time).child("imageLink").setValue(String.valueOf(uri));

                                            Toast.makeText(MyPostsActivity.this, "Url: "+uri, Toast.LENGTH_SHORT).show();
                                            //finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    Toast.makeText(MyPostsActivity.this, "No Url", Toast.LENGTH_SHORT).show();
                                }
                            });

                            //uploadToFirebase();
                            progressDialog.dismiss();
                            Toast.makeText(MyPostsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MyPostsActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        else {
            myRef.child("allPost").child(time).child("imageLink").setValue(postImageLink);
        }

        myRef.child("allPost").child(time).child("uid").setValue(uid);
        myRef.child("allPost").child(time).child("name").setValue(name);
        myRef.child("allPost").child(time).child("post").setValue(post);
        myRef.child("allPost").child(time).child("date").setValue(date);
        myRef.child("allPost").child(time).child("time").setValue(time);
        getAllPost();
    }

    public void getAllPost() {

        postImageView.setVisibility(View.GONE);

        personName.clear();
        personPost.clear();
        imageLink.clear();
        postDate.clear();
        postTime.clear();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.child("allPost").getChildren()) {
                    Log.e(snap.getKey(), snap.getChildrenCount() + "");
                    final String id = snap.getKey();

                    //Toast.makeText(MyPostsActivity.this, id, Toast.LENGTH_SHORT).show();

                    myRef.child("allPost").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {
                                //personName.add(snapshot.child(id).child("uid").getValue().toString());

                                if (String.valueOf(snapshot.child("uid").getValue()).equals(uid)){
                                    personName.add(String.valueOf(snapshot.child("name").getValue()));
                                    personPost.add(String.valueOf(snapshot.child("post").getValue()));
                                    imageLink.add(String.valueOf(snapshot.child("imageLink").getValue()));
                                    postDate.add(String.valueOf(snapshot.child("date").getValue()));
                                    postTime.add(String.valueOf(snapshot.child("time").getValue()));
                                }

                                //Log.e("name: ", snapshot.child(uid).child("post").child(id).child("name").getValue().toString());
                                //Log.e("post: ", snapshot.child(uid).child("post").child(id).child("post").getValue().toString());
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

        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onFinish() {

                swipeRefreshLayout.setRefreshing(false);

                Log.i("Finished", "All of sec Done");
                Log.d(TAG, "initRecyclerView: init recyclerview.");

                Collections.reverse(personName);
                Collections.reverse(personPost);
                Collections.reverse(imageLink);

                RecyclerView recyclerView = findViewById(R.id.myPostRecyclerView);
                MyPostRecyclerViewAdapter adapter = new MyPostRecyclerViewAdapter(MyPostsActivity.this, personName, personPost, imageLink, postDate, postTime);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MyPostsActivity.this));

            }
        }.start();
        swipeRefreshLayout.setRefreshing(false);

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                postImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
