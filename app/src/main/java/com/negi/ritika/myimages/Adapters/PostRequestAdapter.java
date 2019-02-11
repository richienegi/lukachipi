package com.negi.ritika.myimages.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.negi.ritika.myimages.ClickListener;
import com.negi.ritika.myimages.Model.User_Images;
import com.negi.ritika.myimages.R;

import java.util.List;

public class PostRequestAdapter extends RecyclerView.Adapter<PostRequestAdapter.MyViewHolder> {

    List<User_Images> data;
    Context c;
    ClickListener listener;

    public PostRequestAdapter(Context c, List<User_Images> data) {
        this.c=c;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.user_request_image, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User_Images ui = data.get(position);
        holder.setData(position, ui);
        Glide.with(c)
                .load(ui.getUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        int position;
        User_Images model;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.rImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(v, getAdapterPosition());
                    return true;
                }
            });
        }

        public void setData(int position , User_Images model)
        {
            this.position = position;
            this.model = model;
        }
    }

    public void setOnClick(ClickListener onClick) {
        this.listener = onClick;
    }

}
