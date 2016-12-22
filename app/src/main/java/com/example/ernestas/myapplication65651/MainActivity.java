package com.example.ernestas.myapplication65651;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button bLogout, bProfileSettings, bCapture, bGoToPost, bPosts, bCreators;
    private ImageView iwCapture;
    private ProgressDialog mProgress;


    private StorageReference mStorage;
    private static final int CAMERA_REQUEST_CODE = 99;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final int GALLERY_INTENT = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

        //FirebaseUser user = firebaseAuth.getCurrentUser();


        mStorage = FirebaseStorage.getInstance().getReference();

        bLogout = (Button) findViewById(R.id.bLogout);
        bProfileSettings = (Button) findViewById(R.id.bProfileSettings);
        bGoToPost = (Button) findViewById(R.id.bGoToPost);
        bPosts = (Button) findViewById(R.id.bPosts);
        mProgress = new ProgressDialog(this);
        bCreators = (Button) findViewById(R.id.bCreators);

        bLogout.setOnClickListener(this);
        bProfileSettings.setOnClickListener(this);
        bGoToPost.setOnClickListener(this);
        bPosts.setOnClickListener(this);
        bCreators.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        if (v == bLogout) {
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
        if (v == bProfileSettings) {
            startActivity(new Intent(getApplicationContext(), ProfileSettings.class));
        }

        if(v == bGoToPost) {
            startActivity(new Intent(getApplicationContext(), PostActivity.class));
        }

        if(v == bPosts) {
            startActivity(new Intent(getApplicationContext(), PostListActivity.class));
        }

        if(v == bCreators) {
            startActivity(new Intent(getApplicationContext(), CreatorsActivity.class));
        }
    }



}
