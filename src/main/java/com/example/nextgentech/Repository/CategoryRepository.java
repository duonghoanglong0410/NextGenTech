package com.example.nextgentech.Repository;

import com.example.nextgentech.Model.CategoryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryPost, Long> {
    CategoryPost findBySlug(String slug);

    boolean existsBySlug(String slug);
}
