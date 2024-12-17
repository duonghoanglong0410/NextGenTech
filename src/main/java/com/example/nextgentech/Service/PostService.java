package com.example.nextgentech.Service;

import com.example.nextgentech.Model.Post;
import com.example.nextgentech.Model.User;
import com.example.nextgentech.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id){
        return postRepository.findById(id);
    }

    public Post findBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    public Optional<Post> findPostBySlug(String slug) {
        return postRepository.findPostBySlug(slug);
    }

    public List<Post> getLatestPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAll(pageable).getContent();
    }


    public List<Post> findPostByAuthor(User author) {
        return postRepository.findByAuthor(author);
    }

    public String generateUniqueSlug(String title) {
        String baseSlug = SlugUtils.toSlug(title); // Tạo slug cơ bản từ tiêu đề
        String categorySlug = baseSlug;
        int count = 1;

        // Kiểm tra xem slug đã tồn tại trong cơ sở dữ liệu chưa
        while (postRepository.existsBySlug(categorySlug)) {
            categorySlug = baseSlug + "-" + count; // Thêm số đằng sau
            count++;
        }

        return categorySlug;
    }

    public Post save(Post post) {
        // Tạo slug duy nhất
        String uniqueSlug = generateUniqueSlug(post.getTitle());
        post.setSlug(uniqueSlug);
        return postRepository.save(post);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }


    public void delete(Post post) {
        postRepository.delete(post);
    }


    public List<Post> getRelatedPosts(Long postId, String tag) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 4);
        return postRepository.findTop4RelatedPostsByTag(postId, tag, pageable);
    }

    public List<Post> getPopularPosts() {
        return postRepository.findTop4ByOrderByViewsDesc();
    }

    public List<Post> getCombinedRecommendations(Long postId, String tag) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 4);
        return postRepository.findTop4CombinedRecommendations(postId, tag, pageable);
    }


    // Tìm kiếm
    public Page<Post> searchPosts(String query, Pageable pageable) {
        return postRepository.findByTitleContainingOrTagsContaining(query, query, pageable);
    }

    public List<Post> getRecommendedPosts(List<String> keywords) {
        List<Post> recommendedPosts = new ArrayList<>();
        for (String keyword : keywords) {
            recommendedPosts.addAll(postRepository.findPostsByKeyword(keyword));
        }
        // Loại bỏ các bài viết trùng lặp và giới hạn số lượng đề xuất
        return recommendedPosts.stream().distinct().limit(10).collect(Collectors.toList());
    }

    // Lấy ngẫu nhiên danh sách bài viết
    public List<Post> getRandomPosts(int limit) {
        return postRepository.findRandomPosts(limit);
    }

    // Lấy bài viết ngẫu nhiên và loại trừ các ID đã xem
    public List<Post> getRandomPostsExcluding(List<Long> excludedIds, int limit) {
        Pageable pageable = PageRequest.of(0, limit); // Giới hạn số lượng bài viết
        return postRepository.findRandomPostsExcluding(excludedIds, pageable);
    }

    public List<Post> getSortedPostsByKeywords(List<String> keywords) {
        List<Post> relatedPosts = new ArrayList<>();
        Set<Long> relatedPostIds = new HashSet<>();

        if (keywords != null && !keywords.isEmpty()) {
            for (String keyword : keywords) {
                List<Post> foundPosts = postRepository.findPostsByKeyword(keyword);
                for (Post post : foundPosts) {
                    if (!relatedPostIds.contains(post.getId())) {
                        relatedPosts.add(post);
                        relatedPostIds.add(post.getId());
                    }
                }
            }
        }

        // Lấy tất cả bài viết
        List<Post> allPosts = postRepository.findAll();

        // Lọc bỏ bài viết đã nằm trong danh sách liên quan
        List<Post> otherPosts = allPosts.stream()
                .filter(post -> !relatedPostIds.contains(post.getId()))
                .collect(Collectors.toList());

        // Gộp danh sách liên quan và còn lại
        relatedPosts.addAll(otherPosts);

        // Xáo trộn danh sách bài viết
        Collections.shuffle(relatedPosts);

        return relatedPosts;
    }


}
