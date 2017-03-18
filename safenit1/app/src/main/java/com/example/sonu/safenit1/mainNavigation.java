package com.example.sonu.safenit1;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

public class mainNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference storageRef ;
    private ProgressDialog progressDialog;
    private static final int result = 2;
    private RecyclerView allList;
    private DatabaseReference mDatabase;
    private DatabaseReference mdatabaselike;
    private DatabaseReference mdatabasesent;
    private ImageView profilePics ;
    private TextView usernameTextView , developersHiddenview , totaluploadsbyme;
    private String image1;
    private FloatingActionButton addFloatButton;
    private DatabaseReference mDatabasefeedback;
    private boolean islikedornot = false;
    private Dialog dialog;
    DatabaseReference usersData;
    private Query mQueryCurrentuser;
    Picasso picasso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        allList = (RecyclerView)findViewById(R.id.recycle);
        allList.setHasFixedSize(true);
        allList.setLayoutManager(new LinearLayoutManager(this));

        addFloatButton = (FloatingActionButton)findViewById(R.id.floatingActionButton2);

        profilePics = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.profilepics1);
        usernameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userNameShow);
        developersHiddenview = (TextView) navigationView.getHeaderView(0).findViewById(R.id.Developer);
        totaluploadsbyme = (TextView) navigationView.getHeaderView(0).findViewById(R.id.totalUploads);

        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("safenit");
        mDatabase.keepSynced(true);

        mDatabasefeedback = FirebaseDatabase.getInstance().getReference().child("feedback");
        mDatabasefeedback.keepSynced(true);


        mdatabaselike = FirebaseDatabase.getInstance().getReference().child("likes");
        mdatabaselike.keepSynced(true);

        mdatabasesent = FirebaseDatabase.getInstance().getReference().child("send");
        mdatabasesent.keepSynced(true);

        mQueryCurrentuser = mDatabase.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
        mDatabase.keepSynced(true);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
