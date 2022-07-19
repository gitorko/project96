package com.demo.project96.controller;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.demo.project96.domain.Comment;
import com.demo.project96.domain.Post;
import com.demo.project96.repo.CommentRepository;
import com.demo.project96.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MutationController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @MutationMapping
    public Post createPost(@Argument("header") String header, @Argument("createdBy") String createdBy) {
        Post post = new Post();
        post.setHeader(header);
        post.setCreatedBy(createdBy);
        post.setCreatedDt(ZonedDateTime.now());
        post = postRepository.save(post);
        return post;
    }

    @MutationMapping
    public Comment createComment(@Argument("message") String message, @Argument("createdBy") String createdBy, @Argument("postId") Long postId) {
        Comment comment = new Comment();
        Optional<Post> byId = postRepository.findById(postId);
        if (byId.isPresent()) {
            Post post = byId.get();
            comment.setPost(post);
            comment.setMessage(message);
            comment.setCreatedBy(createdBy);
            comment.setCreatedDt(ZonedDateTime.now());
            comment = commentRepository.save(comment);
            return comment;
        } else {
            throw new RuntimeException("Post not found!");
        }

    }

    @MutationMapping
    public boolean deleteComment(@Argument("id") Long id) {
        commentRepository.deleteById(id);
        return true;
    }

    @MutationMapping
    public Comment updateComment(@Argument("id") Long id, @Argument("message") String message) {
        Optional<Comment> byId = commentRepository.findById(id);
        if (byId.isPresent()) {
            Comment comment = byId.get();
            comment.setMessage(message);
            commentRepository.save(comment);
            return comment;
        }
        throw new RuntimeException("Post not found!");
    }
}
