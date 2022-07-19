package com.demo.project96.repo;

import java.util.List;

import com.demo.project96.domain.Comment;
import com.demo.project96.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Iterable<Comment> findByPost(Post post);

    //To avoid N+1 problem
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.post")
    List<Comment> findAllComments();
}
