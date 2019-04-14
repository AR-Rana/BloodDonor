package com.example.rana.blooddonordiu;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SignUpWithPhoneActivity extends AppCompatActivity {

    private EditText fullNameET, nickNameET,mobileET,heightET,weightET, emailET,passwordET,addressET;
    private Spinner bloodGoupSpinner, areaSpinner;
    private CheckBox interestedCheckBox;
    private TextView birthDateTV,lastDateOfDonateBloodTV;
    private Button cancelBtn, signUpBtn;

    private DatePickerDialog.OnDateSetListener bDaySelectedListener;
    private DatePickerDialog.OnDateSetListener lastBloodDonationDateSelectedListener;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    private String fullName = "empty", nickName = "empty", mobileNo = "empty",email = "empty", address = "empty", bloodGroup = "empty", area = "empty",
                            birthDate = "empty", lastDateOfBloodDonate = "empty", gender = "empty", interestToDonateBlood = "no", joinDate="empty";
    private String height = "empty", weight = "empty", dpLink = "empty", status = "Hey there, I'm using blood donor app";

    private RadioButton rdMale, rdFemale;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_phone);
        ((AppCompatActivity) this).getSupportActionBar().hide();

        progressDialog = new ProgressDialog(SignUpWithPhoneActivity.this);

        fullNameET = findViewById(R.id.fullNameET);
        nickNameET = findViewById(R.id.nickNameET);
        mobileET = findViewById(R.id.mobileNoET);
        //heightET = findViewById(R.id.heightET);
        //weightET = findViewById(R.id.weightET);
        emailET = findViewById(R.id.emailET);
        //passwordET = findViewById(R.id.passwordET);
        //addressET = findViewById(R.id.addressET);

        bloodGoupSpinner = findViewById(R.id.bloodGroupSpinner);
        areaSpinner = findViewById(R.id.areaSpinner);

        interestedCheckBox = findViewById(R.id.interestedCheckBox);

        birthDateTV = findViewById(R.id.birthDateTV);
        //lastDateOfDonateBloodTV = findViewById(R.id.lastDateOfDonateBloodTV);
        rdMale = findViewById(R.id.rdBtnMale);
        rdFemale = findViewById(R.id.rdBtnFemale);

        cancelBtn = findViewById(R.id.cancelBtn);
        signUpBtn = findViewById(R.id.signUpBtn);


        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        //for selected date of birth
        birthDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SignUpWithPhoneActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        bDaySelectedListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        bDaySelectedListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                birthDate = dayOfMonth+"/"+month+"/"+year;
                birthDateTV.setText(birthDate);
            }

        };

        //for selected last date of blood donation
        /*lastDateOfDonateBloodTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SignUpWithPhoneActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        lastBloodDonationDateSelectedListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        lastBloodDonationDateSelectedListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                lastDateOfBloodDonate = dayOfMonth+" / "+month+" / "+year;
                lastDateOfDonateBloodTV.setText(lastDateOfBloodDonate);
            }

        };*/

        //set on click listener on button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpWithPhoneActivity.this, SignInWithPhoneActivity.class);
                startActivity(intent);
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*progressDialog.setTitle("Creating new user");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();*/

                GetValueFromUser();
                if (fullName.isEmpty()){
                    fullNameET.setError("Cannot be empty");
                    fullNameET.requestFocus();
                }
                else if (nickName.isEmpty()){
                    nickNameET.setError("Cannot be empty");
                    nickNameET.requestFocus();
                }
                else if (mobileNo.isEmpty()){
                    mobileET.setError("Cannot be empty");
                    mobileET.requestFocus();
                }
                /*else if (height.isEmpty()){
                    heightET.setError("Cannot be empty");
                    heightET.requestFocus();
                }
                else if (weight.isEmpty()){
                    weightET.setError("Cannot be empty");
                    weightET.requestFocus();
                }*/
                /*else if (email.isEmpty()){
                    emailET.setError("Cannot be empty");
                    emailET.requestFocus();
                }
                else if (password.isEmpty()){
                    passwordET.setError("Cannot be empty");
                    passwordET.requestFocus();
                }*/
                else if (area.isEmpty() || area.equals("Select Area")){
                    Toast.makeText(SignUpWithPhoneActivity.this,"Select Your Area",Toast.LENGTH_SHORT).show();
                }
                else if (bloodGroup.isEmpty() || bloodGroup.equals("Blood Group")){
                    Toast.makeText(SignUpWithPhoneActivity.this,"Select Your Blood Group",Toast.LENGTH_SHORT).show();
                }
                /*else if (!interestedCheckBox.isChecked()){
                    Toast.makeText(SignUpWithPhoneActivity.this,"Interested to donate blood",Toast.LENGTH_SHORT).show();
                }*/
                else if (birthDate.isEmpty()){
                    Toast.makeText(SignUpWithPhoneActivity.this,"Enter your birth date",Toast.LENGTH_SHORT).show();
                }
                /*else if (lastDateOfBloodDonate.isEmpty()){
                    //Toast.makeText(SignUpWithPhoneActivity.this,"Enter last date of donate blood",Toast.LENGTH_SHORT).show();
                    lastDateOfBloodDonate="null";
                }*/
                if (!rdMale.isChecked() && !rdFemale.isChecked()){
                    Toast.makeText(SignUpWithPhoneActivity.this,"Select Your Gender",Toast.LENGTH_SHORT).show();
                }

                else {
                    registerUser();
                    Toast.makeText(SignUpWithPhoneActivity.this,"Register Btn Clicked",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //for blood group spinner
        ArrayList<String> arrayList1 = new ArrayList<>();
        arrayList1.add("Blood Group");
        arrayList1.add("A+");
        arrayList1.add("A-");
        arrayList1.add("B+");
        arrayList1.add("B-");
        arrayList1.add("AB+");
        arrayList1.add("AB-");
        arrayList1.add("O+");
        arrayList1.add("O-");

        ArrayAdapter deviceAdapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList1);
        bloodGoupSpinner.setAdapter(deviceAdapter1);

        bloodGoupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodGroup = parent.getItemAtPosition(position).toString();
                //Toast.makeText(SignUpWithPhoneActivity.this,bloodGroup,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //for area spinner
        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList2.add("Select Area");
        arrayList2.add("Adabor");
        arrayList2.add("Uttar Khan");
        arrayList2.add("Uttara");
        arrayList2.add("Kadamtali");
        arrayList2.add("Kalabagan");
        arrayList2.add("Kafrul");
        arrayList2.add("Kamrangirchar");
        arrayList2.add("Cantonment");
        arrayList2.add("Kotwali");
        arrayList2.add("Khilkhet");
        arrayList2.add("Khilgaon");
        arrayList2.add("Gulshan");
        arrayList2.add("Gendaria");
        arrayList2.add("Chawkbazar Model");
        arrayList2.add("Demra");
        arrayList2.add("Turag");
        arrayList2.add("Tejgaon");
        arrayList2.add("Tejgaon I/A");
        arrayList2.add("Dakshinkhan");
        arrayList2.add("Darus Salam");
        arrayList2.add("Dhanmondi");
        arrayList2.add("New Market");
        arrayList2.add("Paltan");
        arrayList2.add("Pallabi");
        arrayList2.add("Bangshal");
        arrayList2.add("Badda");
        arrayList2.add("Bimanbandar");
        arrayList2.add("Motijheel");
        arrayList2.add("Mirpur Model");
        arrayList2.add("Mohammadpur");
        arrayList2.add("Jatrabari");
        arrayList2.add("Ramna");
        arrayList2.add("Rampura");
        arrayList2.add("Lalbagh");
        arrayList2.add("Shah Ali");
        arrayList2.add("Shahbagh");
        arrayList2.add("Sher-e-Bangla Nagar");
        arrayList2.add("Shyampur");
        arrayList2.add("Sabujbagh");
        arrayList2.add("Sutrapur");
        arrayList2.add("Hazaribagh");
        ArrayAdapter deviceAdapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList2);
        areaSpinner.setAdapter(deviceAdapter2);

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = parent.getItemAtPosition(position).toString();
                //Toast.makeText(SignUpWithPhoneActivity.this,area,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null){
            //handle the current user
        }
    }

    public void GetValueFromUser(){
        fullName = fullNameET.getText().toString();
        nickName = nickNameET.getText().toString();
        mobileNo = "+88"+mobileET.getText().toString();
        email = emailET.getText().toString();
        /*height = heightET.getText().toString();
        weight = weightET.getText().toString();
        password = passwordET.getText().toString();
        address = addressET.getText().toString();*/

        if (email.isEmpty()){
            email = "empty";
        }
        if (rdMale.isChecked()){
            gender = "Male";
            dpLink = "https://firebasestorage.googleapis.com/v0/b/blood-donor-diu.appspot.com/o/userProfilePicture%2Fmale.png?alt=media&token=47c300e3-eeb5-4a8a-b21b-a7afdb057ba8";
        }
        if (rdFemale.isChecked()){
            gender = "Female";
            dpLink = "https://firebasestorage.googleapis.com/v0/b/blood-donor-diu.appspot.com/o/userProfilePicture%2Ffemale.png?alt=media&token=5b6280db-73f4-41ed-acb7-0add2ed0502a";
        }
        if (interestedCheckBox.isChecked()){
            interestToDonateBlood = "yes";
        }

        Calendar calendar = Calendar.getInstance();
        joinDate = DateFormat.getDateInstance().format(calendar.getTime());
    }

    public void registerUser(){

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

                                if (mo.equals(mobileNo)){
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpWithPhoneActivity.this, "User already registered",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpWithPhoneActivity.this, SignInWithPhoneActivity.class);
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
        Toast.makeText(SignUpWithPhoneActivity.this, "new Reg",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUpWithPhoneActivity.this,SignUpVerifyPhoneActivity.class);
        intent.putExtra("fullName", fullName);
        intent.putExtra("nickName", nickName);
        intent.putExtra("birthDate", birthDate);
        intent.putExtra("mobileNo", mobileNo);
        intent.putExtra("email", email);
        intent.putExtra("height", height);
        intent.putExtra("weight", weight);
        intent.putExtra("area", area);
        intent.putExtra("address", address);
        intent.putExtra("bloodGroup", bloodGroup);
        intent.putExtra("lastDateOfBloodDonate", lastDateOfBloodDonate);
        intent.putExtra("dpLink", dpLink);
        intent.putExtra("status", status);
        intent.putExtra("gender", gender);
        intent.putExtra("interestToDonateBlood", interestToDonateBlood);
        intent.putExtra("joinDate", joinDate);
        startActivity(intent);

    }
}
