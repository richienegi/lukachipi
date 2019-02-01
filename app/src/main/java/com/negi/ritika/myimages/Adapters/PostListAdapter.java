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
import com.negi.ritika.myimages.Model.All_Images;
import com.negi.ritika.myimages.R;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    Context c;
    LayoutInflater li;
    List<All_Images> data;

    public ClickListener listener;

    public PostListAdapter(Context c, List<All_Images> data) {
        this.c=c;
        li = LayoutInflater.from(c);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = li.inflate(R.layout.row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        All_Images info = data.get(position);
        holder.setData(position, info);

        Glide.with(c)
                .load(info.getUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.mImage);

//        Picasso.with(ctx).load(image).placeholder(R.drawable.download).into(mImage);
        holder.downloads.setText(info.getDownloads());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public All_Images model;
        int position;
        ImageView mImage;
        TextView downloads;

        public ViewHolder(View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.rImageView);
            downloads = itemView.findViewById(R.id.noDown);

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

        public void setData(int position , All_Images model)
        {
            this.position = position;
            this.model = model;
        }
    }

    public void setOnClick(ClickListener onClick)
    {
        this.listener=onClick;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
