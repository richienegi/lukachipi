package com.negi.ritika.myimages.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.negi.ritika.myimages.Adapters.my_adapter;
import com.negi.ritika.myimages.Model.nature_model;
import com.negi.ritika.myimages.R;

public class Admin_Uploads extends AppCompatActivity implements my_adapter.RecylerListener{

    String category="";
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__uploads);

        rv = (RecyclerView)findViewById(R.id.rview);
        my_adapter myadpt = new my_adapter(this, nature_model.getObjectList());
        myadpt.setOnClick(this);
        rv.setAdapter(myadpt);
        LinearLayoutManager lm = new LinearLayoutManager(this);//to show card view, recycler View
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(lm);
        rv.setItemAnimator(new DefaultItemAnimator());//
    }

    @Override
    public void Onlick(int poistion) {
        switch (poistion)
        {

            case 0:
                category="Wildlife";
                break;
            case 1:
                category="Insects";
                break;
            case 2:
                category="Flowers";
                break;
            case 3:
                category="Landscape";
                break;


        }
        Intent i=new Intent(Admin_Uploads.this,PostsListActivity.class);
        i.putExtra("categ",category);
        i.putExtra("owner", "admin");
        startActivity(i);
    }
}
