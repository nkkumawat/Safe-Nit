package com.example.sonu.safenit1;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Target;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class singleAcctivity extends AppCompatActivity {

    ImageView uploadedimage , likeimage , profileimage , deletePost ,downloadButton;
    TextView title1 , disc1 , likses , username , usertoolbar;
    String mpost_key = null;
    String urlimage = null;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLikes;
    private FirebaseAuth mAuth1;
    private boolean islikedornot = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_acctivity);
         mpost_key = getIntent().getExtras().getString("post_key");
        //Toast.makeText(getApplicationContext() , post_key , Toast.LENGTH_LONG).show();

        uploadedimage = (ImageView) findViewById(R.id.uploadedimage1);
        downloadButton = (ImageView) findViewById(R.id.downloadButton);
        likeimage = (ImageView) findViewById(R.id.likebtn1);
        profileimage = (ImageView) findViewById(R.id.profilePics1);
        deletePost = (ImageView) findViewById(R.id.deletepost);
        deletePost.setVisibility(View.INVISIBLE);
        title1 = (TextView)findViewById(R.id.Titleshow11);
        disc1 = (TextView)findViewById(R.id.DiscriptionShow1);
        likses = (TextView)findViewById(R.id.likecount11);
        username = (TextView)findViewById(R.id.uploaderName1);
        usertoolbar = (TextView)findViewById(R.id.usertoolbar);


        mAuth1 = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("safenit");
        mDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("likes");

        mDatabase.child(mpost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_disc = (String) dataSnapshot.child("disc").getValue();
                final String post_upload_image = (String) dataSnapshot.child("imageurl").getValue();
                final String post_profile_image = (String) dataSnapshot.child("profilepic").getValue();
                String post_username = (String) dataSnapshot.child("username").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                title1.setText(post_title);
                disc1.setText(post_disc);
                username.setText(post_username);
                usertoolbar.setText(post_username);

                if(mAuth1.getCurrentUser().getUid().equals(post_uid)) {
                    deletePost.setVisibility(View.VISIBLE);
                }

                urlimage = post_upload_image;
//                downloadFile(post_upload_image);
                OkHttpClient okHttpClient = new OkHttpClient();
                File customCacheDirectory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/SafenitCache");
                okHttpClient.setCache(new Cache(customCacheDirectory, Integer.MAX_VALUE));
                OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);
                final Picasso picasso = new Picasso.Builder(getApplicationContext()).downloader(okHttpDownloader).build();

                picasso.load(post_upload_image).placeholder(R.drawable.loading).networkPolicy(NetworkPolicy.OFFLINE).into(uploadedimage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {
                        picasso.load(post_upload_image).placeholder(R.drawable.loading).into(uploadedimage, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError() {
                            }
                        });
                    }
                });


                picasso.with(getApplicationContext()).load(post_profile_image).placeholder(R.drawable.avatar).networkPolicy(NetworkPolicy.OFFLINE).resize(50, 50).transform(new circularImageView()).into(profileimage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {
                        picasso.load(post_profile_image).placeholder(R.drawable.avatar).resize(50, 50).transform(new circularImageView()).into(profileimage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        mDatabaseLikes.child(mpost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                String str = "0";
                for(int i = 1; i <= count ; i++) {
                    str = i + " ";
                }
                likses.setText(str);
                if (dataSnapshot.hasChild(mAuth1.getCurrentUser().getUid())) {
                    likeimage.setImageResource(R.drawable.liked);
                } else {
                    likeimage.setImageResource(R.drawable.like);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mpost_key).removeValue();
                finish();
            }
        });

        likeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                islikedornot = true;

                mDatabaseLikes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(islikedornot) {
                            if (dataSnapshot.child(mpost_key).hasChild(mAuth1.getCurrentUser().getUid())) {
                                mDatabaseLikes.child(mpost_key).child(mAuth1.getCurrentUser().getUid()).removeValue();
                                islikedornot = false;
                            } else {
                                mDatabaseLikes.child(mpost_key).child(mAuth1.getCurrentUser().getUid()).setValue("liked");
                                islikedornot = false;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });



        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(urlimage);
//                Toast.makeText(getApplicationContext() , urlimage , Toast.LENGTH_LONG).show();
            }
        });


    }



    public void downloadFile(String uRl) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/SafeNIT");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Downloading Image")
                .setDescription("SafeNIT Image.")
                .setDestinationInExternalPublicDir("/SafeNIT", mpost_key+"safenit.jpg");

        mgr.enqueue(request);

    }


}
