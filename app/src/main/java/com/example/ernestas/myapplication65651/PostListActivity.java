package com.example.ernestas.myapplication65651;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    private Button bMyPosts;
    private Button bGoToPost;
    public String username;

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
        bMyPosts = (Button) findViewById(R.id.bMyPosts);
        bGoToPost = (Button) findViewById(R.id.bGoToPost);
        lv = (ListView) findViewById(R.id.lvAllPosts);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        bGoToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PostActivity.class));
            }
        });

        bMyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MyPostsActivity.class));

            }
        });


        //Log.d("STATE", "Going in here");
        postList.clear();

        progressBar.setVisibility(View.VISIBLE);
        firebase.child("posts").orderByChild("postDate").addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Map<String, String> map = dataSnapshot.getValue(Map.class);
                    FirebaseUser user = firebaseAuth.getCurrentUser();


                    PostInformation postInfo;
                    if (!dataSnapshot.child("ratings").hasChild(user.getUid())) {
                        postInfo = new PostInformation(map.get("postID"), map.get("userID"), map.get("postText"), map.get("postDate"), map.get("bit64"), "");
                    } else {
                        postInfo = new PostInformation(map.get("postID"), map.get("userID"), map.get("postText"), map.get("postDate"), map.get("bit64"), dataSnapshot.child("ratings").child(user.getUid()).child("progress").getValue().toString());
                    }

                    postList.add(postInfo);



                    populateListView();

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                PostInformation postInfo;
                int size = postList.size();
                // Log.d("SIZE", String.valueOf(size));

                for(int a = 0; size > a; a++) {
                    PostInformation crrPost = postList.get(a);
                    //Log.d("asd", map.get("postID") + " ar" + crrPost.getPostID());

                    if(crrPost.getPostID().equals(map.get("postID"))) {
                        //Log.d("id", crrPost.getPostID());
                        if (!dataSnapshot.child("ratings").hasChild(user.getUid())) {
                            postInfo = new PostInformation(map.get("postID"), map.get("userID"), map.get("postText"), map.get("postDate"), map.get("bit64"), "");
                        } else {
                            postInfo = new PostInformation(map.get("postID"), map.get("userID"), map.get("postText"), map.get("postDate"), map.get("bit64"), dataSnapshot.child("ratings").child(user.getUid()).child("progress").getValue().toString());
                        }

                        postList.set(a, postInfo);
                    }
                }
                populateListView();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                for(int a = 0; postList.size() > a; a++) {
                    PostInformation crrPost = postList.get(a);
                    //Log.d("asd", map.get("postID") + " ar" + crrPost.getPostID());
                    Log.d("ID", map.get("postID"));
                    Log.d("ID", crrPost.getPostID());
                    if(crrPost.getPostID().equals(map.get("postID"))) {
                        postList.remove(a);

                    }
                }
                populateListView();
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
            final PostInformation currentPost = postList.get(position);
            seekBar(user.getUid(), currentPost.getPostID());

            //fill the view
            ImageView imageView = (ImageView)itemView.findViewById(R.id.ivPostPhoto);

            byte[] decodedString = Base64.decode(currentPost.getBit64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imageView.setImageBitmap(decodedByte);


            String str_date=currentPost.getPostDate();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = (Date)formatter.parse(str_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TextView textView = (TextView)itemView.findViewById(R.id.tvPostText);
            textView.setText(currentPost.getPostText());

            SeekBar sbRatings = (SeekBar)itemView.findViewById(R.id.sbRating);
            if(currentPost.getRatings() != "") {
                sbRatings.setProgress(parseInt(currentPost.getRatings()));
            }

            TextView textView1 = (TextView)itemView.findViewById(R.id.tvPostedAgo);
            textView1.setText("Posted: " + DateUtils.getRelativeTimeSpanString(date.getTime(), new Date().getTime(), 0));


            final Button bDelete = (Button)itemView.findViewById(R.id.bDeletePost);
            if(currentPost.getUserID().equals(user.getUid())) {
                bDelete.setVisibility(View.VISIBLE);

            } else {
                bDelete.setVisibility(View.GONE);
            }
            bDelete.setTag("close");
            bDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(bDelete.getTag().toString().equals("close")) {

                        bDelete.setBackgroundResource(R.drawable.opendelete);
                        bDelete.setTag("open");
                    } else {
                        Log.d("DELETE", "post");
                        firebase.child("posts").child(currentPost.getPostID()).removeValue();
                    }
                   /* if (bDelete.getTag().toString().equals()) {


                    } else {
                        Log.d("da", "" + bDelete.getBackground().toString());
                    }*/



                    //


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
                if(fromUser) {
                    PostSeekBarInformation postSeekBarInformation = new PostSeekBarInformation(progress);
                    databaseReference.child("posts").child(postID).child("ratings").child(userID).setValue(postSeekBarInformation);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {



            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {





            }
        });
    }

    public void getSeekBarRatings(final String userID, final String postID) {

        /*firebase.child("posts").child(userID).child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("progress")) {
                    if (sbRating.getProgress() != parseInt(dataSnapshot.child("progress").getValue().toString())) {
                        sbRating.setProgress(parseInt(dataSnapshot.child("progress").getValue().toString()));

                        Log.d("sccas", ""+parseInt(dataSnapshot.child("progress").getValue().toString()));
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
    }

}
