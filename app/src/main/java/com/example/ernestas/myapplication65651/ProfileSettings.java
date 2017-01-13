package com.example.ernestas.myapplication65651;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
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
import com.firebase.client.utilities.Utilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class ProfileSettings extends AppCompatActivity implements View.OnClickListener{

    EditText etFirstName, etLastName, etAddress;
    TextView tvViewEmail;
    Button bSaveInformation, bUploadPicture;
    ImageView ivProfilePicture;
    Firebase firebase;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;
    private String base64Image;
    public static final int GALLERY_INTENT = 2;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        Firebase.setAndroidContext(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        firebase = new Firebase("https://howdoilooktoday-401f4.firebaseio.com");

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvViewEmail = (TextView) findViewById(R.id.tvViewEmail);
        bSaveInformation = (Button) findViewById(R.id.bSaveInformation);
        bUploadPicture = (Button) findViewById(R.id.bUploadPicture);
        ivProfilePicture = (ImageView) findViewById(R.id.ivTakePicture);

        if (user != null)  {
            String email = user.getEmail();
            String first = user.getDisplayName();
            tvViewEmail.setText("Welcome: " + first);
        }

        bSaveInformation.setOnClickListener(this);
        ivProfilePicture.setOnClickListener(this);
        bUploadPicture.setOnClickListener(this);

        Firebase dataRef = firebase.child("users").child(user.getUid()).child("user_information");
        progressBar.setVisibility(View.VISIBLE);
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);

                if (dataSnapshot.hasChild("firstName")) {
                    if (dataSnapshot.hasChild("firstName")) {
                        String FirstName = map.get("firstName");
                        etFirstName.setText(FirstName);
                    }
                    if(dataSnapshot.hasChild("lastName")) {
                        String LastName = map.get("lastName");
                        etLastName.setText(LastName);
                    }
                    if(dataSnapshot.hasChild("address")) {
                        String Address = map.get("address");
                        etAddress.setText(Address);
                    }
                    if(dataSnapshot.hasChild("profilePicture")) {
                        //fill the view
                        byte[] decodedString = Base64.decode(map.get("profilePicture"), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        ivProfilePicture.setImageBitmap(decodedByte);


                        bUploadPicture.setText("Change Profile Picture");
                    }
                } else {
                    String userSplit[] = user.getDisplayName().split(" ");
                    etFirstName.setText(userSplit[0]);
                    etLastName.setText(userSplit[1]);
                    Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).into(ivProfilePicture);
                }

                Log.d("context", user.getPhotoUrl().toString());



                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    private void saveUserInformation() {
        String first = etFirstName.getText().toString().trim();
        String last = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        storeImageToFirebase();
        UserInformation userInformation = new UserInformation(first, last, address, base64Image);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(TextUtils.isEmpty(first)) {
            Toast.makeText(this, "You must fill First Name edit text bar", Toast.LENGTH_LONG).show();
        } else if(TextUtils.isEmpty(last)) {
            Toast.makeText(this, "You must fill Last Name edit text bar", Toast.LENGTH_LONG).show();
        } else if(TextUtils.isEmpty(address)) {
            Toast.makeText(this, "You must fill Address edit text bar", Toast.LENGTH_LONG).show();
        } else {
            databaseReference.child("users").child(user.getUid()).child("user_information").setValue(userInformation); //Saugojimas
            Toast.makeText(this, "Information Saved", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        if(v == bSaveInformation) {
            saveUserInformation();
        }

        if(v == ivProfilePicture || v == bUploadPicture) {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent,"Select Picture"), GALLERY_INTENT);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            ivProfilePicture.setImageURI(selectedImageUri);
        }
    }

    private void storeImageToFirebase() {
        Bitmap bmp = ((BitmapDrawable) ivProfilePicture.getDrawable()).getBitmap();
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bYtE);
        bmp.recycle();
        byte[] byteArray = bYtE.toByteArray();
        base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
