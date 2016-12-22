package com.example.ernestas.myapplication65651;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Integer.parseInt;

public class PostListActivity extends AppCompatActivity {


    Firebase firebase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ImageView ivPostPhoto;
    private SeekBar sbRating;
    ListView lv;
    public View itemView;
    private ProgressBar progressBar;
    public RatingBar rbRatingPost;

    private List<PostInformation> postList = new ArrayList<PostInformation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        // Firebase Connection
        Firebase.setAndroidContext(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        firebase = new Firebase("https://howdoilooktoday-401f4.firebaseio.com");
        // All views
        ivPostPhoto = (ImageView) findViewById(R.id.ivPostPhoto);
        lv = (ListView) findViewById(R.id.lvAllPosts);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        //postList.clear();
        Log.d("STATE", "Going in here");
        postList.clear();
        progressBar.setVisibility(View.VISIBLE);
        firebase.child("posts").addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);

                PostInformation postInfo = new PostInformation(map.get("postID"), map.get("userID"),map.get("postText"), map.get("postDate"), map.get("bit64"));
                postList.add(postInfo);
                populateListView();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    private void populateListView() {
        ArrayAdapter<PostInformation> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.lvAllPosts);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<PostInformation> {

        public MyListAdapter() {
            super(PostListActivity.this, R.layout.single_row, postList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // make sure we have a view to work with
            itemView = convertView;
            itemView = getLayoutInflater().inflate(R.layout.single_row, parent, false);
            FirebaseUser user = firebaseAuth.getCurrentUser();

            //find a car
            PostInformation currentPost = postList.get(position);
            seekBar(user.getUid(), currentPost.getPostID());
            getSeekBarRatings(user.getUid(), currentPost.getPostID());
            //fill the view
            ImageView imageView = (ImageView)itemView.findViewById(R.id.ivPostPhoto);

            byte[] decodedString = Base64.decode(currentPost.getBit64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imageView.setImageBitmap(decodedByte);



            TextView textView = (TextView)itemView.findViewById(R.id.tvPostText);
            textView.setText(currentPost.getPostText());



            firebase.child("users").child(currentPost.getUserID()).child("user_information").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*
                    TextView textView1 = (TextView)itemView.findViewById(R.id.tvPosterName);
                    textView1.setText("Poster is: " + dataSnapshot.child("firstName").getValue().toString() + " " + dataSnapshot.child("lastName").getValue().toString());

                    */
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });





            return itemView;

        }

    }

    public void seekBar(final String userID, final String postID) {
        sbRating = (SeekBar)itemView.findViewById(R.id.sbRating);


        sbRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress_value;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {



            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {



                PostSeekBarInformation postSeekBarInformation = new PostSeekBarInformation(sbRating.getProgress());


                databaseReference.child("postsRatings").child(userID).child(postID).setValue(postSeekBarInformation);

            }
        });
    }

    public void getSeekBarRatings(final String userID, final String postID) {

        firebase.child("postsRatings").child(userID).child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("rating")) {
                    if (sbRating.getProgress() != parseInt(dataSnapshot.child("rating").getValue().toString())) {
                        sbRating.setProgress(parseInt(dataSnapshot.child("rating").getValue().toString()));
                        Log.d("haha", String.valueOf(parseInt(dataSnapshot.child("rating").getValue().toString())));
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
