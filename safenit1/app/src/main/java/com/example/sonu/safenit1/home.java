package com.example.sonu.safenit1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.R.attr.thumb;
//import static com.example.sonu.safenit1.R.id.imageView;b

public class home extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference storageRef ;
    private Button upload;
    private EditText title , disc;
    private ImageView imageButton;
    private  ImageButton uploadButtonImage  ,rotateButtonImage;
    private  ProgressDialog progressDialog;
    private  StorageReference storageReference ;
    private static final int result = 2  ;
    private  DatabaseReference databaseReference ;
    private  DatabaseReference usersData;
    private  Uri uri  = null;
    int rotationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        upload = (Button)findViewById(R.id.upload);
        imageButton = (ImageView) findViewById(R.id.downloaded1);
        title = (EditText)findViewById(R.id.title11) ;
        disc = (EditText)findViewById(R.id.disc);
        uploadButtonImage = (ImageButton)findViewById(R.id.download2);
//        rotateButtonImage = (ImageButton)findViewById(R.id.rotateimage1);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("safenit");
        usersData = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData();
            }
        });

//        rotateButtonImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rotateImageuri();
//            }
//        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signOut();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , result);
            }
        });
        uploadButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signOut();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , result);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == result && resultCode == RESULT_OK) {
             uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

     }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                uri = resultUri;
                Picasso.with(this).load(uri).into(imageButton);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public void UploadData() {
        if(uri != null) {
            progressDialog = ProgressDialog.show(this, "", "Uploading \nPlease Wait...", true);
            final String titl = title.getText().toString();
            final String dis = disc.getText().toString();
            StorageReference filepath = storageRef.child("photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid = user.getUid();
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_LONG).show();
                    final DatabaseReference newData = databaseReference.push();
//                    Calendar c = Calendar.getInstance();
//                    newData.setPriority( c.getTime());
                    usersData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                        String userName;
                            newData.child("title").setValue(titl);
                            newData.child("disc").setValue(dis);
                            newData.child("imageurl").setValue(downloadUrl.toString());
                            newData.child("uid").setValue(uid);
                            DatabaseReference username = usersData.child(uid).child("name");
                            username.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.getValue(String.class);
                                    //do what you want with the email
                                    newData.child("username").setValue(userName);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            DatabaseReference profilePic = usersData.child(uid).child("imageurl");
                            profilePic.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String profilpc = dataSnapshot.getValue(String.class);
                                    //do what you want with the email
                                    newData.child("profilepic").setValue(profilpc);
                                    finish();
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
            });
        }
    }
//    public void rotateImageuri() {
//        if(uri != null) {
//            if(rotationCount % 4 == 0) {
////
//                Picasso.with(this).load(uri).rotate(90f).into(imageButton);
//            }
//            else if(rotationCount % 4 == 1) {
////
//                Picasso.with(this).load(uri).rotate(180f).into(imageButton);
//            }
//            else if(rotationCount % 4 == 2) {
////
//                Picasso.with(this).load(uri).rotate(270f).into(imageButton);
//            }
//            else if(rotationCount % 4 == 3) {
////
//                Picasso.with(this).load(uri).rotate(360f).into(imageButton);
//            }
//            rotationCount ++;
//        }
//    }
}
