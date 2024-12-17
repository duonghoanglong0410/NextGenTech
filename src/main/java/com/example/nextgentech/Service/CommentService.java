package com.example.nextgentech.Service;


import com.example.nextgentech.Model.Comment;
import com.example.nextgentech.Repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(postId);
    }

    public List<Comment> getRepliesByComment(Long commentId) {
        return commentRepository.findByParentCommentIdOrderByCreatedAtAsc(commentId);
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsWithReplies(Long postId) {
        // Lấy tất cả bình luận cha (parentCommentId = null)
        List<Comment> parentComments = commentRepository.findByPostIdAndParentCommentIsNull(postId);

        // Thêm các replies vào từng parent comment
        for (Comment parent : parentComments) {
            List<Comment> replies = commentRepository.findRepliesByParentId(parent.getId());
            parent.setReplies(replies);
        }

        return parentComments;
    }

}
