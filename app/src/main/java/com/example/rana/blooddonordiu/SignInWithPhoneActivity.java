package com.example.rana.blooddonordiu;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInWithPhoneActivity extends AppCompatActivity {

    private EditText editTextMobile;
    private TextView signUpTV;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_with_phone);

        editTextMobile = findViewById(R.id.editTextMobile);
        signUpTV = findViewById(R.id.signInSignUpTV);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        findViewById(R.id.signInContinueBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobile = editTextMobile.getText().toString().trim();

                if(mobile.isEmpty() || mobile.length() < 10){
                    editTextMobile.setError("Enter a valid mobile");
                    editTextMobile.requestFocus();
                    return;
                }

                checkingUser();
            }
        });

        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInWithPhoneActivity.this, SignUpWithPhoneActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkingUser() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sign Up");
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.child("Users").getChildren()) {
                    Log.e(snap.getKey(), snap.getChildrenCount() + "");


                    myRef.child("Users").child(snap.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {

                                String mo = String.valueOf(snapshot.child("mobileNo").getValue());
                                Log.d("mobile",mo);

                                if (mo.equals("+88"+mobile)){
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SignInWithPhoneActivity.this, SignInVerifyPhoneActivity.class);
                                    intent.putExtra("mobile", mobile);
                                    startActivity(intent);
                                    finish();
                                    return;
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        progressDialog.dismiss();
        Toast.makeText(this,"Register First", Toast.LENGTH_SHORT).show();
    }
}
