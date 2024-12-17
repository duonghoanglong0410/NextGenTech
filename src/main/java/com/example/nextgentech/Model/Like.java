package com.example.nextgentech.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Table(name = "likes", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)

    private User user;
    @Transient // Không lưu thuộc tính này vào database
    private boolean isLikedByCurrentUser;

    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        isLikedByCurrentUser = likedByCurrentUser;
    }

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    public enum Type {
        LIKE, DISLIKE
    }
    public Type getType() {
        return this.type;
    }

    // Nếu Lombok không hoạt động, tự thêm setter
    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Post getPost() {
        return this.post;
    }
    public void setType(Type type) {
        this.type = type;
    }

}
