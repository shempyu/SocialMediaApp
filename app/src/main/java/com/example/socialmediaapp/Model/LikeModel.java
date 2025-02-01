package com.example.socialmediaapp.Model;

public class LikeModel {
    private String likedBy;
    private long likedAt;
 private int likeCount;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public LikeModel() {
    }

    public LikeModel(String likedBy, long likedAt) {
        this.likedBy = likedBy;
        this.likedAt = likedAt;
    }

    public String getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(String likedBy) {
        this.likedBy = likedBy;
    }

    public long getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(long likedAt) {
        this.likedAt = likedAt;
    }
}
