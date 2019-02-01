package com.negi.ritika.myimages.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.negi.ritika.myimages.Fragments.ImagePreview;
import com.negi.ritika.myimages.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity implements ImagePreview.OnFragmentInteractionListener {
    TextView mtitle;
    private ImageView mdImage;
    private CardView ci;
    private Button msave, mshare, mwall;
    private Bitmap bitmap;
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    private String url, id, cat, downloads, uid;

    private DatabaseReference ref;

    private Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ci = findViewById(R.id.card);

        // mtitle=findViewById(R.id.titletv);
        mdImage = findViewById(R.id.mdetail);
        msave = findViewById(R.id.SaveBtn);
        mshare = findViewById(R.id.ShareBtn);
        mwall = findViewById(R.id.WallBtn);

        url = getIntent().getStringExtra("image");
        id = getIntent().getStringExtra("id");
        cat = getIntent().getStringExtra("cate");
        downloads = getIntent().getStringExtra("downloads");
        uid = getIntent().getStringExtra("uid");

        ref = FirebaseDatabase.getInstance().getReference("uploads").child(cat).child(uid).child(id);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Wait...");
        pd.show();
        Picasso.with(this).load(url).fetch(new Callback() {
            @Override
            public void onSuccess() {
                mdImage.setAlpha(0f);
                pd.dismiss();
                Picasso.with(PostDetailActivity.this).load(url).into(mdImage);
                mdImage.animate().setDuration(400).alpha(1f).start();
            }

            @Override
            public void onError() {

            }
        });


        //save Button click Handle
        msave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if os > marshmello we need runtime permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE);
                    } else {
                        //permission is already rganted

                        new saveImage().execute();
                    }
                } else {
                    //os is >marshmello no need of permission
                    new saveImage().execute();
                }

            }
        });


        //share btn click Handle

        mshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //set WallPaper click Handle;

        mwall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ci.setVisibility(View.GONE);

                previewe(url);
            }
        });


    }

    class saveImage extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bitmap = ((BitmapDrawable) mdImage.getDrawable()).getBitmap();
            dialog = new ProgressDialog(PostDetailActivity.this);
            dialog.setMessage("Saving...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/setWallPaper");
            dir.mkdir();
            String imageName = timeStamp + " .PNG";
            File file = new File(dir, imageName);
            OutputStream out;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                String data = imageName + " saved to " + dir;
                return data;

            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if(!s.isEmpty())
            {
                int d = Integer.parseInt(downloads);
                d++;

                downloads = String.valueOf(d);

//                All_Images m = new All_Images(cat, url, downloads, id);

                Map<String, Object> map = new HashMap<>();
                map.put("downloads", downloads);

                ref.updateChildren(map);
//                ref.setValue(m);
                Toast.makeText(PostDetailActivity.this, s, Toast.LENGTH_SHORT).show();

                finish();
            }
            else
            {
                Toast.makeText(PostDetailActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission is granted
                    new saveImage().execute();
                } else {
                    Toast.makeText(this, "enable permission to save Image", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void previewe(String url) {
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            tb.setVisibility(View.GONE);
        }
        ImagePreview p = new ImagePreview();

        Bundle b = new Bundle();
        b.putString("passimage", url);

        p.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, p).addToBackStack(null).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
