package com.demo.project96.repo;

import com.demo.project96.domain.Comment;
import com.demo.project96.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Iterable<Comment> findByPost(Post post);
}
