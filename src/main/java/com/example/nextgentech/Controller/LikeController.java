package com.example.nextgentech.Controller;

import com.example.nextgentech.Model.Like;
import com.example.nextgentech.Model.Post;
import com.example.nextgentech.Model.User;
import com.example.nextgentech.Service.LikeService;
import com.example.nextgentech.Service.PostService;
import com.example.nextgentech.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * API để xử lý yêu cầu like/unlike một bài viết.
     */
    @PostMapping("/{postId}/like")
    @ResponseBody
    public ResponseEntity<String> toggleLike(@PathVariable Long postId) {

        // Lấy thông tin người dùng hiện tại từ Google
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(403).body("Không thể xác thực người dùng hiện tại.");
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // Tìm người dùng qua email
        Optional<User> currentUserOptional = userService.findUserByEmail(email);
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(403).body("Người dùng không tồn tại trong hệ thống.");
        }
        User currentUser = currentUserOptional.get();

        // Lấy bài viết từ ID
        Optional<Post> postOptional = postService.findById(postId);
        if (postOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy bài viết.");
        }
        Post post = postOptional.get();

        // Thực hiện toggle like
        boolean isLiked = likeService.toggleLike(post, currentUser);

        return ResponseEntity.ok(isLiked ? "Đã thích bài viết." : "Đã bỏ thích bài viết.");
    }

    /**
     * Hiển thị danh sách bài viết kèm trạng thái like của user.
     */
}
