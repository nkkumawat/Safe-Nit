package com.example.sonu.safenit1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class myProfileinfo extends AppCompatActivity {

    ImageView profilePics;
    TextView usernamepro , userEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRefrence;
    private String mpost_key = null , mpost_email;
    String uid1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profileinfo);



        mpost_key = getIntent().getExtras().getString("post_key");
        mpost_email = getIntent().getExtras().getString("post_email");


//        Toast.makeText(getApplicationContext() , mpost_email + mpost_key , Toast.LENGTH_LONG).show();

        usernamepro = (TextView)findViewById(R.id.userName111);
        userEmail = (TextView)findViewById(R.id.useremail);

        profilePics = (ImageView)findViewById(R.id.userprofilepichidden);

        userEmail.setText(mpost_email);


        mAuth = FirebaseAuth.getInstance();
        mDatabaseRefrence = FirebaseDatabase.getInstance().getReference().child("user");


        setProfilePics();

    }
    public void  setProfilePics() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        if(mpost_email.equals(".")) {
            DatabaseReference userUid = FirebaseDatabase.getInstance().getReference().child("safenit").child(mpost_key).child("uid");

            userUid.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    uid1 = (String) dataSnapshot.getValue();
//                    Toast.makeText(getApplicationContext(), uid + "    \n" + uid1, Toast.LENGTH_LONG).show();
                    DatabaseReference username;

                    username = FirebaseDatabase.getInstance().getReference().child("users").child(uid1).child("imageurl");

                    username.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String imageurl = dataSnapshot.getValue(String.class);
                            Picasso.with(getApplicationContext()).load(imageurl).resize(250, 250).transform(new circularImageView()).placeholder(R.drawable.avatar).networkPolicy(NetworkPolicy.OFFLINE).into(profilePics, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getApplicationContext()).load(imageurl).resize(250, 250).transform(new circularImageView()).placeholder(R.drawable.avatar).into(profilePics, new Callback() {
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

                    username = FirebaseDatabase.getInstance().getReference().child("users").child(uid1).child("name");

                    username.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String name = dataSnapshot.getValue(String.class);
                            usernamepro.setText(name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
//        Toast.makeText(getApplicationContext() , uid + "    \n" + uid1, Toast.LENGTH_LONG).show();
        else {
            DatabaseReference username;

            username = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("imageurl");

            username.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String imageurl = dataSnapshot.getValue(String.class);
                    Picasso.with(getApplicationContext()).load(imageurl).resize(250, 250).transform(new circularImageView()).placeholder(R.drawable.avatar).networkPolicy(NetworkPolicy.OFFLINE).into(profilePics, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(imageurl).resize(250, 250).transform(new circularImageView()).placeholder(R.drawable.avatar).into(profilePics, new Callback() {
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

            username = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("name");

            username.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String name = dataSnapshot.getValue(String.class);
                    usernamepro.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
