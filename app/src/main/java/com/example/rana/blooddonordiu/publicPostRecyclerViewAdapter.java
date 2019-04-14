package com.example.rana.blooddonordiu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class publicPostRecyclerViewAdapter extends RecyclerView.Adapter<publicPostRecyclerViewAdapter.ViewHolder>{
    private ArrayList<String> MyName = new ArrayList<>();
    private ArrayList<String> MyPost = new ArrayList<>();
    private ArrayList<String> ImageLink = new ArrayList<>();
    private Context mContext;

    public publicPostRecyclerViewAdapter(Context context, ArrayList<String> name, ArrayList<String> post, ArrayList<String> imageLink ) {
        MyName = name;
        MyPost = post;
        ImageLink = imageLink;
        mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_post_view_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Log.d(TAG, "onBindViewHolder: called.");

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

        public ViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.publicPostUserName);
            postTV = itemView.findViewById(R.id.publicPostUserPost);
            imageLinkIV = itemView.findViewById(R.id.publicPostUserImage);

            parentLayout = itemView.findViewById(R.id.publicPostViewParentLayout);
        }
    }
}
