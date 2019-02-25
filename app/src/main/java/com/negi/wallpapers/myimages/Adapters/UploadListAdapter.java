package com.negi.wallpapers.myimages.Adapters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.negi.wallpapers.myimages.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {
    public List<String> filename;
    public List<String> fileDoneList;
    public List<Bitmap> images;

    public UploadListAdapter(List<String> filename, List<String> fileDoneList, List<Bitmap> images) {
        this.fileDoneList = fileDoneList;
        this.filename = filename;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fileName = filename.get(position);
        holder.title.setText(fileName);
        holder.upload.setImageBitmap(images.get(position));
        String filedone = fileDoneList.get(position);
        if (filedone.equals("uploading")) {
            holder.done.setImageResource(R.drawable.show);
        } else {
            holder.done.setImageResource(R.drawable.checked);
        }



    }

    @Override
    public int getItemCount() {
        return filename.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView title;
        CircleImageView upload;
        ImageView done;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            upload = (CircleImageView) itemView.findViewById(R.id.upload);
            title = (TextView) itemView.findViewById(R.id.title);
            done = (ImageView) itemView.findViewById(R.id.show);
        }
    }
}
