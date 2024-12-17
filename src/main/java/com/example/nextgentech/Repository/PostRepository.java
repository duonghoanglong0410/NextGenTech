package com.example.nextgentech.Repository;

import com.example.nextgentech.Model.Post;
import com.example.nextgentech.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Post findBySlug(String slug);

    boolean existsBySlug(String categorySlug);

    List<Post> findByAuthor(User author);

    Optional<Post> findPostBySlug(String slug);

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findTop3ByOrderByCreatedDateDesc(Pageable pageable);

    // Lấy 4 bài viết liên quan dựa trên tags
    @Query("SELECT p FROM Post p WHERE p.id <> :postId AND p.tags LIKE %:tag%")
    List<Post> findTop4RelatedPostsByTag(@Param("postId") Long postId, @Param("tag") String tag, Pageable pageable);

    // Lấy 4 bài viết phổ biến nhất dựa trên lượt xem
    List<Post> findTop4ByOrderByViewsDesc();

    // Kết hợp bài viết liên quan và phổ biến (giới hạn 4 kết quả)
    @Query("SELECT p FROM Post p WHERE p.id <> :postId AND p.tags LIKE %:tag% ORDER BY p.views DESC")
    List<Post> findTop4CombinedRecommendations(@Param("postId") Long postId, @Param("tag") String tag, Pageable pageable);

    // Tìm kiếm
    Page<Post> findByTitleContainingOrTagsContaining(String title, String tags, Pageable pageable);

    // Tìm các bài viết có chứa từ khóa trong title hoặc tags
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.tags LIKE %:keyword%")
    List<Post> findPostsByKeyword(@Param("keyword") String keyword);

    // Lấy ngẫu nhiên một số bài viết, giới hạn số lượng (ví dụ: 5 bài viết)
    @Query(value = "SELECT * FROM posts ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Post> findRandomPosts(@Param("limit") int limit);

    // Lấy bài viết ngẫu nhiên, loại bỏ các bài viết đã hiển thị
    @Query("SELECT p FROM Post p WHERE p.id NOT IN :excludedIds ORDER BY FUNCTION('RAND')")
    List<Post> findRandomPostsExcluding(@Param("excludedIds") List<Long> excludedIds, Pageable pageable);


    List<Post> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

}
