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


public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder>{
    private ArrayList<String> senderId = new ArrayList<>();
    private ArrayList<String> receiverId = new ArrayList<>();
    private ArrayList<String> message = new ArrayList<>();
    private Context mContext;

    //Firebase
    private FirebaseAuth mAuth;

    public ConversationRecyclerViewAdapter(Context context, ArrayList<String> sender,
                                           ArrayList<String> receiver, ArrayList<String> msg) {
        senderId = sender;
        receiverId = receiver;
        message = msg;
        mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_view_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Log.d(TAG, "onBindViewHolder: called.");

        mAuth = FirebaseAuth.getInstance();

        //to get firebase current user id
        String myUid = mAuth.getCurrentUser().getUid();

        if (senderId.get(position).equals(myUid)){
            holder.sendMessageTV.setText(message.get(position));
            holder.recMessageTV.setVisibility(View.GONE);
        }
        else {
            holder.recMessageTV.setText(message.get(position));
            holder.sendMessageTV.setVisibility(View.GONE);
        }


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: clicked on: " + MyName.get(position));
                Toast.makeText(mContext, message.get(position), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return senderId.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView sendMessageTV;
        TextView recMessageTV;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            sendMessageTV = itemView.findViewById(R.id.conversationSendMsgTv);
            recMessageTV = itemView.findViewById(R.id.conversationRecMsgTv);

            parentLayout = itemView.findViewById(R.id.conversation_parent_layout);
        }
    }
}
