package com.example.rana.blooddonordiu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<MyPostRecyclerViewAdapter.ViewHolder>{
    private ArrayList<String> MyName = new ArrayList<>();
    private ArrayList<String> MyPost = new ArrayList<>();
    private ArrayList<String> ImageLink = new ArrayList<>();
    private ArrayList<String> PostDate = new ArrayList<>();
    private ArrayList<String> PostTime = new ArrayList<>();
    private Context mContext;

    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;


    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    public MyPostRecyclerViewAdapter(Context context, ArrayList<String> name,
                                     ArrayList<String> post, ArrayList<String> imageLink,
                                     ArrayList<String> postDate, ArrayList<String> postTime ) {
        MyName = name;
        MyPost = post;
        ImageLink = imageLink;
        PostDate = postDate;
        PostTime = postTime;
        mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_post_view_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Log.d(TAG, "onBindViewHolder: called.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        holder.nameTV.setText(MyName.get(position));
        holder.postTV.setText(MyPost.get(position));

        if (ImageLink.get(position).equals("empty") || ImageLink.equals("")){
            holder.imageLinkIV.setVisibility(View.GONE);
        }else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(ImageLink.get(position))
                    .into(holder.imageLinkIV);
        }


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: clicked on: " + MyName.get(position));

                Toast.makeText(mContext, MyName.get(position), Toast.LENGTH_SHORT).show();

                /*Intent intent = new Intent(mContext, ConversationActivity.class);
                intent.putExtra("image_url", MyDpLink.get(position));
                intent.putExtra("image_name", MyName.get(position));
                mContext.startActivity(intent);*/
            }
        });
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "This button is disable for now", Toast.LENGTH_SHORT).show();
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("allPost").child(PostTime.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (ImageLink.get(position).equals("empty")){
                            Toast.makeText(mContext, "This post delete successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            storageReference = storage.getReferenceFromUrl(ImageLink.get(position));
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    Log.d("photo delete ", "onSuccess: deleted file");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.d("photo delete ", "onFailure: did not delete file");
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return MyName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameTV;
        TextView postTV;
        ImageView imageLinkIV;
        RelativeLayout parentLayout;
        ImageButton editBtn;
        ImageButton deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.myPostUserName);
            postTV = itemView.findViewById(R.id.myPostUserPost);
            imageLinkIV = itemView.findViewById(R.id.myPostUserImage);
            editBtn = itemView.findViewById(R.id.myPostEditButton);
            deleteBtn = itemView.findViewById(R.id.myPostDeleteButton);

            parentLayout = itemView.findViewById(R.id.myPostViewParentLayout);
        }
    }
}
