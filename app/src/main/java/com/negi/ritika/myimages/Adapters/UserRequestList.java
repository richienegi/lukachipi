package com.negi.ritika.myimages.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.negi.ritika.myimages.ClickListener;
import com.negi.ritika.myimages.R;

import java.util.List;

public class UserRequestList extends RecyclerView.Adapter<UserRequestList.MyViewHolder> {

    Context c;
    List<String> username;
    List<String> number;
    ClickListener listener;

    public UserRequestList(Context c, List<String> username, List<String> number) {
        this.c = c;
        this.username = username;
        this.number = number;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.adapter_userlist, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.username.setText(username.get(position));
        holder.request.setText(number.get(position));
    }

    @Override
    public int getItemCount() {
        return username.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, request;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_name);
            request = (TextView) itemView.findViewById(R.id.no_request);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

    public void setOnClick(ClickListener onClick) {
        this.listener = onClick;
    }
}
