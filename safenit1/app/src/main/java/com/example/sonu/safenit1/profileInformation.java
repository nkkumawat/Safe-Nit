package com.example.sonu.safenit1;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileInformation extends AppCompatActivity {



    DatabaseReference usersData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_information);
        usersData = FirebaseDatabase.getInstance().getReference().child("users");


    }


    public void display() {

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
        final String uid = user.getUid();
             usersData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DatabaseReference username =  usersData.child(uid).child("name");
                username.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String  userName = dataSnapshot.getValue(String.class);
                        //do what you want with the email
//                        newData.child("username").setValue(userName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                DatabaseReference profilePic =  usersData.child(uid).child("imageurl");
                profilePic.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String  profilpc = dataSnapshot.getValue(String.class);
                        //do what you want with the email
//                        newData.child("profilepic").setValue(profilpc);
//                        finish();
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


}
