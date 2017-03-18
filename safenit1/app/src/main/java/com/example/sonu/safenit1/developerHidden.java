package com.example.sonu.safenit1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class developerHidden extends AppCompatActivity {

    ImageView developerPic;
    TextView developerName , developerIntro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_hidden);
        developerName = (TextView)findViewById(R.id.userName111hidden);
        developerIntro = (TextView)findViewById(R.id.useremailhidden);
        developerPic = (ImageView)findViewById(R.id.userprofilepichidden);
        Picasso.with(getApplicationContext()).load(R.drawable.developer).resize(250, 250).transform(new circularImageView()).into(developerPic);

    }
}
