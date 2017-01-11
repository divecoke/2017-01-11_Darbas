package com.example.ernestas.myapplication65651;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import java.util.Map;

import static com.example.ernestas.myapplication65651.R.id.ivPostPhoto;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {


    private  String IMAGE_URL;

    private String base64Image;

    private ImageView ivTakePhoto;
    private EditText etPostText;
    private Button bPost, bUploadPicture;

    Firebase firebase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressBar progressBar;

    private StorageReference mStorage;

    public static final int GALLERY_INTENT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        // FireBase prisijungimai
        Firebase.setAndroidContext(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebase = new Firebase("https://howdoilooktoday-401f4.firebaseio.com");
        mStorage = FirebaseStorage.getInstance().getReference();
        // All buttons and textviews
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ivTakePhoto = (ImageView) findViewById(R.id.ivTakePicture);
        etPostText = (EditText) findViewById(R.id.etPostText);
        bUploadPicture = (Button) findViewById(R.id.bUploadPicture);
        bPost = (Button) findViewById(R.id.bPost);
        // Set On Click Listeneriai
        bPost.setOnClickListener(this);
        ivTakePhoto.setOnClickListener(this);
        bUploadPicture.setOnClickListener(this);




    }

    @Override
    public void onClick(View v)  {

        if(v == ivTakePhoto || v == bUploadPicture) {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent,"Select Picture"), GALLERY_INTENT);

        }


        if(v == bPost) {
            if (ivTakePhoto.getDrawable() != null) {
                try {
                    savePost();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void savePost() throws ParseException {

        String postText = etPostText.getText().toString().trim();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date()); // Find todays date

        FirebaseUser user = firebaseAuth.getCurrentUser();

        storeImageToFirebase();

        String uuid = UUID.randomUUID().toString();


        PostInformation postInformation = new PostInformation(uuid, user.getUid(), postText, currentDateTime, base64Image, "");


        databaseReference.child("posts").child(uuid).setValue(postInformation); //Saugojimas
        Toast.makeText(this, "Information Saved", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {


            Uri selectedImageUri = data.getData();

            ivTakePhoto.setImageURI(selectedImageUri);
        }
    }

    private void storeImageToFirebase() {
        Bitmap bmp = ((BitmapDrawable) ivTakePhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bYtE);
        bmp.recycle();
        byte[] byteArray = bYtE.toByteArray();


        base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }


}
