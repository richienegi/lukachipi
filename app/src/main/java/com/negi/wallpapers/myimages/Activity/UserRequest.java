package com.negi.wallpapers.myimages.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
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
import com.negi.wallpapers.myimages.Adapters.PostRequestAdapter;
import com.negi.wallpapers.myimages.ClickListener;
import com.negi.wallpapers.myimages.Constants;
import com.negi.wallpapers.myimages.Fragments.ShowImage;
import com.negi.wallpapers.myimages.Model.All_Images;
import com.negi.wallpapers.myimages.Model.User_Images;
import com.negi.wallpapers.myimages.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class UserRequest extends AppCompatActivity implements ClickListener {

    private RecyclerView rv;
    private String uid;

    private DatabaseReference request, upload;
    private GridLayoutManager layoutManager;

    List<User_Images> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request);

        data = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        uid = b.getString("id");

        rv = (RecyclerView)findViewById(R.id.user_r_view);

        request = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_REQUEST).child(uid);

        layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setLayoutManager(layoutManager);

        request.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    User_Images ui = ds.getValue(User_Images.class);
                    data.add(ui);
                }

                if(data.size()==0)
                {
                    LinearLayout li = findViewById(R.id.error);
                    li.setVisibility(View.VISIBLE);
                }
                else {
                    LinearLayout li = findViewById(R.id.error);
                    if(li.getVisibility()==View.VISIBLE)
                    {
                        li.setVisibility(View.GONE);
                    }
                }
                Collections.reverse(data);
                PostRequestAdapter ad = new PostRequestAdapter(UserRequest.this, data);
                ad.setOnClick(UserRequest.this);
                rv.setAdapter(ad);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        ShowImage im = new ShowImage();
        Bundle b = new Bundle();
        b.putString("url", data.get(position).getThumb());
        im.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(R.id.userContainer, im).addToBackStack(null).commit();
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Approval");
        builder.setMessage("Do you want to approve this image?");
        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                approveImage(position);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImage(position);
                dialog.dismiss();
            }
        });

        AlertDialog ad = builder.create();
        ad.show();
    }

    private void approveImage(int position) {
        User_Images ui = data.get(position);
        String id = ui.getId();
        String url = ui.getImageUrl();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        long time = cal.getTimeInMillis();
        All_Images images = new All_Images(id,uid, ui.getName(),"0",String.valueOf(time), url, ui.getThumb());
        upload = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS).child(uid);
        upload.child(id).setValue(images);
        request.child(id).removeValue();
    }

    private void deleteImage(int position) {
        final User_Images ui = data.get(position);
        final StorageReference rf2 = FirebaseStorage.getInstance().getReferenceFromUrl(ui.getThumb());
        StorageReference rf = FirebaseStorage.getInstance().getReferenceFromUrl(ui.getImageUrl());
        rf.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    rf2.delete();
                    request.child(ui.getId()).removeValue();
                }
                else
                {
                    Toast.makeText(UserRequest.this, "Deletion Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}