package com.example.nextgentech.Repository;

import com.example.nextgentech.Model.SearchLog;
import com.example.nextgentech.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
    List<SearchLog> findTop5ByUserOrderByCreatedAtDesc(User user);

    // Lấy từ khóa tìm kiếm mới nhất của người dùng (giới hạn 5)
    @Query("SELECT s.searchQuery FROM SearchLog s WHERE s.user.id = :userId GROUP BY s.searchQuery ORDER BY MAX(s.createdAt) DESC")
    List<String> findTop5SearchQueriesByUserId(@Param("userId") Long userId);



}
