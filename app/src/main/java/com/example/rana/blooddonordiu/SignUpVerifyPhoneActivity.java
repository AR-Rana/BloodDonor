package com.example.rana.blooddonordiu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignUpVerifyPhoneActivity extends AppCompatActivity {

    private String fullName, nickName, mobileNo,email , address, bloodGroup, area,
            birthDate, lastDateOfBloodDonate, gender, interestToDonateBlood, joinDate;
    private String height, weight, dpLink, status;

    //It is the verification id that will be sent to the user
    private String mVerificationId;

    //The edittext to input the code
    private EditText editTextCode;

    //firebase auth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_verify_phone);

        fullName = getIntent().getStringExtra("fullName");
        nickName = getIntent().getStringExtra("nickName");
        mobileNo = getIntent().getStringExtra("mobileNo");
        email = getIntent().getStringExtra("email");
        address = getIntent().getStringExtra("address");
        bloodGroup = getIntent().getStringExtra("bloodGroup");
        area = getIntent().getStringExtra("area");
        birthDate = getIntent().getStringExtra("birthDate");
        lastDateOfBloodDonate = getIntent().getStringExtra("lastDateOfBloodDonate");
        gender = getIntent().getStringExtra("gender");
        interestToDonateBlood = getIntent().getStringExtra("interestToDonateBlood");
        joinDate = getIntent().getStringExtra("joinDate");
        height = getIntent().getStringExtra("height");
        weight = getIntent().getStringExtra("weight");
        dpLink = getIntent().getStringExtra("dpLink");
        status = getIntent().getStringExtra("status");

        sendVerificationCode(mobileNo);

        editTextCode = findViewById(R.id.suCode);
        //initializing objects
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.suBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    //return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });

    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(SignUpVerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("verify: ",e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignUpVerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //verification successful we will start the profile activity
                            Toast.makeText(SignUpVerifyPhoneActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                            registerUser();


                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
    public void registerUser(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sign Up");
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        UserInfo user = new UserInfo(
                fullName, nickName,
                birthDate, mobileNo,
                height,weight,email,
                area,address, bloodGroup, lastDateOfBloodDonate,
                dpLink,status,gender,interestToDonateBlood, joinDate
        );

        FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignUpVerifyPhoneActivity.this,"Successfully Registered",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                    Intent intent = new Intent(SignUpVerifyPhoneActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    //display the failure message
                    progressDialog.dismiss();
                    Toast.makeText(SignUpVerifyPhoneActivity.this,"Registation Failed",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
