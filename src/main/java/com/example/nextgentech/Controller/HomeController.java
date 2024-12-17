package com.example.nextgentech.Controller;

import com.example.nextgentech.Model.CategoryPost;
import com.example.nextgentech.Model.Post;
import com.example.nextgentech.Model.SearchLog;
import com.example.nextgentech.Model.User;
import com.example.nextgentech.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SearchLogService searchLogService;

    @GetMapping("/")
    public String getHome(@RequestParam(defaultValue = "0") int page, // Tham số page
                          @AuthenticationPrincipal OidcUser oidcUser,
                          Model model, HttpSession session) {
        // Xử lý tên người dùng hoặc gán tên "Guest" nếu chưa đăng nhập
        String fullName = (oidcUser != null) ? oidcUser.getAttribute("name") : "Guest";
        model.addAttribute("name", fullName != null ? fullName : "Guest");

        // Lấy danh sách tất cả danh mục
        List<CategoryPost> categoryPostList = categoryService.findAll();

        // Lấy danh sách ID đã xem từ session
        List<Long> excludedIds = (List<Long>) session.getAttribute("excludedPostIds");
        if (excludedIds == null) {
            excludedIds = new ArrayList<>();
        }

        // Lấy bài viết ngẫu nhiên (ví dụ: 5 bài viết)
        List<Post> randomPosts = postService.getRandomPostsExcluding(excludedIds, 5);

        List<Long> newExcludedIds = new ArrayList<>(excludedIds);
        randomPosts.forEach(post -> newExcludedIds.add(post.getId()));
        session.setAttribute("excludedPostIds", newExcludedIds);

        List<String> recentQueries = new ArrayList<>();
        List<Post> sortedPosts;

        // Lấy thông tin người dùng từ OAuth2
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            Optional<User> optionalUser = userService.findUserByEmail(email);
            if (optionalUser.isPresent()) {
                User currentUser = optionalUser.get();

                // Cập nhật trạng thái like cho mỗi bài viết
                for (Post post : postService.findAll()) {
                    boolean isLiked = likeService.isPostLikedByUser(post, currentUser);
                    post.setLikedByCurrentUser(isLiked);
                }

                recentQueries = searchLogService.getRecentSearchQueries(currentUser.getId());
                List<Post> recommendedPosts = postService.getRecommendedPosts(recentQueries);

                // Giới hạn danh sách bài viết gợi ý còn 5 bài
                int maxRecommended = 5;
                if (recommendedPosts.size() > maxRecommended) {
                    recommendedPosts = recommendedPosts.subList(0, maxRecommended);
                }
                model.addAttribute("recommendedPosts", recommendedPosts);
            }
        }

        // Lấy và sắp xếp bài viết
        sortedPosts = postService.getSortedPostsByKeywords(recentQueries);

        // Phân trang
        int pageSize = 10; // Số bài viết mỗi trang
        int start = page * pageSize;
        int end = Math.min(start + pageSize, sortedPosts.size());

        List<Post> pagedPosts = sortedPosts.subList(start, end);
        int totalPages = (int) Math.ceil((double) sortedPosts.size() / pageSize);

        // Thêm dữ liệu phân trang vào model
        model.addAttribute("post", pagedPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // Thêm danh sách danh mục và bài viết ngẫu nhiên vào model
        model.addAttribute("randomPosts", randomPosts);
        model.addAttribute("categoryPostList", categoryPostList);

        return "layouts/user_layout";
    }


    @GetMapping("/search")
    public String searchPosts(@RequestParam("query") String query,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        // Lấy thông tin người dùng từ Google OAuth2
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            User user = userService.findUserByEmail(email).orElse(null);

            if (user != null) {
                // Lưu lịch sử tìm kiếm
                searchLogService.saveSearchLog(user, query);

                // Lấy lịch sử tìm kiếm gần đây
                List<SearchLog> recentSearches = searchLogService.getRecentSearches(user);
                model.addAttribute("recentSearches", recentSearches);
            }
        }

        // Kiểm tra query không rỗng
        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("error", "Từ khóa tìm kiếm không hợp lệ.");
            return "layouts/user_layout";
        }

        // Thực hiện tìm kiếm với phân trang
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> searchResults = postService.searchPosts(query, pageable);

        model.addAttribute("searchResults", searchResults.getContent()); // Dữ liệu bài viết
        model.addAttribute("currentPage", page); // Trang hiện tại
        model.addAttribute("totalPages", searchResults.getTotalPages()); // Tổng số trang
        model.addAttribute("query", query);

        return "layouts/user_layout";
    }


    @GetMapping("/login")
    public String getLoginForm(Model model) {
        return "user/login";
    }
    @GetMapping("/my-account")
    public String getMyAccount(Model model) {
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

                model.addAttribute("user", currentUser);
                // Gắn danh sách bài viết vào model
                model.addAttribute("posts", userPosts);
            } else {
                // Xử lý trường hợp không tìm thấy người dùng
                throw new RuntimeException("Người dùng không tồn tại.");
            }
        } else {
            throw new RuntimeException("Người dùng chưa được xác thực.");
        }
        model.addAttribute("categories", categoryService.findAll()); // Phương thức này trả về danh sách các danh mục


        return "user/my-account";
    }
}
