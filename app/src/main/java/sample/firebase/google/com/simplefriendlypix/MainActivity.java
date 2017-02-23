package sample.firebase.google.com.simplefriendlypix;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sample.firebase.google.com.simplefriendlypix.model.Post;
import sample.firebase.google.com.simplefriendlypix.model.User;

public class MainActivity extends AppCompatActivity {

    public static class PostHolder extends RecyclerView.ViewHolder {

        private ImageView postImageView;
        private TextView postAuthorTextView;


        public PostHolder(View itemView) {
            super(itemView);
            postImageView = (ImageView) itemView.findViewById(R.id.postImageView);
            postAuthorTextView = (TextView) itemView.findViewById(R.id.postAuthorTextView);
        }

    }

    private static final String TAG = "MainActivity";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPostReference;

    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter<Post, PostHolder> mFirebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            mFirebaseAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = task.getResult().getUser().getUid();
                                Log.d(TAG, "User ID: " + uid);

                                // Write user to database
                                addAnonymousUserToDatabase(uid);

                                loadPosts();
                            }
                        }
                    });
        } else {
            Log.d(TAG, "User signed in with ID: " + mFirebaseAuth.getCurrentUser().getUid());
            loadPosts();
        }
    }

    private void loadPosts() {
        mPostReference = mFirebaseDatabase.getReference("posts");

        mRecyclerView = (RecyclerView) findViewById(R.id.postRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Post, PostHolder>(Post.class, R.layout.item_post,
                        PostHolder.class, mPostReference) {
                    @Override
                    protected void populateViewHolder(PostHolder viewHolder, Post post, int position) {
                        viewHolder.postAuthorTextView.setText(post.author);
                        Glide.with(viewHolder.postImageView.getContext())
                                .load(post.imageUrl)
                                .into(viewHolder.postImageView);
                    }
                };
        mRecyclerView.setAdapter(mFirebaseRecyclerAdapter);
    }

    private void addAnonymousUserToDatabase(String uid) {
        User user = new User();
        user.name = "anonymous";
        mFirebaseDatabase.getReference("users").child(uid).setValue(user).addOnCompleteListener(
                new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User added successfully");
                } else {
                    Log.w(TAG, "Unable to add user.", task.getException());
                }
            }
        });

    }
}
