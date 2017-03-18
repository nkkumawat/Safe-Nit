package com.example.sonu.safenit1;

import android.app.Application;
import android.os.Environment;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by sonu on 6/3/17.
 */

public class SafeNit extends Application {
   @Override
   public void onCreate(){
       super.onCreate();
       FirebaseDatabase.getInstance().setPersistenceEnabled(true);

//
//       OkHttpClient okHttpClient = new OkHttpClient();
//       File customCacheDirectory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/MyCache");
//       okHttpClient.setCache(new Cache(customCacheDirectory, Integer.MAX_VALUE));
//       OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);
//       Picasso picasso = new Picasso.Builder(getApplicationContext()).downloader(okHttpDownloader).build();

       Picasso.Builder builder = new Picasso.Builder(this);
       builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
       Picasso built = builder.build();
       built.setIndicatorsEnabled(false);
       built.setLoggingEnabled(true);
       Picasso.setSingletonInstance(built);
   }

}
