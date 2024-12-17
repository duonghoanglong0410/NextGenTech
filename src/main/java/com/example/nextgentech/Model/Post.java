package com.example.nextgentech.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryPost category;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "views", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer views = 0;

    @Column(name = "tags")
    private String tags;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Status {
        PENDING,  // Chờ duyệt
        APPROVED, // Đã duyệt
        DRAFT,    // Bản nháp
        REJECTED  // Từ chối duyệt
    }

    public String postImage;

    // Getter và Setter
    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    // Getter và Setter cho id
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // Getter và Setter cho title
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter và Setter cho slug
    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }

    // Getter và Setter cho content
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    // Getter và Setter cho status
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    // Getter và Setter cho author
    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }

    // Getter và Setter cho category
    public CategoryPost getCategory() {
        return category;
    }
    public void setCategory(CategoryPost category) {
        this.category = category;
    }

    // Getter và Setter cho createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter và Setter cho updatedAt
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Getter và Setter cho publishedAt
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    // Getter và Setter cho postImage
    public String getPostImage() {
        return postImage;
    }
    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    // Thuộc tính bổ sung (không lưu trong database)
    @Transient // Không lưu vào database
    private boolean likedByCurrentUser;

    // Getter và Setter
    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }
}
