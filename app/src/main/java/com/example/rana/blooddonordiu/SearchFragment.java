package com.example.rana.blooddonordiu;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private LinearLayout layoutNoUser;
    private ImageButton searchBtn;
    private Spinner areaSpinner, bloodGroupSpinner;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    private String currentUid;
    private String selectedArea, selectedBloodGroup;

    ArrayList<String> fullName = new ArrayList<>();
    ArrayList<String> email = new ArrayList<>();
    ArrayList<String> phone = new ArrayList<>();
    ArrayList<String> area = new ArrayList<>();
    ArrayList<String> dpLink = new ArrayList<>();
    ArrayList<String> uid = new ArrayList<>();
    ArrayList<String> status = new ArrayList<>();
    ArrayList<String> bloodGroup = new ArrayList<>();


    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_search, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        initialization();




        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReferenceFromUrl("https://blood-donor-diu.firebaseio.com/");

        //to get firebase current user id
        currentUid = mAuth.getCurrentUser().getUid();

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

        ArrayAdapter deviceAdapter1 = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,arrayList1);
        bloodGroupSpinner.setAdapter(deviceAdapter1);

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBloodGroup = parent.getItemAtPosition(position).toString();
                //Toast.makeText(SignUpWithPhoneActivity.this,bloodGroup,Toast.LENGTH_SHORT).show();
                if(!selectedBloodGroup.equals("Blood Group") && !selectedArea.equals("Select Area")){

                    search();
                }

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
        ArrayAdapter deviceAdapter2 = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,arrayList2);
        areaSpinner.setAdapter(deviceAdapter2);

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedArea = parent.getItemAtPosition(position).toString();
                //Toast.makeText(SignUpWithPhoneActivity.this,area,Toast.LENGTH_SHORT).show();
                if(!selectedArea.equals("Select Area")){

                    search();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Search button: ","Clicked");

                if (selectedArea.equals("Select Area") && selectedBloodGroup.equals("Blood Group")){
                    Toast.makeText(getActivity(),"Select Area ana Blood group",Toast.LENGTH_SHORT).show();
                }
                else {
                    showSearchPeople();
                }
            }
        });

        return v;
    }

    public void search(){
        fullName.clear();
        email.clear();
        phone.clear();
        area.clear();
        dpLink.clear();
        uid.clear();
        status.clear();
        bloodGroup.clear();


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.child("Users").getChildren()) {
                    //Log.e(snap.getKey(),snap.getChildrenCount() + "");
                    final String id = snap.getKey();
                    if (!id.equals(currentUid)){
                        setArray(id);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setArray(final String id){

        myRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                //Toast.makeText(UpdateDeviceInfo.this,snapshot.child(selectedDevice).child("devLocation").getValue().toString(),Toast.LENGTH_SHORT ).show();
                //uid[position] = id;

                try{
                    String a= snapshot.child(id).child("area").getValue().toString(),
                            b=snapshot.child(id).child("bloodGroup").getValue().toString();
                    /*Log.e("Select Area "+position+" :",a);
                    Log.e("Select Blood "+position+" :",b);*/
                    //a.equals(selectedArea) && b.equals(selectedBloodGroup)
                    if (a.equals(selectedArea) && b.equals(selectedBloodGroup)){
                        uid.add(id);
                        fullName.add(snapshot.child(id).child("fullName").getValue().toString());
                        email.add(snapshot.child(id).child("email").getValue().toString());
                        phone.add(snapshot.child(id).child("mobileNo").getValue().toString());
                        area.add(snapshot.child(id).child("area").getValue().toString());
                        dpLink.add(snapshot.child(id).child("dpLink").getValue().toString());
                        status.add(snapshot.child(id).child("status").getValue().toString());
                        bloodGroup.add(snapshot.child(id).child("bloodGroup").getValue().toString());

                    }else {
                        Log.e("Name :","No one");
                    }

                }catch (Exception e){
                    Log.e("person found error ",e.getMessage());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void showSearchPeople(){
        layoutNoUser.setVisibility(View.GONE);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        //progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Searching...");
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

                RecyclerView recyclerView = v.findViewById(R.id.searchPersonRecyclerView);
                SearchPersonListRecyclerViewAdapter adapter = new SearchPersonListRecyclerViewAdapter(getActivity(), uid, fullName, dpLink);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        }.start();

        if (uid.size() == 0){
            layoutNoUser.setVisibility(View.VISIBLE);
        }


    }

    private void initialization(){
        areaSpinner = v.findViewById(R.id.searchAreaSpinner);
        bloodGroupSpinner = v.findViewById(R.id.searchBloodSpinner);

        layoutNoUser = v.findViewById(R.id.searchNoUser);
        layoutNoUser.setVisibility(View.GONE);

        searchBtn = v.findViewById(R.id.searchBtn);

    }

}

