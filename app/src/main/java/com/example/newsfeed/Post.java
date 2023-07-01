package com.example.newsfeed;

public class Post {
    private String postId;
    private String imageUrl;
    private String postName;
    private String postDescription;
    private String userId;

    public Post() {
        // Default constructor required for Firebase
    }

    public Post(String postId, String imageUrl, String postName, String postDescription, String userId) {
        this.postId = postId;
        this.imageUrl = imageUrl;
        this.postName = postName;
        this.postDescription = postDescription;
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPostName() {
        return postName;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public String getUserId() {
        return userId;
    }
}

