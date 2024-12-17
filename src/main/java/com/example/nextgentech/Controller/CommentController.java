package com.example.nextgentech.Controller;

import com.example.nextgentech.Model.Comment;
import com.example.nextgentech.Model.Post;
import com.example.nextgentech.Model.User;
import com.example.nextgentech.Service.CommentService;
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
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * Thêm bình luận vào bài viết.
     */
    @PostMapping("/comment")
    public String addComment(@RequestParam Long postId,
                             @RequestParam String content,
                             @RequestParam(required = false) Long parentCommentId,
                             Model model) {
        // Lấy thông tin người dùng hiện tại từ Google
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            model.addAttribute("error", "Bạn cần đăng nhập để bình luận.");
            return "error-page"; // Trang lỗi nếu cần
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // Tìm người dùng qua email
        Optional<User> currentUserOptional = userService.findUserByEmail(email);
        if (currentUserOptional.isEmpty()) {
            model.addAttribute("error", "Người dùng không tồn tại trong hệ thống.");
            return "error-page";
        }
        User currentUser = currentUserOptional.get();

        // Lấy bài viết từ ID
        Optional<Post> postOptional = postService.findById(postId);
        if (postOptional.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy bài viết.");
            return "error-page";
        }
        Post post = postOptional.get();

        // Tạo bình luận mới
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setUser(currentUser);

        // Nếu là trả lời cho bình luận cha
        if (parentCommentId != null) {
            Optional<Comment> parentCommentOptional = commentService.getCommentById(parentCommentId);
            if (parentCommentOptional.isEmpty()) {
                model.addAttribute("error", "Không tìm thấy bình luận cha.");
                return "error-page";
            }
            comment.setParentComment(parentCommentOptional.get());
        }

        // Lưu bình luận
        commentService.saveComment(comment);

        System.out.println("Content: " + content);
        System.out.println("ParentCommentId: " + parentCommentId);
        System.out.println("PostId: " + postId);


        // Chuyển hướng lại trang bài viết
        return "layouts/user_layout"; // Trả về tên của view (ví dụ, welcome.html)
    }

    /**
     * Hiển thị chi tiết bài viết cùng danh sách bình luận.
     */
    @GetMapping("/{postId}")
    public String showPostDetail(@PathVariable Long postId, Model model) {
        // Lấy bài viết
        Optional<Post> postOptional = postService.findById(postId);
        if (postOptional.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy bài viết.");
            return "error-page";
        }
        Post post = postOptional.get();

        // Lấy danh sách bình luận
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.getCommentsByPost(postId));
        return "post-detail"; // Tên file HTML hiển thị chi tiết bài viết
    }
}