//                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
//                    startActivity(intent);
//                    finish();
                }
            }
        };
        addFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , home.class));
            }
        });

        developersHiddenview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , developerHidden.class));
            }
        });


        profilePics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , myProfileinfo.class);
                String uid =  mAuth.getCurrentUser().getUid();
                String useremail = mAuth.getCurrentUser().getEmail();
                intent.putExtra("post_key" , uid);
                intent.putExtra("post_email" , useremail);
                startActivity(intent);
            }
        });


        //populate
        onCreateAll();
        setProfilePics();
        myUploadsCount();



    }
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void userInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String providerId = profile.getProviderId();
                String uid = profile.getUid();
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();
//                TextView tv = (TextView)findViewById(R.id.tv);
//                tv.setText(providerId + "\n" + uid + "\n" + name + "\n" + email +"\n"+ photoUrl);
                Toast.makeText(getApplicationContext() , providerId + "\n" + uid + "\n" + name + "\n" + email +"\n"+ photoUrl , Toast.LENGTH_LONG).show();
            }
        }
    }
    public void uploadImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent , result);
    }
    public void uploadFrmGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent , result);
    }


    public void  setProfilePics() {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        DatabaseReference username =  FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("imageurl");
        username.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String  imageurl = dataSnapshot.getValue(String.class);
                Picasso.with(getApplicationContext()).load(imageurl).resize(100 , 100).transform(new circularImageView()).placeholder(R.drawable.avatar).networkPolicy(NetworkPolicy.OFFLINE).into(profilePics, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {
                        Picasso.with(getApplicationContext()).load(imageurl).resize(100 , 100).transform(new circularImageView()).placeholder(R.drawable.avatar).into(profilePics, new Callback() {
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
        username =  FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("name");
        username.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String  name = dataSnapshot.getValue(String.class);
                usernameTextView.setText(name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }




    public void downloadFile(String uRl , String mpost_key) {
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == result && resultCode == RESULT_OK) {
            progressDialog = ProgressDialog.show(this, "","Uploading \nPlease Wait...", true);
            Uri  uri = data.getData();
            StorageReference filepath = storageRef.child("photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext() , "Uploaded" , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void onCreateAll() {

        FirebaseRecyclerAdapter<dataRetreve , ImageViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<dataRetreve, ImageViewHolder>(
                dataRetreve.class,R.layout.layoutshow,ImageViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(ImageViewHolder viewHolder, final dataRetreve model, int position) {
                final int[] likeCounts = {0};
                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDisc(model.getDisc());
                viewHolder.setImageurl(getApplicationContext() , model.getImageurl());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setProfilepic(getApplicationContext() , model.getProfilepic());

                viewHolder.setlikeButton(post_key);
                viewHolder.setlikeNumbers(post_key);
                viewHolder.setseenButton( post_key);



                viewHolder.proFpics.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext() , myProfileinfo.class);
//                        String uid =  mAuth.getCurrentUser().getUid();
//                        String useremail = mAuth.getCurrentUser().getEmail();
                        intent.putExtra("post_key" , post_key);
                        intent.putExtra("post_email" , ".");
                        startActivity(intent);
                    }
                });


                viewHolder.img11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext() , singleAcctivity.class);
                        intent.putExtra("post_key" , post_key);
                        startActivity(intent);
                    }
                });

                viewHolder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFile( model.getImageurl() , post_key);
                    }
                });


                viewHolder.likeorvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        islikedornot = true;

                                mdatabaselike.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(islikedornot) {
                                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                                mdatabaselike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                islikedornot = false;
                                            } else {
                                                mdatabaselike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("liked");
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
            }
        };
        allList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        View view1;
        ImageView likeorvote;
        final ImageView img11 ;
         ImageView download ;
        ImageView proFpics , sentornot ;
        DatabaseReference mDatabaselike2;
        FirebaseAuth mAuth1;
        final TextView likecountsin;
        public ImageViewHolder(View itemView) {
            super(itemView);
            view1 = itemView;

            img11 = (ImageView)view1.findViewById(R.id.uploadedimage);
            proFpics = (ImageView)view1.findViewById(R.id.profilePics);
            likeorvote = (ImageView)view1.findViewById(R.id.likebtn);
            sentornot = (ImageView)view1.findViewById(R.id.doneornot);
            download = (ImageView)view1.findViewById(R.id.download);
            likecountsin = (TextView)view1.findViewById(R.id.likecount1);
            mDatabaselike2 = FirebaseDatabase.getInstance().getReference().child("likes");
            mDatabaselike2.keepSynced(true);
            mAuth1 = FirebaseAuth.getInstance();


        }


        public void setseenButton(final String post_key) {
            mDatabaselike2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild("sent") ){
                        sentornot.setImageResource(R.drawable.done);
                    } else {
                        sentornot.setImageResource(R.drawable.donesingle);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }




        public void setlikeNumbers(final String post_key) {
            mDatabaselike2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child(post_key).hasChild(mAuth1.getCurrentUser().getUid())) {
//                        likeorvote.setImageResource(R.drawable.liked);
//                    } else {
//                        likeorvote.setImageResource(R.drawable.like);
//                    }
                    String str = "0";
                    int  Count = (int) dataSnapshot.child(post_key).getChildrenCount();

                    for(int i  = 1 ; i <= Count ; i++) {
                        str = i + " ";
                    }
                    likecountsin.setText(str);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setlikeButton(final String post_key) {

            mDatabaselike2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth1.getCurrentUser().getUid())) {
                        likeorvote.setImageResource(R.drawable.liked);
                    } else {
                        likeorvote.setImageResource(R.drawable.like);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        public void setTitle(String title) {
            TextView titl11 = (TextView)view1.findViewById(R.id.Titleshow1);

            titl11.setText(title);
        }
        public void setDisc(String disc) {
            TextView disc11 = (TextView)view1.findViewById(R.id.DiscriptionShow);
            disc11.setText(disc);
        }
        public void setUsername(String username) {
            TextView userNametext = (TextView)view1.findViewById(R.id.uploaderName);
            userNametext.setText(" "+username+"   ");
        }
        public void setImageurl(final Context ctx, final String imageurl) {

            Picasso.with(ctx).load(imageurl).placeholder(R.drawable.loading).networkPolicy(NetworkPolicy.OFFLINE).into(img11, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(ctx).load(imageurl).placeholder(R.drawable.loading).into(img11, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError() {
                        }
                    });
                }
            });
