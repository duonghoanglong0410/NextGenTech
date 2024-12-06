package com.example.nextgentech.Repository;

import com.example.nextgentech.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
