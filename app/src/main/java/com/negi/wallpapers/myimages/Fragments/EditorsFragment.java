package com.negi.wallpapers.myimages.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.negi.wallpapers.myimages.Activity.ShowPostActivity;
import com.negi.wallpapers.myimages.Adapters.PostListAdapter;
import com.negi.wallpapers.myimages.ClickListener;
import com.negi.wallpapers.myimages.Constants;
import com.negi.wallpapers.myimages.Model.All_Images;
import com.negi.wallpapers.myimages.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditorsFragment extends Fragment implements ClickListener {

    RecyclerView rv;
    ProgressBar pb;
    View v;

    DatabaseReference ref;

    List<All_Images> data;

    public EditorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_editors, container, false);
        rv = (RecyclerView)v.findViewById(R.id.rview);
        pb = (ProgressBar)v.findViewById(R.id.progress);
        pb.setVisibility(View.VISIBLE);
        ref = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS).child("Admin");
        data = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setLayoutManager(gridLayoutManager);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    All_Images im = ds.getValue(All_Images.class);
                    data.add(im);
                }
                if(pb.getVisibility()==View.VISIBLE)
                {
                    pb.setVisibility(View.GONE);
                }
                if(data.size()==0)
                {
                    LinearLayout li = v.findViewById(R.id.error);
                    li.setVisibility(View.VISIBLE);
                }
                else {
                    LinearLayout li = v.findViewById(R.id.error);
                    if(li.getVisibility()==View.VISIBLE)
                    {
                        li.setVisibility(View.GONE);
                    }
                }
                Collections.reverse(data);
                PostListAdapter ad = new PostListAdapter(getContext(), data);
                ad.setOnClick(EditorsFragment.this);
                rv.setAdapter(ad);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                if(pb.getVisibility()==View.VISIBLE)
                {
                    pb.setVisibility(View.GONE);
                }
            }
        });
        return v;
    }

    @Override
    public void onItemClick(View view, int position) {
        All_Images images = data.get(position);

        Intent i = new Intent(getContext(), ShowPostActivity.class);

        i.putExtra("id", images.getId());
        i.putExtra("uid", images.getUid());
        i.putExtra("downloads", images.getDownloads());
        i.putExtra("image", images.getImageUrl());
        i.putExtra("date", images.getDate());
        i.putExtra("owner", images.getOwner());
        i.putExtra("thumb", images.getThumb());

        startActivity(i);
    }

    @Override
    public void onItemLongClick(View view, final int position) {
//delete dialog to be shown here
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Image");
        builder.setMessage("Do you want to Delete this image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImage(position);
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = builder.create();
        ad.show();
    }

    private void deleteImage(int position) {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Deleting...");
        pd.setCancelable(false);
        pd.show();
        final All_Images ui = data.get(position);
        final StorageReference rf2 = FirebaseStorage.getInstance().getReferenceFromUrl(ui.getThumb());
        StorageReference rf = FirebaseStorage.getInstance().getReferenceFromUrl(ui.getImageUrl());
        rf.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    rf2.delete();
                    ref.child(ui.getId()).removeValue();
                } else {
                    Toast.makeText(getContext(), "Deletion Failed", Toast.LENGTH_SHORT).show();
                }
                pd.dismiss();
            }
        });
    }
}
