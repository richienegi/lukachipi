package com.negi.ritika.myimages.Adapters;

/**
 * Created by Ravi Sharma on 21-Dec-17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.negi.ritika.myimages.Model.nature_model;
import com.negi.ritika.myimages.R;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by payal on 9/21/2017.
 */

public class my_adapter extends RecyclerView.Adapter<my_adapter.MyViewHolder> {
    RecylerListener recylerListener;
    private List<nature_model> objList;
    private LayoutInflater inflater;

    public my_adapter(Context context, List<nature_model> obj) {
        inflater = LayoutInflater.from(context);
        objList = obj;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.abcd, parent, false);//xml file inflate, viewGroup parent,for boolean
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        nature_model current = objList.get(position);
        holder.setData(current, position);
        holder.relate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recylerListener.Onlick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objList.size();
    }

    public interface RecylerListener {
        void Onlick(int poistion);
    }

    public void setOnClick(RecylerListener listener) {
        recylerListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView t;
        private CircleImageView im;
        int position;
        nature_model currentobj;
        RelativeLayout relate;

        public MyViewHolder(View itemView) {
            super(itemView);
            t = (TextView) itemView.findViewById(R.id.txt);
            im = (CircleImageView) itemView.findViewById(R.id.img);
            relate = (RelativeLayout) itemView.findViewById(R.id.relat);

        }

        public void setData(nature_model current, int position) {
            t.setText(current.getTitle());
            im.setImageResource(current.getImageid());
            this.position = position;
            currentobj = current;

        }
    }
}