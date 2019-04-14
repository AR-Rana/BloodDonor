package com.example.rana.blooddonordiu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 1/1/2018.
 */

public class SearchPersonListRecyclerViewAdapter extends RecyclerView.Adapter<SearchPersonListRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> sPersonUid = new ArrayList<>();
    private ArrayList<String> sPersonName = new ArrayList<>();
    private ArrayList<String> sPersonDpLink = new ArrayList<>();
    private Context mContext;

    public SearchPersonListRecyclerViewAdapter(Context context, ArrayList<String> personUid,ArrayList<String> personName, ArrayList<String> personDpLink ) {
        sPersonUid = personUid;
        sPersonName = personName;
        sPersonDpLink = personDpLink;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_person_list_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(sPersonDpLink.get(position))
                .into(holder.personDpIv);

        holder.personNameTv.setText(sPersonName.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));

                Toast.makeText(mContext, sPersonName.get(position), Toast.LENGTH_SHORT).show();

                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                View mView = inflater.inflate( R.layout.search_person_option_dialog, null );

                Button sendMsgBtn = mView.findViewById(R.id.spdSendMsg);
                Button sendMsgPhoneBtn = mView.findViewById(R.id.spdSendMsgPhone);
                Button cancelBtn = mView.findViewById(R.id.spdCancel);

                alert.setView(mView);

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                sendMsgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("uid",sPersonUid.get(position));
                        Log.d("name",sPersonName.get(position));
                        Log.d("dp",sPersonDpLink.get(position));


                        Intent intent = new Intent(mContext, ConversationActivity.class);

                        intent.putExtra("personUid", sPersonUid.get(position));
                        intent.putExtra("personName", sPersonName.get(position));
                        intent.putExtra("imageLink", sPersonDpLink.get(position));

                        mContext.startActivity(intent);
                    }
                });
                sendMsgPhoneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,"Currently this service is not available",Toast.LENGTH_SHORT).show();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return sPersonName.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView personDpIv;
        TextView personNameTv;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            personDpIv = itemView.findViewById(R.id.searchPersonDpIv);
            personNameTv = itemView.findViewById(R.id.searchPersonNameTv);
            parentLayout = itemView.findViewById(R.id.search_person_parent_layout);
        }
    }
}
