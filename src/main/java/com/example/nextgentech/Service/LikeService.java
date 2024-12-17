package com.example.nextgentech.Service;

import com.example.nextgentech.Model.Like;
import com.example.nextgentech.Model.Post;
import com.example.nextgentech.Model.User;
import com.example.nextgentech.Repository.LikeRepository;
import jakarta.persistence.Transient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    public boolean isPostLikedByUser(Post post, User user) {
        Like like = likeRepository.findByPostAndUser(post, user);
        return like != null && like.getType() == Like.Type.LIKE; // True nếu đã like
    }

    public boolean toggleLike(Post post, User user) {
        // Kiểm tra xem người dùng đã like bài viết chưa
        Optional<Like> existingLike = Optional.ofNullable(likeRepository.findByPostAndUser(post, user));
        if (existingLike.isPresent()) {
            // Nếu đã like, xóa like
            likeRepository.delete(existingLike.get());
            return false; // Đã bỏ like
        } else {
            // Nếu chưa like, thêm like
            Like newLike = new Like();
            newLike.setPost(post);
            newLike.setUser(user);
            newLike.setType(Like.Type.LIKE);
            likeRepository.save(newLike);
            return true; // Đã like
        }
    }

}
