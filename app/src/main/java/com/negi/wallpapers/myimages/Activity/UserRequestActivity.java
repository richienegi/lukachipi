package com.negi.wallpapers.myimages.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.negi.wallpapers.myimages.Adapters.UserRequestList;
import com.negi.wallpapers.myimages.ClickListener;
import com.negi.wallpapers.myimages.Constants;
import com.negi.wallpapers.myimages.Model.User_Images;
import com.negi.wallpapers.myimages.R;

import java.util.ArrayList;
import java.util.List;

public class UserRequestActivity extends AppCompatActivity implements ClickListener {

    RecyclerView rv;
    LinearLayoutManager lm;
    DatabaseReference ref;
    List<String> userNameList, no_post, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request);

        userNameList = new ArrayList<>();
        no_post = new ArrayList<>();
        id = new ArrayList<>();
        rv = (RecyclerView)findViewById(R.id.user_r_view);
        lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setLayoutManager(lm);

        ref = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_REQUEST);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userNameList.clear();
                no_post.clear();
                id.clear();

                for(DataSnapshot ds1 : dataSnapshot.getChildren())
                {
                    for(DataSnapshot ds2 : ds1.getChildren())
                    {
                        User_Images image = ds2.getValue(User_Images.class);
                        userNameList.add(image.getName());
                        break;
                    }
                    no_post.add(String.valueOf(ds1.getChildrenCount()));
                    id.add(ds1.getKey());
                }

                if(id.size()==0)
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
                UserRequestList ad_list = new UserRequestList(UserRequestActivity.this, userNameList, no_post);
                ad_list.setOnClick(UserRequestActivity.this);
                rv.setAdapter(ad_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(UserRequestActivity.this, UserRequest.class);
        i.putExtra("id", id.get(position));
        startActivity(i);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
