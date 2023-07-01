package com.example.newsfeed;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ViewPostActivity extends AppCompatActivity {

    private ImageView postImageView;
    private TextView postNameTextView;
    private TextView postDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        postImageView = findViewById(R.id.postImageView);
        postNameTextView = findViewById(R.id.postNameTextView);
        postDescriptionTextView = findViewById(R.id.postDescriptionTextView);

        // Retrieve post details from the intent
        String postId = getIntent().getStringExtra("postId");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String postName = getIntent().getStringExtra("postName");
        String postDescription = getIntent().getStringExtra("postDescription");
        String userId = getIntent().getStringExtra("userId");

        // Load post image using Glide library
        Glide.with(this)
                .load(imageUrl)
                .into(postImageView);

        // Set post details to the text views
        postNameTextView.setText(postName);
        postDescriptionTextView.setText(postDescription);
    }
}
