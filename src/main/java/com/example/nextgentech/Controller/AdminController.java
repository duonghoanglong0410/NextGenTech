package com.example.nextgentech.Controller;

import com.example.nextgentech.Model.CategoryPost;
import com.example.nextgentech.Model.Post;
import com.example.nextgentech.Model.SearchLog;
import com.example.nextgentech.Model.User;
import com.example.nextgentech.Repository.CommentRepository;
import com.example.nextgentech.Repository.LikeRepository;
import com.example.nextgentech.Repository.PostRepository;
import com.example.nextgentech.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private SearchLogService searchLogService;
    @GetMapping("")
    public String homepage(Authentication authentication) {
        return "layouts/admin_layout";  // Trả về trang index.html
    }


    // Danh mục bài viết
    @GetMapping("/category")
    public String categoryPost(Model model){
        List<CategoryPost> categoryPostList = categoryService.findAll();
        model.addAttribute("categoryPostList", categoryPostList);
        model.addAttribute("content", "admin/category/list"); // Thêm dòng này
        return "layouts/admin_layout"; // Trả về layout với nội dung
    }
    // Xử lý thêm danh mục từ modal
    @PostMapping("/category/add")
    public String addCategory(@RequestParam("name") String name,
                              @RequestParam("description") String description, RedirectAttributes redirectAttributes) {
        CategoryPost category = new CategoryPost();
        category.setName(name);
        category.setDescription(description);

        // Lưu danh mục sử dụng service
        categoryService.save(category);
        // Thêm thông báo thành công vào Flash Attributes
        redirectAttributes.addFlashAttribute("successMessage", "Thêm danh mục thành công!");

        return "redirect:/admin/category"; // Chuyển hướng về danh sách danh mục
    }

    @GetMapping("/category/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        CategoryPost category = categoryService.findById(id).orElse(new CategoryPost()); // Khởi tạo đối tượng nếu không tìm thấy
        model.addAttribute("category", category);
        model.addAttribute("content", "admin/category/edit"); // Thêm dòng này
        return "layouts/admin_layout"; // Trả về layout với nội dung
    }

    @PostMapping("/category/edit/{id}")
    public String editCategory(@PathVariable("id") Long id, @ModelAttribute("category") CategoryPost updatedCategory, RedirectAttributes redirectAttributes) {
        CategoryPost category = categoryService.findById(id).orElse(null);
        if (category != null) {
            category.setName(updatedCategory.getName());
            category.setDescription(updatedCategory.getDescription());
            category.setId(id);
            categoryService.save(category);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
        return "redirect:/admin/category"; // Chuyển hướng về danh sách danh mục sau khi cập nhật
    }
    @GetMapping("/category/del/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Tìm danh mục cần xóa
        CategoryPost categoryPost = categoryService.findById(id).orElse(null);
        // Nếu danh mục tồn tại, thực hiện xóa
        if (categoryPost != null) {
            categoryService.deleteById(id);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
        // Chuyển hướng về trang danh sách danh mục sau khi xóa thành công
        return "redirect:/admin/category";
    }



    // Danh sách bài viết
    @GetMapping("/post")
    public String post(Model model){
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("content", "admin/posts/list"); // Thêm dòng này
        return "layouts/admin_layout"; // Trả về layout với nội dung
    }
    @GetMapping("/post/view/{id}")
    public String postView(@PathVariable("id") Long id, Model model) {
        Post post = postService.findById(id).orElse(new Post()); // Khởi tạo đối tượng nếu không tìm thấy
        model.addAttribute("post", post);
        model.addAttribute("content", "admin/posts/view"); // Thêm dòng này
        return "layouts/admin_layout"; // Trả về layout với nội dung
    }
    @GetMapping("/post/del/{id}")
    public String delPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa bài viết thành công!");
        return "redirect:/admin/post";
    }



    // User
    @GetMapping("/user")
    public String user(Model model) {
        List<User> users = userService.findAll();
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("No users found!");
        }
        model.addAttribute("users", users);
        model.addAttribute("content", "admin/users/list"); // Thêm dòng này
        return "layouts/admin_layout";
    }
    @GetMapping("/user/view/{id}")
    public String userView(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id).orElse(new User()); // Khởi tạo đối tượng nếu không tìm thấy
        model.addAttribute("user", user);
        model.addAttribute("content", "admin/users/view"); // Thêm dòng này
        return "layouts/admin_layout"; // Trả về layout với nội dung
    }
    @PostMapping("/user/{id}/update-role")
    public String updateUserRole(@PathVariable Long id, @RequestParam("role") String role , RedirectAttributes redirectAttributes)  {
        User user = userService.findById(id).orElse(null);
        if (user != null) {
            user.setRole(role);
            userService.save(user);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật role thành công!");
        return "redirect:/admin/user";
    }
    @GetMapping("/user/del/{id}")
    public String delUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        return "redirect:/admin/user";
    }



    // Thống kê
    @GetMapping("/thongke")
    public String hienThiThongKe(Model model) {
        // Lấy danh sách bài viết và sắp xếp giảm dần theo lượt xem
        List<Post> top10BaiViet = postRepository.findAll().stream()
                .sorted((p1, p2) -> Long.compare(p2.getViews(), p1.getViews())) // Sắp xếp giảm dần theo lượt xem
                .limit(10) // Giới hạn chỉ lấy 10 bài viết
                .collect(Collectors.toList());

        // Tạo danh sách dữ liệu cho biểu đồ
        List<String> tieuDeBaiViet = top10BaiViet.stream()
                .map(Post::getTitle)
                .collect(Collectors.toList());

        List<Long> luotXem = top10BaiViet.stream()
                .map(post -> post.getViews().longValue())
                .collect(Collectors.toList());

        List<Long> soLuotLike = top10BaiViet.stream()
                .map(post -> likeRepository.countByPost(post))
                .collect(Collectors.toList());

        List<Long> soLuotComment = top10BaiViet.stream()
                .map(post -> commentRepository.countByPost(post))
                .collect(Collectors.toList());

        // Tính tổng lượt xem theo danh mục (nếu cần)
        Map<String, Long> luotXemTheoDanhMuc = top10BaiViet.stream()
                .collect(Collectors.groupingBy(
                        post -> post.getCategory().getName(), // Lấy tên danh mục
                        Collectors.summingLong(Post::getViews) // Tính tổng lượt xem
                ));

        // Đưa dữ liệu vào model để hiển thị trên giao diện
        model.addAttribute("tieuDeBaiViet", tieuDeBaiViet);
        model.addAttribute("luotXem", luotXem);
        model.addAttribute("soLuotLike", soLuotLike);
        model.addAttribute("soLuotComment", soLuotComment);
        model.addAttribute("luotXemTheoDanhMuc", luotXemTheoDanhMuc);

        model.addAttribute("content", "admin/thongke/thongke"); // Đường dẫn layout
        return "layouts/admin_layout"; // Trả về layout với nội dung
    }

}

