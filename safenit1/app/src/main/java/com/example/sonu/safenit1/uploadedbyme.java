package com.example.sonu.safenit1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class uploadedbyme extends AppCompatActivity {

    private RecyclerView allList1;
    private DatabaseReference mDatabase;
    DatabaseReference usersData;
    private String uid;
    private Query mQueryCurrentuser;
    FirebaseAuth mAuth;
    private boolean islikedornot = false;
    private DatabaseReference mdatabaselike;

    public uploadedbyme() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadedbyme);

        allList1 = (RecyclerView)findViewById(R.id.recycleView1);
        allList1.setHasFixedSize(true);
        allList1.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        mAuth = FirebaseAuth.getInstance();
        String useruid  =mAuth.getCurrentUser().getUid();
        usersData = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("safenit");
        mQueryCurrentuser = mDatabase.orderByChild("uid").equalTo(useruid);
        mDatabase.keepSynced(true);

        mdatabaselike = FirebaseDatabase.getInstance().getReference().child("likes");
        mdatabaselike.keepSynced(true);
        onCreateAll();
    }

    public void onCreateAll() {

        FirebaseRecyclerAdapter<dataRetreve , mainNavigation.ImageViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<dataRetreve, mainNavigation.ImageViewHolder>(
                dataRetreve.class,R.layout.uploadbymelayout,mainNavigation.ImageViewHolder.class, mQueryCurrentuser ) {
            @Override
            protected void populateViewHolder(mainNavigation.ImageViewHolder viewHolder, dataRetreve model, int position) {
                final int[] likeCounts = {0};
                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDisc(model.getDisc());
                viewHolder.setImageurl(getApplicationContext() , model.getImageurl());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setProfilepic(getApplicationContext() , model.getProfilepic());

                viewHolder.setlikeButton(post_key);
                viewHolder.setlikeNumbers(post_key);


                viewHolder.img11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext() , singleAcctivity.class);
                        intent.putExtra("post_key" , post_key);
                        startActivity(intent);
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
        allList1.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        View view1;
        ImageView likeorvote;
        final ImageView img11 ;
        DatabaseReference mDatabaselike2;
        FirebaseAuth mAuth1;
        final TextView likecountsin;
        public ImageViewHolder(View itemView) {
            super(itemView);
            view1 = itemView;

            img11 = (ImageView)view1.findViewById(R.id.uploadedimage);
            likeorvote = (ImageView)view1.findViewById(R.id.likebtn);
            likecountsin = (TextView)view1.findViewById(R.id.likecount1);
            mDatabaselike2 = FirebaseDatabase.getInstance().getReference().child("likes");
            mDatabaselike2.keepSynced(true);
            mAuth1 = FirebaseAuth.getInstance();


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

            Picasso.with(ctx).load(imageurl).placeholder(R.drawable.loading).networkPolicy(NetworkPolicy.OFFLINE).resize(600, 500).into(img11, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(ctx).load(imageurl).placeholder(R.drawable.loading).resize(600, 500).into(img11, new Callback() {
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
            final ImageView proFpics = (ImageView)view1.findViewById(R.id.profilePics);
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
}
