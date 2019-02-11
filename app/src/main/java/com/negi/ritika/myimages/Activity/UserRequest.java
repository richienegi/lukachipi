package com.negi.ritika.myimages.Activity;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.negi.ritika.myimages.Adapters.PostRequestAdapter;
import com.negi.ritika.myimages.ClickListener;
import com.negi.ritika.myimages.Constants;
import com.negi.ritika.myimages.Fragments.ShowImage;
import com.negi.ritika.myimages.Model.All_Images;
import com.negi.ritika.myimages.Model.User_Images;
import com.negi.ritika.myimages.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class UserRequest extends AppCompatActivity implements ClickListener {

    private RecyclerView rv;
    private String uid, category;

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
        category = b.getString("categ");

        rv = (RecyclerView)findViewById(R.id.rview);

        request = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_REQUEST).child(category).child(uid);

        layoutManager = new GridLayoutManager(this, 2);
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
        b.putString("url", data.get(position).getUrl());
        im.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, im).addToBackStack(null).commit();
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Approval");
        builder.setMessage("Do you want to approve this image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                approveImage(position);
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
        String url = ui.getUrl();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        long time = cal.getTimeInMillis();
        All_Images images = new All_Images(id,url,"0","0",String.valueOf(time),category, uid, ui.getName());
        upload = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS).child(category).child(uid);
        upload.child(id).setValue(images);
        request.child(id).removeValue();
    }

    private void deleteImage(int position) {
        final User_Images ui = data.get(position);
        StorageReference rf = FirebaseStorage.getInstance().getReferenceFromUrl(ui.getUrl());
        rf.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
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
