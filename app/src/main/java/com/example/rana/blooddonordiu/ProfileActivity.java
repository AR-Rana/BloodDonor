package com.example.rana.blooddonordiu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImageEV;
    private TextView profileNameTV;
    private TextView profileNickNameTV;
    private TextView profileBloodTV;
    private TextView profileStatusTV;
    private TextView profileEmailTV;
    private TextView profileMobileNoTV;
    private TextView profileAreaTV;
    //private TextView profileAddressTV;
    private TextView profileBirthDateTV;
    private TextView profileLastDateOfBloodDonationTV;
    private TextView profileInterestedToDonateTV;
    private TextView profileGenderTV;
    private TextView profileHeightTV;
    private TextView profileWeightTV;

    SwipeRefreshLayout swipeRefreshLayout;

    private String uid;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseRef;

    UserInfo userInfo = new UserInfo();

    //for update
    private String area, bloodGroup, birthDate, lastDateOfDonateBlood, interestToDonateBlood, gender;
    DatePickerDialog.OnDateSetListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ((AppCompatActivity) this).getSupportActionBar().hide();

        profileImageEV = findViewById(R.id.profileImageView);
        profileNameTV = findViewById(R.id.profileNameTV);
        profileNickNameTV = findViewById(R.id.profileNickNameTV);
        profileBloodTV = findViewById(R.id.profileBlood);
        profileStatusTV = findViewById(R.id.profileStatus);
        profileEmailTV = findViewById(R.id.profileEmailTV);
        profileMobileNoTV = findViewById(R.id.profileMobileNo);
        profileAreaTV = findViewById(R.id.profileArea);
        //profileAddressTV = findViewById(R.id.profileAddress);
        profileBirthDateTV = findViewById(R.id.profileBirthdate);
        profileLastDateOfBloodDonationTV = findViewById(R.id.profileLastDateOfDonateBlood);
        profileInterestedToDonateTV = findViewById(R.id.profileInterestedToDonateBlood);
        profileGenderTV = findViewById(R.id.profileYourGender);
        profileHeightTV = findViewById(R.id.profileYourHeight);
        profileWeightTV = findViewById(R.id.profileYourWeight);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference("userProfilePicture");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userProfilePicture");

        //to get firebase current user id
        uid = mAuth.getCurrentUser().getUid();

        myRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
                showData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //sweep refresh
        swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.indigo, R.color.lime);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        //update info dialog
        final AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        LayoutInflater inflater = (LayoutInflater) ProfileActivity.this.getSystemService(ProfileActivity.this.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate( R.layout.profile_info_update_dialog, null );

        final TextView optionTv = mView.findViewById(R.id.udOptionTV);
        final EditText textEt = mView.findViewById(R.id.udTextET);
        final Spinner spinner = mView.findViewById(R.id.udSpinner);
        final CheckBox checkBox = mView.findViewById(R.id.udInterestedCheckBox);
        final RadioGroup rdGroup = mView.findViewById(R.id.udGender);
        final RadioButton maleRd = mView.findViewById(R.id.udRdBtnMale);
        final RadioButton femaleRd = mView.findViewById(R.id.udRdBtnFemale);
        final Button updateInfoBtn = mView.findViewById(R.id.udUpdateBtn);
        final Button cancelInfoBtn = mView.findViewById(R.id.udCancelBtn);

        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        //for update profile
        profileImageEV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,UpdateProfileActivity.class);
                intent.putExtra("email", userInfo.getMobileNo());
                startActivity(intent);
                return true;
            }
        });
        profileNameTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                textEt.setText("");

                optionTv.setText("Enter Full Name");
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("fullName").setValue(textEt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileNickNameTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                textEt.setText("");

                optionTv.setText("Enter Your Nick Name");
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("nickName").setValue(textEt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileBloodTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                optionTv.setText("Select Your Blood Group");

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

                ArrayAdapter deviceAdapter1 = new ArrayAdapter(ProfileActivity.this, android.R.layout.simple_list_item_1,arrayList1);
                spinner.setAdapter(deviceAdapter1);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        bloodGroup = parent.getItemAtPosition(position).toString();
                        //Toast.makeText(SignUpWithPhoneActivity.this,bloodGroup,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("bloodGroup").setValue(bloodGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileStatusTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                textEt.setText("");

                optionTv.setText("Enter Your Status");
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("status").setValue(textEt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileMobileNoTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                textEt.setText("");
                textEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                textEt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});

                optionTv.setText("Enter Your Mobile Number");
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("mobileNo").setValue(textEt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileEmailTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                textEt.setText("");

                textEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                optionTv.setText("Enter Your Email Address");
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("email").setValue(textEt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileAreaTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                optionTv.setText("Select Your Area");

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
                ArrayAdapter deviceAdapter2 = new ArrayAdapter(ProfileActivity.this, android.R.layout.simple_list_item_1,arrayList2);
                spinner.setAdapter(deviceAdapter2);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        area = parent.getItemAtPosition(position).toString();
                        //Toast.makeText(SignUpWithPhoneActivity.this,area,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("mobileNo").setValue(area).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileBirthDateTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                optionTv.setText("Pick Your Birthdate");
                optionTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(ProfileActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                listener,
                                year,month,day);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });
                listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        birthDate = dayOfMonth+"/"+month+"/"+year;
                        optionTv.setText(birthDate);
                    }

                };
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("birthDate").setValue(birthDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileLastDateOfBloodDonationTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                optionTv.setText("Pick Last Date Of Your Blood Donation");
                optionTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(ProfileActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                listener,
                                year,month,day);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });
                listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        lastDateOfDonateBlood = dayOfMonth+"/"+month+"/"+year;
                        optionTv.setText(birthDate);
                    }

                };
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("lastDateOfDonateBlood").setValue(lastDateOfDonateBlood).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileInterestedToDonateTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.VISIBLE);
                rdGroup.setVisibility(View.GONE);

                optionTv.setText("Interest to donate blood or not");
                if (checkBox.isChecked()){
                    interestToDonateBlood = "yes";
                }
                else {
                    interestToDonateBlood = "no";
                }
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("interestToDonateBlood").setValue(interestToDonateBlood).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileGenderTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.VISIBLE);

                optionTv.setText("Interest to donate blood or not");

                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (maleRd.isChecked() || femaleRd.isChecked()){
                            if (maleRd.isChecked()){
                                gender = "Male";
                            }
                            if (femaleRd.isChecked()){
                                gender = "Female";
                            }
                            myRef.child("Users").child(uid).child("gender").setValue(gender).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            });
                        }
                        else {
                            Toast.makeText(ProfileActivity.this,"Select Your Gender Please",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                return true;
            }
        });
        profileHeightTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                textEt.setText("");

                optionTv.setText("Enter Your Height");
                textEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("height").setValue(textEt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        profileWeightTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                optionTv.setVisibility(View.VISIBLE);
                textEt.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                rdGroup.setVisibility(View.GONE);

                textEt.setText("");

                optionTv.setText("Enter Your Weight");
                textEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                updateInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("Users").child(uid).child("weight").setValue(textEt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Updated",Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                cancelInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
    }

    public  void refreshData(){
        showData();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void showData(){
        profileNameTV.setText(userInfo.getFullName());
        profileNickNameTV.setText(" ("+userInfo.getNickName()+")");
        profileBloodTV.setText("("+userInfo.getBloodGroup()+")");
        profileStatusTV.setText(userInfo.getStatus());
        profileMobileNoTV.setText(userInfo.getMobileNo());
        profileAreaTV.setText(userInfo.getArea());
        //profileAddressTV.setText(userInfo.getAddress());
        profileBirthDateTV.setText(userInfo.getBirthDate());
        profileLastDateOfBloodDonationTV.setText(userInfo.getLastDateOfDonateBlood());

        if (userInfo.getEmail().equals("empty")){
            profileEmailTV.setText("Not Provided");
        }
        else{
            profileEmailTV.setText(userInfo.getEmail());
        }

        if (userInfo.getInterestToDonateBlood().equals("no")){
            profileInterestedToDonateTV.setText("You are not interested to donate blood");
        }
        else{
            profileInterestedToDonateTV.setText("You are interested to donate blood");
        }

        profileGenderTV.setText("Gender : "+userInfo.getGender());

        if (userInfo.getHeight().isEmpty() || userInfo.getHeight().equals("empty")){
            profileHeightTV.setText("Height : Not Provided");
        }
        else{
            profileHeightTV.setText("Height : "+userInfo.getHeight());
        }

        if (userInfo.getWeight().isEmpty() || userInfo.getWeight().equals("empty")){
            profileWeightTV.setText("Weight : Not Provided");
        }
        else{
            profileWeightTV.setText("Weight : "+userInfo.getWeight());
        }

        Picasso.get().load(userInfo.getDpLink()).fit().centerCrop().into(profileImageEV);
    }

    public void getData(DataSnapshot ds) {

        userInfo.setFullName(ds.child(uid).getValue(UserInfo.class).getFullName());
        userInfo.setNickName(ds.child(uid).getValue(UserInfo.class).getNickName());
        userInfo.setBirthDate(ds.child(uid).getValue(UserInfo.class).getBirthDate());
        userInfo.setMobileNo(ds.child(uid).getValue(UserInfo.class).getMobileNo());
        userInfo.setHeight(ds.child(uid).getValue(UserInfo.class).getHeight());
        userInfo.setWeight(ds.child(uid).getValue(UserInfo.class).getWeight());
        userInfo.setEmail(ds.child(uid).getValue(UserInfo.class).getEmail());
        userInfo.setArea(ds.child(uid).getValue(UserInfo.class).getArea());
        userInfo.setAddress(ds.child(uid).getValue(UserInfo.class).getAddress());
        userInfo.setBloodGroup(ds.child(uid).getValue(UserInfo.class).getBloodGroup());
        userInfo.setLastDateOfDonateBlood(ds.child(uid).getValue(UserInfo.class).getLastDateOfDonateBlood());
        userInfo.setDpLink(ds.child(uid).getValue(UserInfo.class).getDpLink());
        userInfo.setStatus(ds.child(uid).getValue(UserInfo.class).getStatus());
        userInfo.setGender(ds.child(uid).getValue(UserInfo.class).getGender());
        userInfo.setInterestToDonateBlood(ds.child(uid).getValue(UserInfo.class).getInterestToDonateBlood());


    }

    public void UpdateProfile(View view) {

    }
}
