package com.example.sonu.safenit1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class cameraUpload extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference storageRef ;
    Button upload;
    EditText title , disc;
    ImageView imageButton;
    ProgressDialog progressDialog;
    StorageReference storageReference ;
    private static final int result = 2  ;
    DatabaseReference databaseReference ;
    DatabaseReference usersData;
    Uri uri ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_upload);
        upload = (Button) findViewById(R.id.upload1);
        imageButton = (ImageView) findViewById(R.id.downloaded1);
        title = (EditText)findViewById(R.id.title11) ;
        disc = (EditText)findViewById(R.id.disc1);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("safenit");

        usersData = FirebaseDatabase.getInstance().getReference().child("users");


        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
//                    finish();
                }
            }
        };
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData();
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signOut();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.setType("image/*");
                startActivityForResult(intent , result);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == result && resultCode == RESULT_OK) {
            uri = data.getData();
            imageButton.setImageURI(uri);
        }
    }
    public void UploadData() {
        progressDialog = ProgressDialog.show(this, "","Uploading \nPlease Wait...", true);
        final String titl = title.getText().toString();
        final String dis = disc.getText().toString();
        StorageReference filepath = storageRef.child("photos").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            progressDialog.dismiss();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    for (UserInfo profile : user.getProviderData()) {
//                        String providerId = profile.getProviderId();
//
//                        String name = profile.getDisplayName();
//                        String email = profile.getEmail();
//                        Uri photoUrl = profile.getPhotoUrl();
//                        Toast.makeText(getApplicationContext() , providerId + "\n" + uid + "\n" + name + "\n" + email +"\n"+ photoUrl , Toast.LENGTH_LONG).show();
//                    }
            final String uid = user.getUid();
            final Uri downloadUrl = taskSnapshot.getDownloadUrl();
            Toast.makeText(getApplicationContext() , "Uploaddded" , Toast.LENGTH_LONG).show();
            final DatabaseReference newData = databaseReference.push();

            usersData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newData.child("title").setValue(titl);
                    newData.child("disc").setValue(dis);
                    newData.child("imageurl").setValue(downloadUrl.toString());
                    newData.child("uid").setValue(uid);
                    newData.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext() , mainNavigation.class));
                                finish();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });






//
            }
        });
    }

}
