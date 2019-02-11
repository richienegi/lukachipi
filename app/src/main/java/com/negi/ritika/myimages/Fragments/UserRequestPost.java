package com.negi.ritika.myimages.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.negi.ritika.myimages.Activity.UserRequest;
import com.negi.ritika.myimages.Adapters.UserRequestList;
import com.negi.ritika.myimages.ClickListener;
import com.negi.ritika.myimages.Constants;
import com.negi.ritika.myimages.Model.User_Images;
import com.negi.ritika.myimages.R;

import java.util.ArrayList;
import java.util.List;


public class UserRequestPost extends Fragment implements ClickListener {

    RecyclerView list;
    DatabaseReference ref;
    List<String> userNameList;
    List<String> no_post;
    List<String> id;
    String category;

    public UserRequestPost() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_request_post, container, false);
        list = (RecyclerView)v.findViewById(R.id.userlist);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(lm);
        list.setHasFixedSize(true);

        category = getArguments().getString("categ");

        userNameList = new ArrayList<>();
        no_post = new ArrayList<>();
        id = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_REQUEST).child(category);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userNameList.clear();
                no_post.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    for(DataSnapshot ds2: ds.getChildren())
                    {
                        User_Images image = ds2.getValue(User_Images.class);
                        userNameList.add(image.getName());
                        break;
                    }
                    no_post.add(String.valueOf(ds.getChildrenCount()));
                    id.add(ds.getKey());
                }

                UserRequestList ad_list = new UserRequestList(getContext(), userNameList, no_post);
                ad_list.setOnClick(UserRequestPost.this);
                list.setAdapter(ad_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(getContext(), UserRequest.class);
        i.putExtra("id", id.get(position));
        i.putExtra("categ", category);
        startActivity(i);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        return;
    }
}
