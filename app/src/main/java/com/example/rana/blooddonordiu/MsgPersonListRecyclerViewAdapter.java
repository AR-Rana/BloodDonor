package com.example.rana.blooddonordiu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 1/1/2018.
 */

public class MsgPersonListRecyclerViewAdapter extends RecyclerView.Adapter<MsgPersonListRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> msgPersonName = new ArrayList<>();
    private ArrayList<String> msgPersonImage = new ArrayList<>();
    private ArrayList<String> msgPersonUid = new ArrayList<>();
    private Context mContext;

    public MsgPersonListRecyclerViewAdapter(Context context, ArrayList<String> personName, ArrayList<String> personImage, ArrayList<String> personUid ) {
        msgPersonName = personName;
        msgPersonImage = personImage;
        msgPersonUid = personUid;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_person_list_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(msgPersonImage.get(position))
                .into(holder.msgPersonImage);

        holder.msgPersonName.setText(msgPersonName.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));

                Toast.makeText(mContext, msgPersonName.get(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, ConversationActivity.class);
                intent.putExtra("imageLink", msgPersonImage.get(position));
                intent.putExtra("personName", msgPersonName.get(position));
                intent.putExtra("personUid", msgPersonUid.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return msgPersonName.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView msgPersonImage;
        TextView msgPersonName;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            msgPersonImage = itemView.findViewById(R.id.msgPersonImageIV);
            msgPersonName = itemView.findViewById(R.id.msgPersonNameTV);
            parentLayout = itemView.findViewById(R.id.msg_person_parent_layout);
        }
    }
}
