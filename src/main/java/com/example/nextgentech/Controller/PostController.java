package com.example.nextgentech.Controller;

import com.example.nextgentech.Model.CategoryPost;
import com.example.nextgentech.Model.Comment;
import com.example.nextgentech.Model.Post;
import com.example.nextgentech.Model.User;
import com.example.nextgentech.Service.CategoryService;
import com.example.nextgentech.Service.CommentService;
import com.example.nextgentech.Service.PostService;
import com.example.nextgentech.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @GetMapping("")
    public String getAllPost(Model model) {
        List<Post> post = postService.findAll();
        model.addAttribute("post", post);
        return "user/my-account";
    }
    @GetMapping("/create")
    public String showCreatePostForm(Model model) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email"); // Lấy email người dùng
            String name = oAuth2User.getAttribute("name");   // Lấy tên người dùng (nếu cần)

            // Truyền email hoặc tên người dùng đến giao diện
            model.addAttribute("currentUserEmail", email);
            model.addAttribute("currentUserName", name);
        }

        model.addAttribute("categories", categoryService.findAll()); // Phương thức này trả về danh sách các danh mục

        model.addAttribute("post", new Post()); // Truyền một đối tượng Post rỗng để Thymeleaf sử dụng
        return "user/posts/index"; // Tên tệp HTML chứa form tạo bài viết
    }


    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post, @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) throws IOException {
        // Lấy thông tin người dùng hiện tại từ Google
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            // Tìm người dùng qua email
            Optional<User> currentUserOptional = userService.findUserByEmail(email);
            if (currentUserOptional.isPresent()) {
                post.setAuthor(currentUserOptional.get());
            } else {
                throw new RuntimeException("Người dùng không tồn tại trong hệ thống.");
            }
        } else {
            throw new RuntimeException("Không thể xác thực người dùng hiện tại.");
        }

        String imageName = "default.jpg";
        if (!imageFile.isEmpty()) {
            imageName = imageFile.getOriginalFilename();
            post.setPostImage(imageName);

            // Thư mục bên ngoài `resources`
            File uploadsDir = new File("uploads/posts_img");

            // Tạo thư mục nếu chưa tồn tại
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs();
            }

            // Đường dẫn lưu file
            Path path = Paths.get(uploadsDir.getAbsolutePath() + File.separator + imageFile.getOriginalFilename());

            // Lưu file
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        // Lưu category vào database
        postService.save(post);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm bài viết thành công!");

        return "redirect:/my-account"; // Chuyển hướng về danh sách bài viết
    }
    @GetMapping("/my-posts")
    public String listUserPosts(Model model) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            // Tìm người dùng
            Optional<User> optionalUser = userService.findUserByEmail(email);

            if (optionalUser.isPresent()) {
                User currentUser = optionalUser.get(); // Lấy đối tượng User từ Optional

                // Lấy danh sách bài viết của người dùng
                List<Post> userPosts = postService.findPostByAuthor(currentUser);

                // Gắn danh sách bài viết vào model
                model.addAttribute("posts", userPosts);
            } else {
                // Xử lý trường hợp không tìm thấy người dùng
                throw new RuntimeException("Người dùng không tồn tại.");
            }
        } else {
            throw new RuntimeException("Người dùng chưa được xác thực.");
        }

        return "user/posts/my-posts"; // Tên view để hiển thị danh sách bài viết
    }

    @GetMapping("/edit/{id}")
    public String editPostPage(@PathVariable("id") Long id, Model model) {
        Optional<Post> optionalPost = postService.findById(id);

        List<CategoryPost> categoryPostList = categoryService.findAll();
        model.addAttribute("posts", categoryPostList);
        if (optionalPost.isPresent()) {
            model.addAttribute("post", optionalPost.get());
            return "user/posts/edit"; // Trả về template edit-post.html
        } else {
            model.addAttribute("errorMessage", "Bài viết không tồn tại.");
            return "error"; // Trả về trang lỗi nếu không tìm thấy bài viết
        }
    }

    // Cập nhật bài viết
    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable("id") Long id,
                             @ModelAttribute Post post,
                             @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) throws IOException {
        // Tìm bài viết theo ID
        Optional<Post> optionalPost = postService.findById(id);

        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            // Cập nhật thông tin bài viết
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());
            existingPost.setCategory(post.getCategory());

            // Xử lý cập nhật hình ảnh
            if (!imageFile.isEmpty()) {
                String imageName = imageFile.getOriginalFilename();
                existingPost.setPostImage(imageName);

                // Thư mục lưu hình ảnh
                File uploadsDir = new File("uploads/posts_img");

                // Tạo thư mục nếu chưa tồn tại
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdirs();
                }

                // Đường dẫn lưu file
                Path path = Paths.get(uploadsDir.getAbsolutePath() + File.separator + imageFile.getOriginalFilename());

                // Lưu file
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            // Lưu bài viết đã chỉnh sửa vào cơ sở dữ liệu
            postService.save(existingPost);
        } else {
            throw new RuntimeException("Không tìm thấy bài viết với ID: " + id);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Sửa bài viết thành công!");

        return "redirect:/my-account"; // Chuyển hướng về danh sách bài viết của người dùng
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Post> post = postService.findById(id);
        if (post.isPresent()) {
            postService.delete(post.get());
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bài viết thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bài viết cần xóa!");
        }
        return "redirect:/my-account"; // Điều hướng về trang danh sách bài viết
    }

    @GetMapping("/{slug}")
    public String showPostDetail(@PathVariable String slug, Model model) {
        Optional<Post> optionalPost = postService.findPostBySlug(slug);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Tăng lượt xem
            post.setViews(post.getViews() + 1);
            postService.save(post); // Lưu lại bài viết với lượt xem mới

            // Thêm bài viết hiện tại vào model
            model.addAttribute("post", post);

            // Lấy danh sách bình luận
            List<Comment> comments = commentService.getCommentsByPost(post.getId());
            model.addAttribute("comments", comments);

            // Gợi ý bài viết liên quan
            List<Post> relatedPosts = postService.getRelatedPosts(post.getId(), post.getTags());
            model.addAttribute("relatedPosts", relatedPosts);

            // Gợi ý bài viết phổ biến
            List<Post> popularPosts = postService.getPopularPosts();
            model.addAttribute("popularPosts", popularPosts);

            // Kết hợp bài viết liên quan và phổ biến
            List<Post> combinedRecommendations = postService.getCombinedRecommendations(post.getId(), post.getTags());
            model.addAttribute("combinedRecommendations", combinedRecommendations);

        } else {
            return "redirect:/404"; // Nếu không tìm thấy bài viết, chuyển hướng đến trang 404
        }

        return "user/posts/detail"; // Trả về trang chi tiết bài viết
    }



}
