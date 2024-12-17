package com.example.nextgentech.Repository;

import com.example.nextgentech.Model.Comment;
import com.example.nextgentech.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(Long postId);
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);


    List<Comment> findByPostIdAndParentCommentIsNull(Long postId);

    Long countByPost(Post post);

}

