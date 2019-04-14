package com.example.rana.blooddonordiu;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;

    private String uid;
    private String uBloodGroup;
    private String signInStatus;
    private String uMobileNo;
    private String uDpLink;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private ImageView personPhoto;
    private TextView personNameTv;
    private TextView personEmailTv;

    private boolean personPhotoClick = false;

    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionManager = new PermissionManager() {        };
        permissionManager.checkAndRequestPermissions(this);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        mDrawerLayout = findViewById(R.id.mainActivity_drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //drawer navigation options
        navigationView = findViewById(R.id.drawer_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_profile:
                        Intent pa = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(pa);
                        break;
                    case R.id.nav_myPosts:
                        Intent myPost = new Intent(MainActivity.this, MyPostsActivity.class);
                        startActivity(myPost);
                        break;
                    case R.id.nav_info:
                        Intent ia = new Intent(MainActivity.this, InformationActivity.class);
                        startActivity(ia);
                        break;
                    case R.id.nav_signOut:
                        mAuth.getInstance().signOut();
                        finish();
                        Intent sa = new Intent(MainActivity.this, SignInWithPhoneActivity.class);
                        startActivity(sa);
                        break;
                    case R.id.nav_send:

                        break;
                }
                return false;
            }
        });


        //Header Navigation View Term

        View headerView = navigationView.getHeaderView(0);
        personPhoto = headerView.findViewById(R.id.personPhoto);
        personNameTv = headerView.findViewById(R.id.personNameTV);
        personEmailTv = headerView.findViewById(R.id.personEmailTV);

        setHeaderView();

    }

    private void setHeaderView() {
        myRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                //Toast.makeText(UpdateDeviceInfo.this,snapshot.child(selectedDevice).child("devLocation").getValue().toString(),Toast.LENGTH_SHORT ).show();
                //uid[position] = id;

                try {
                    uDpLink = snapshot.child(uid).child("dpLink").getValue().toString();
                    uBloodGroup = snapshot.child(uid).child("bloodGroup").getValue().toString();
                    uMobileNo = snapshot.child(uid).child("mobileNo").getValue().toString();

                    Picasso.get().load(uDpLink).fit().centerCrop().into(personPhoto);
                    personNameTv.setText(snapshot.child(uid).child("fullName").getValue().toString());
                    personEmailTv.setText(snapshot.child(uid).child("email").getValue().toString());


                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {

        if (mAuth.getCurrentUser() == null) {
            //handle the current user
            Intent intent = new Intent(MainActivity.this, SignInWithPhoneActivity.class);
            startActivity(intent);
        } else {
            signInStatus = "yes";
        }
        //to get firebase current user id
        uid = mAuth.getCurrentUser().getUid();
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_search:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.nav_message:
                    selectedFragment = new MsgPersonListFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
