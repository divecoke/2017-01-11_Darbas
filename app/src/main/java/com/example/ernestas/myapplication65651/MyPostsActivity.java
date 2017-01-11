package com.example.ernestas.myapplication65651;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;


public class MyPostsActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    Firebase firebase;
    private TextView tvName;
    public View itemView;

    private List<PostInformation> postList = new ArrayList<PostInformation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        TextView tvName = (TextView) findViewById(R.id.tvName);

        Firebase.setAndroidContext(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        firebase = new Firebase("https://howdoilooktoday-401f4.firebaseio.com");


        if (user != null)  {
            String email = user.getEmail();
            String first = user.getDisplayName();
            tvName.setText(first + " Posts:");
        }

        postList.clear();
        firebase.child("posts").addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot.exists()) {
                    Map<String, String> map = dataSnapshot.getValue(Map.class);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    PostInformation postInfo;
                    if(map.get("userID").equals(user.getUid())) {
                        if (!dataSnapshot.child("ratings").hasChild(user.getUid())) {
                            postInfo = new PostInformation(map.get("postID"), map.get("userID"), map.get("postText"), map.get("postDate"), map.get("bit64"), "");
                        } else {
                            postInfo = new PostInformation(map.get("postID"), map.get("userID"), map.get("postText"), map.get("postDate"), map.get("bit64"), dataSnapshot.child("ratings").child(user.getUid()).child("progress").getValue().toString());
                        }

                        postList.add(postInfo);
                        populateListView();
                    }
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
        ArrayAdapter<PostInformation> adapter = new MyPostsActivity.MyListAdapter();
        ListView list = (ListView) findViewById(R.id.lvAllPosts);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<PostInformation> {

        public MyListAdapter() {
            super(MyPostsActivity.this, R.layout.single_row, postList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // make sure we have a view to work with
            itemView = convertView;
            itemView = getLayoutInflater().inflate(R.layout.single_row, parent, false);
            FirebaseUser user = firebaseAuth.getCurrentUser();

            //find a car1
            final PostInformation currentPost = postList.get(position);

            //fill the view
            ImageView imageView = (ImageView)itemView.findViewById(R.id.ivPostPhoto);

            byte[] decodedString = Base64.decode(currentPost.getBit64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imageView.setImageBitmap(decodedByte);

            TextView textView = (TextView)itemView.findViewById(R.id.tvPostText);
            textView.setText(currentPost.getPostText());

            Button bDelete = (Button)itemView.findViewById(R.id.bDeletePost);
            bDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebase.child("posts").child(currentPost.getPostID()).removeValue();
                }
            });


            SeekBar sbRatings = (SeekBar)itemView.findViewById(R.id.sbRating);
            if(currentPost.getRatings() != "") {
                sbRatings.setProgress(parseInt(currentPost.getRatings()));
            }


            return itemView;

        }

    }

}
