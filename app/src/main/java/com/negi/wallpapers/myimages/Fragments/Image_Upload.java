package com.negi.wallpapers.myimages.Fragments;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.negi.wallpapers.myimages.Adapters.UploadListAdapter;
import com.negi.wallpapers.myimages.Constants;
import com.negi.wallpapers.myimages.FileUtil;
import com.negi.wallpapers.myimages.Model.All_Images;
import com.negi.wallpapers.myimages.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class Image_Upload extends Fragment {

    private StorageReference storageReference;
    private StorageReference storageReference2;
    private DatabaseReference mDatabase;
    Button b1;
    String downloads = "0";
    RecyclerView uploadList;
    int RESULT_LOAD_IMAGE1 = 1;
    List<String> filename;
    List<String> filedone;
    List<Bitmap> images;
    UploadListAdapter uploadListAdapter;
    FirebaseAuth auth;

    private File actualImage;
    private File compressedImage;

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

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_UPLOADS);
        storageReference2 = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_THUMB);
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        b1 = (Button) v.findViewById(R.id.browse);

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
                if (images.size() > 0) {
                    images.clear();
                    filedone.clear();
                    filename.clear();
                    uploadListAdapter.notifyDataSetChanged();
                }
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
                final int totalItem = data.getClipData().getItemCount();
                if(totalItem>20)
                {
                    Toast.makeText(getContext(), "Select Maximum 10 Images", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.setCancelable(false);
                pd.setMessage("Uploading All Images...");
                pd.show();

                Toast.makeText(getContext(), "" + totalItem, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < totalItem; i++) {

                    Uri uri = data.getClipData().getItemAt(i).getUri();

                    try {
                        actualImage = FileUtil.from(getContext(), uri);
                        compressedImage = new Compressor(getContext())
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES+"/WallArt/").getAbsolutePath())
                                .compressToFile(actualImage);

                        images.add(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri));
                        String filenn = getFilename(uri);
                        filename.add(filenn);
                        filedone.add("uploading");
                        uploadListAdapter.notifyDataSetChanged();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    final Uri imagethumb = getImageContentUri(getContext(), compressedImage);

                    final int finalI = i;
                    StorageReference sRef = storageReference.child(filename.get(i) + "." + "png");
                    final StorageReference thumb = storageReference2.child(filename.get(i) + "." + "png");

                    sRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            thumb.putFile(imagethumb).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    filedone.remove(finalI);
                                    filedone.add(finalI, "done");
                                    uploadListAdapter.notifyDataSetChanged();

                                    String uid = "Admin";

                                    DatabaseReference ref = mDatabase.child(uid);

                                    //adding an uploadimage to firebase database
                                    String uploadId = ref.push().getKey();

                                    All_Images u_images = new All_Images(uploadId, uid, uid, downloads, time(), taskSnapshot.getDownloadUrl().toString(), task.getResult().getDownloadUrl().toString());

                                    ref.child(uploadId).setValue(u_images);

                                    Toast.makeText(getContext(), "Uploaded Sucessfully", Toast.LENGTH_SHORT).show();

                                    if(finalI==totalItem-1)
                                    {
                                        pd.dismiss();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), " Sorry" + e, Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    //145
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), " Sorry" + e, Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }

            } else if (data.getData() != null) {

                Uri uri = data.getData();

                try {

                    actualImage = FileUtil.from(getContext(), uri);
                    compressedImage = new Compressor(getContext())
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES+"/WallArt/").getAbsolutePath())
                            .compressToFile(actualImage);
                    images.add(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri));
                    String filenn = getFilename(uri);
                    filename.add(filenn);
                    filedone.add("uploading");
                    uploadListAdapter.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.setCancelable(false);
                pd.setMessage("Uploading...");
                pd.show();

                final Uri imagethumb = getImageContentUri(getContext(), compressedImage);

                final int finalI = 0;
                StorageReference sRef = storageReference.child(filename.get(0) + "." + "png");
                final StorageReference thumb = storageReference2.child(filename.get(0) + "." + "png");

                sRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        thumb.putFile(imagethumb).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                filedone.remove(finalI);
                                filedone.add(finalI, "done");
                                uploadListAdapter.notifyDataSetChanged();

                                String uid = "Admin";

                                DatabaseReference ref = mDatabase.child(uid);

                                //adding an uploadimage to firebase database
                                String uploadId = ref.push().getKey();

                                All_Images u_images = new All_Images(uploadId, uid, uid, downloads, time(), taskSnapshot.getDownloadUrl().toString(), task.getResult().getDownloadUrl().toString());

                                ref.child(uploadId).setValue(u_images);

                                Toast.makeText(getContext(), "Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), " Sorry" + e, Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        pd.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
            }

        }
        else
        {
            Toast.makeText(getContext(), "Please select an image to upload", Toast.LENGTH_SHORT).show();
        }

    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
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

        String localTime = String.valueOf(cal.getTimeInMillis());

        return localTime;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

}
