package com.negi.wallpapers.myimages.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.negi.wallpapers.myimages.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowImage extends Fragment {


    public ShowImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.image, container, false);
        ImageView iv = (ImageView)v.findViewById(R.id.imageView);
        String url = getArguments().getString("url");
        Glide.with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv);

        return v;
    }

}