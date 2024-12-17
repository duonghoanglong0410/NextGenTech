package com.example.nextgentech.Repository;

import com.example.nextgentech.Model.Like;
import com.example.nextgentech.Model.Post;
import com.example.nextgentech.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository  extends JpaRepository<Like, Long> {

    Like findByPostAndUser(Post post, User user);

    Long countByPost(Post post);


}

