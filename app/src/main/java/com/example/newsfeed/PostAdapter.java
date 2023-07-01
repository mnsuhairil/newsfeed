package com.example.newsfeed;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.postNameTextView.setText(post.getPostName());
        holder.postDescriptionTextView.setText(post.getPostDescription());

        // Load the post image using Glide library
        Glide.with(holder.itemView)
                .load(post.getImageUrl())
                .apply(new RequestOptions().placeholder(R.drawable.default_profile_image))
                .into(holder.postImageView);

        // Set click listener for the post item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the clicked post
                Post clickedPost = postList.get(holder.getAdapterPosition());

                // Start the ViewPostActivity and pass the post details
                Intent intent = new Intent(v.getContext(), ViewPostActivity.class);
                intent.putExtra("postId", clickedPost.getPostId());
                intent.putExtra("imageUrl", clickedPost.getImageUrl());
                intent.putExtra("postName", clickedPost.getPostName());
                intent.putExtra("postDescription", clickedPost.getPostDescription());
                intent.putExtra("userId", clickedPost.getUserId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView;
        TextView postNameTextView;
        TextView postDescriptionTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postImageView);
            postNameTextView = itemView.findViewById(R.id.postNameTextView);
            postDescriptionTextView = itemView.findViewById(R.id.postDescriptionTextView);
        }
    }
}

