package com.example.nextgentech.Repository;

import com.example.nextgentech.Model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
