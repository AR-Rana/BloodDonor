package com.example.rana.blooddonordiu;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MsgPersonListFragment extends Fragment {


    private static final String TAG = "MsgPersonListFragment";

    //vars
    private ArrayList<String> personName = new ArrayList<>();
    private ArrayList<String> dpLink = new ArrayList<>();
    private ArrayList<String> personUid = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String currentUid;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_msg_person_list, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        Log.d(TAG, "onCreate: started.");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();//getReferenceFromUrl("https://blood-donor-diu.firebaseio.com/");

        //to get firebase current user id
        currentUid = mAuth.getCurrentUser().getUid();

        getUsersInfo();

        initRecyclerView();

        return v;
    }

    public void getUsersInfo() {

        personName.clear();
        dpLink.clear();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.child("Users").getChildren()) {
                    Log.e(snap.getKey(),snap.getChildrenCount() + "");
                    final String id = snap.getKey();

                    if (!id.equals(currentUid)){
                        myRef.child("Users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                try {
                                    personUid.add(id);
                                    dpLink.add(snapshot.child(id).child("dpLink").getValue().toString());
                                    personName.add(snapshot.child(id).child("fullName").getValue().toString());

                                    Log.e("name: ",snapshot.child(id).child("fullName").getValue().toString());
                                    Log.e("dpLink: ",snapshot.child(id).child("dpLink").getValue().toString());
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void initRecyclerView() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        //progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);



        CountDownTimer countDownTimer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressDialog.show();
            }

            @Override
            public void onFinish() {

                progressDialog.dismiss();
                Log.i("Finished","All of sec Done");
                Log.d(TAG, "initRecyclerView: init recyclerview.");


                RecyclerView recyclerView = v.findViewById(R.id.recyclerv_view);
                MsgPersonListRecyclerViewAdapter adapter = new MsgPersonListRecyclerViewAdapter(getActivity(), personName, dpLink, personUid);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        }.start();


    }
}
