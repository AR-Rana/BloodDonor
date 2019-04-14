package com.example.rana.blooddonordiu;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    //private RecyclerView publicPostRecyclerView;

    private ArrayList<String> postID = new ArrayList<>();


    private ArrayList<String> personName = new ArrayList<>();
    private ArrayList<String> personPost = new ArrayList<>();
    private ArrayList<String> imageLink = new ArrayList<>();

    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;


    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    int maxPost = 100;

    private View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        //publicPostRecyclerView = v.findViewById(R.id.publicPostRecyclerView);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        //sweep refresh
        swipeRefreshLayout =
                (SwipeRefreshLayout) v.findViewById(R.id.home_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.indigo, R.color.lime);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPost();
                startRecyclerView();
            }
        });


        //startRecyclerView();
        refresh();

        return v;
    }

    private void refresh(){
        getAllPostId();
        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onFinish() {

                swipeRefreshLayout.setRefreshing(false);
                getAllPost();
                startRecyclerView();

            }
        }.start();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void getAllPostId() {
        postID.clear();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.child("allPost").getChildren()) {
                    Log.e(snap.getKey(), snap.getChildrenCount() + "");
                    postID.add(snap.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getAllPost() {

        Collections.shuffle(postID);
        personName.clear();
        personPost.clear();
        imageLink.clear();

        if (postID.size()<maxPost){
            maxPost = postID.size()-1;
        }
        for (int i = 0; i<=maxPost; i++){

            myRef.child("allPost").child(postID.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        personName.add(String.valueOf(snapshot.child("name").getValue()));
                        personPost.add(String.valueOf(snapshot.child("post").getValue()));
                        imageLink.add(String.valueOf(snapshot.child("imageLink").getValue()));

                        //Log.e("name: ", snapshot.child("name").getValue().toString());
                        //Log.e("post: ", snapshot.child("post").getValue().toString());
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
                //Log.d(TAG, "initRecyclerView: init recyclerview.");

                Collections.reverse(personName);
                Collections.reverse(personPost);
                Collections.reverse(imageLink);

                RecyclerView recyclerView = v.findViewById(R.id.publicPostRecyclerView);
                publicPostRecyclerViewAdapter adapter = new publicPostRecyclerViewAdapter(getActivity(), personName, personPost, imageLink);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            }
        }.start();
        swipeRefreshLayout.setRefreshing(false);

    }

}