//            Picasso.with(ctx).load(imageurl).into(img11);
        }
        public void setProfilepic(final Context ctx, final String profilepic) {
            proFpics = (ImageView)view1.findViewById(R.id.profilePics);

            Picasso.with(ctx).load(profilepic).placeholder(R.drawable.avatar).networkPolicy(NetworkPolicy.OFFLINE).resize(50, 50).transform(new circularImageView()).into(proFpics, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(ctx).load(profilepic).placeholder(R.drawable.avatar).resize(50, 50).transform(new circularImageView()).into(proFpics, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            });
//            Picasso.with(ctx).load(imageurl).into(img11);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    void sendFeedback() {
        progressDialog = ProgressDialog.show(this, "", "Sending \nPlease Wait...", true);
        EditText message = (EditText) dialog.findViewById(R.id.feedbackmessage);
        final String messagesend = message.getText().toString();
        final String uid = mAuth.getCurrentUser().getUid();

        mDatabasefeedback.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDatabasefeedback.child("userfeed").child(mAuth.getCurrentUser().getUid()).setValue(messagesend);
                Toast.makeText(getApplicationContext() , "Sent. \nThank you" , Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext() , "Check Internet Connection" , Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
            }

        });
        progressDialog.dismiss();


    }











    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mainNavigation.this);
            builder.setCancelable(false);
            builder.setTitle("Double tick  ");
            builder.setMessage("It is a indicator that this picture sent to the authority");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //
                }
            });
//                    .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });

            // Create the AlertDialog object and return it
            builder.create().show();
//            return true;
        }
        return super.onOptionsItemSelected(item);
    }







    public void myUploadsCount() {

        mQueryCurrentuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String str = "0";
                int  Count = (int) dataSnapshot.getChildrenCount();

                for(int i  = 1 ; i <= Count ; i++) {
                    str = i + " ";
                }
//                Toast.makeText(getApplicationContext() ,str , Toast.LENGTH_LONG).show();
                totaluploadsbyme.setText("Total : "+str);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }










    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_upload) {
//            uploadImage();
//            startActivity(new Intent(getApplicationContext() , WelcomeActivity.class));
//            finish();
            AlertDialog.Builder builder = new AlertDialog.Builder(mainNavigation.this);
            builder.setCancelable(false);
            builder.setIcon(R.drawable.logo);
            builder.setTitle("Safe and Clean NIT");
            builder.setMessage("Safe Nit is a Initiative to be safe and preoblem free at whole NIT campus\nYou can capture any image (which is not normal for your safety or may cause some problem) and drop it in this app.\nthe picture will act as a avidance before the top authority who is responsible for this");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //
                }
            });


            builder.create().show();

        } else if (id == R.id.nav_gallery_upload) {
//            uploadFrmGallery();
            startActivity(new Intent(getApplicationContext() , home.class));
//            finish();

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(getApplicationContext() , uploadedbyme.class));

        } else if (id == R.id.nav_manage) {


             dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.feedback);
//            dialog.setTitle("Feedback");

            EditText text = (EditText) dialog.findViewById(R.id.feedbackmessage);
//            text.setText("Android custom dialog example!");

            ImageView dialogButton = (ImageView) dialog.findViewById(R.id.sendfeedback);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFeedback();
                    dialog.dismiss();
                }
            });

            dialog.show();


        } else if (id == R.id.nav_profile) {
//            userInfo();
            Intent intent = new Intent(getApplicationContext() , myProfileinfo.class);
            String uid =  mAuth.getCurrentUser().getUid();
            String useremail = mAuth.getCurrentUser().getEmail();
            intent.putExtra("post_key" , uid);
            intent.putExtra("post_email" , useremail);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            signOut();
//            myUploadsCount();
        }else if (id == R.id.nav_developer) {

            Intent intent = new Intent(getApplicationContext() , developersCorner.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
