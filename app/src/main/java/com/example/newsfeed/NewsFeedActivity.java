package com.example.newsfeed;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;

    private Button addPostButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private ImageView postImageView;
    private EditText postNameEditText;
    private EditText postDescriptionEditText;

    private Uri selectedImageUri;


    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        addPostButton = findViewById(R.id.addPostButton);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPostDialog();
            }
        });
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        fetchPosts();
    }
    private void fetchPosts() {
        DatabaseReference postsRef = mDatabase.child("posts");

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    postList.add(post);
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void showAddPostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_post, null);
        builder.setView(dialogView);

        postImageView = dialogView.findViewById(R.id.postImageView);
        postNameEditText = dialogView.findViewById(R.id.postNameEditText);
        postDescriptionEditText = dialogView.findViewById(R.id.postDescriptionEditText);

        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String postName = postNameEditText.getText().toString().trim();
                String postDescription = postDescriptionEditText.getText().toString().trim();

                if (TextUtils.isEmpty(postName) || TextUtils.isEmpty(postDescription) || selectedImageUri == null) {
                    Toast.makeText(NewsFeedActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage(selectedImageUri, postName, postDescription);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                postImageView.setImageURI(selectedImageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri, final String postName, final String postDescription) {
        StorageReference fileRef = mStorage.child("post_images").child(imageUri.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                createPost(imageUrl, postName, postDescription);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(NewsFeedActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void createPost(String imageUrl, String postName, String postDescription) {
        String userId = mAuth.getCurrentUser().getUid();

        DatabaseReference postRef = mDatabase.child("posts").push();
        String postId = postRef.getKey();

        Post post = new Post(postId, imageUrl, postName, postDescription, userId);
        postRef.setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(NewsFeedActivity.this, "Post created successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NewsFeedActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
