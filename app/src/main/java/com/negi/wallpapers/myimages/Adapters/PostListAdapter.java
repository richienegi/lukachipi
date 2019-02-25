package com.negi.wallpapers.myimages.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.negi.wallpapers.myimages.ClickListener;
import com.negi.wallpapers.myimages.GlideImageLoader;
import com.negi.wallpapers.myimages.Model.All_Images;
import com.negi.wallpapers.myimages.R;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        All_Images info = data.get(position);
        holder.setData(position, info);
        holder.pb.setVisibility(View.VISIBLE);
        holder.pb.setProgress(0);

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .priority(Priority.HIGH);

        new GlideImageLoader(holder.mImage, holder.pb).load(info.getThumb(),options);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public All_Images model;
        int position;
        ImageView mImage;
        ProgressBar pb;

        public ViewHolder(View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.rImageView);
            pb = itemView.findViewById(R.id.progress);
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


    public void setOnClick(ClickListener onClick) {
        this.listener = onClick;
    }

}
