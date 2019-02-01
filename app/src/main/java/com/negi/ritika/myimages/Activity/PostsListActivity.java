package com.negi.ritika.myimages.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.negi.ritika.myimages.Adapters.PostListAdapter;
import com.negi.ritika.myimages.Model.All_Images;
import com.negi.ritika.myimages.R;

import java.util.ArrayList;
import java.util.List;

public class PostsListActivity extends AppCompatActivity implements PostListAdapter.ClickListener {

    private ProgressDialog pd;

    private RecyclerView mRecylerview;

    private DatabaseReference mFirebaseDatabase;

    private GridLayoutManager layoutManager;

    private String cate, owner;

    private List<All_Images> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        mRecylerview = findViewById(R.id.recyclerView);

        data = new ArrayList<>();

        cate = getIntent().getStringExtra("categ");
        owner = getIntent().getStringExtra("owner");

        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecylerview.setLayoutManager(layoutManager);

        if (owner.length()>0) {
            showUserPost(owner);
        }
        else {
            showAllImages();
        }
    }

    private void showAllImages() {

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("uploads").child(cate);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.show();

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();

                for (DataSnapshot ps : dataSnapshot.getChildren()) {
                    for (DataSnapshot ps2 : ps.getChildren()) {
                        All_Images image = ps2.getValue(All_Images.class);
                        data.add(image);
                    }
                }

                if (data.size() == 0) {
                    Toast.makeText(PostsListActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
                    finish();
                }

                PostListAdapter ad = new PostListAdapter(PostsListActivity.this, data);
                ad.setOnClick(PostsListActivity.this);
                mRecylerview.setAdapter(ad);

                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
            }
        });

    }

    private void showUserPost(String owner) {
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("uploads").child(cate).child(owner);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.show();

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();

                for (DataSnapshot ps2 : dataSnapshot.getChildren()) {
                    All_Images image = ps2.getValue(All_Images.class);
                    data.add(image);
                }

                if (data.size() == 0) {
                    Toast.makeText(PostsListActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
                    finish();
                }

                PostListAdapter ad = new PostListAdapter(PostsListActivity.this, data);
                ad.setOnClick(PostsListActivity.this);
                mRecylerview.setAdapter(ad);

                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        All_Images image = data.get(position);
        String img = image.getUrl();
        String id = image.getId();
        String cat = image.getCategory();
        String download = image.getDownloads();
        String uid = image.getUid();

        Intent i = new Intent(this, PostDetailActivity.class);
        i.putExtra("image", img);
        i.putExtra("uid", uid);
        i.putExtra("id", id);
        i.putExtra("cate", cat);
        i.putExtra("downloads", download);
        startActivity(i);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
