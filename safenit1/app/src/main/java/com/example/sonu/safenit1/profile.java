package com.example.sonu.safenit1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class profile extends AppCompatActivity {
    EditText  name , rollNo;
    Button  uploadData  ;
    ImageButton chooseImage , rotateimage;
    ImageView pickImage;
    private static final int result = 2  ;
    private DatabaseReference databaseUsers;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorage;
    ProgressDialog progressDialog;
    String userName;
    Uri uri = null;
    int rotationCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("name");

        pickImage = (ImageView)findViewById(R.id.pickAImagepro);
        uploadData = (Button)findViewById(R.id.uploadpro);
        chooseImage = (ImageButton)findViewById(R.id.choosepic);
//        rotateimage = (ImageButton)findViewById(R.id.rotateimage);



        databaseUsers  = FirebaseDatabase.getInstance().getReference().child("users");
        firebaseAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("profile");

//        rotateimage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rotateImageuri();
//            }
//        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent , result);
            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent , result);
            }
        });
        uploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startSetup();
            }
        });

    }
    void startSetup() {

        final String user_id = firebaseAuth.getCurrentUser().getUid();
        if(!TextUtils.isEmpty(userName) && uri  != null) {
            progressDialog = ProgressDialog.show(this, "","Updating \nPlease Wait...", true);
            StorageReference filePath = mStorage.child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    databaseUsers.child(user_id).child("name").setValue(userName);
                    databaseUsers.child(user_id).child("imageurl").setValue(downloadUrl.toString());
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext() , mainNavigation.class));
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == result && resultCode == RESULT_OK) {
             uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1 , 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                uri = resultUri;
                Picasso.with(this).load(uri).resize(250 , 250).transform(new circularImageView()).into(pickImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
