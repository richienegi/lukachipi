package com.negi.ritika.myimages.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.negi.ritika.myimages.Constants;
import com.negi.ritika.myimages.Main2Activity;
import com.negi.ritika.myimages.Model.All_Images;
import com.negi.ritika.myimages.R;
import com.negi.ritika.myimages.UploadListAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class Image_Upload extends Fragment {

    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private Spinner spinner;
    Button b1;
    String likes = "0";
    String downloads = "0";
    RecyclerView uploadList;
    int RESULT_LOAD_IMAGE1 = 1;
    List<String> filename;
    List<String> filedone;
    List<Bitmap> images;
    UploadListAdapter uploadListAdapter;


    public Image_Upload() {
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
        View v = inflater.inflate(R.layout.fragment_image__upload, container, false);
        FirebaseApp.initializeApp(getContext());

        storageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_UPLOADS);
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        b1 = (Button) v.findViewById(R.id.browse);

        spinner = (Spinner) v.findViewById(R.id.spinner);
        filename = new ArrayList<>();
        filedone = new ArrayList<>();
        images = new ArrayList<>();
        uploadListAdapter = new UploadListAdapter(filename, filedone, images);


        uploadList = (RecyclerView) v.findViewById(R.id.uploadlist);
        uploadList.setLayoutManager(new LinearLayoutManager(getContext()));
        uploadList.setHasFixedSize(true);
        uploadList.setAdapter(uploadListAdapter);
        b1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "select picture"), RESULT_LOAD_IMAGE1);
            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int totalItem = data.getClipData().getItemCount();
                Toast.makeText(getContext(), "" + totalItem, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < totalItem; i++) {

                    final ProgressDialog pd = new ProgressDialog(getContext());
                    pd.setCancelable(false);
                    pd.setMessage("Uploading...");
                    pd.show();

                    Uri uri = data.getClipData().getItemAt(i).getUri();

                    try {
                        images.add(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String filenn = getFilename(uri);
                    filename.add(filenn);
                    filedone.add("uploading");
                    uploadListAdapter.notifyDataSetChanged();

                    final int finalI = i;
                    StorageReference sRef = storageReference.child(spinner.getSelectedItem().toString() + "/" + filename.get(i) + "." + "png");


                    sRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filedone.remove(finalI);
                            filedone.add(finalI, "done");
                            uploadListAdapter.notifyDataSetChanged();

                            DatabaseReference ref = mDatabase.child(spinner.getSelectedItem().toString());

                            //adding an uploadimage to firebase database
                            String uploadId = ref.push().getKey();

                            All_Images all_images = new All_Images(uploadId, taskSnapshot.getDownloadUrl().toString(),
                                    likes, downloads, time(), spinner.getSelectedItem().toString());

                            ref.child("admin").child(uploadId).setValue(all_images);

                            Toast.makeText(getContext(), "Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), " Sorry" + e, Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                    // Toast.makeText(this, "Selected multiple files", Toast.LENGTH_SHORT).show();
                }

            } else if (data.getData() != null) {
                Toast.makeText(getContext(), "Single Image Selected", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public String getFilename(Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }
            } finally {
                cursor.close();
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }

        return result;
    }

    public String time() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
// you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

}
