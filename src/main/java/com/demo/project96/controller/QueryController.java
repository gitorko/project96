package com.demo.project96.controller;

import java.util.Optional;

import com.demo.project96.domain.Comment;
import com.demo.project96.domain.Post;
import com.demo.project96.domain.PostPage;
import com.demo.project96.repo.CommentRepository;
import com.demo.project96.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class QueryController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @QueryMapping
    public Iterable<Post> findAllPosts() {
        return postRepository.findAll();
    }

    @QueryMapping
    public PostPage findAllPostsPage(@Argument Integer page, @Argument Integer size) {
        PageRequest pageOf = PageRequest.of(page, size);
        Page<Post> all = postRepository.findAll(pageOf);
        return PostPage.builder()
                .posts(all.getContent())
                .totalElements(all.getTotalElements())
                .totalPages(all.getTotalPages())
                .currentPage(all.getNumber())
                .size(all.getNumberOfElements())
                .build();
    }

    @QueryMapping
    public Optional<Post> findPostById(@Argument("id") Long id) {
        return postRepository.findById(id);
    }

    @QueryMapping
    public Iterable<Comment> findAllComments() {
        //Will cause N+1 problem
        //return commentRepository.findAll();
        return commentRepository.findAllComments();
    }

    @QueryMapping
    public Optional<Comment> findCommentById(@Argument("id") Long id) {
        return commentRepository.findById(id);
    }

    @QueryMapping
    public long countPosts() {
        return postRepository.count();
    }

    @QueryMapping
    public Iterable<Comment> findCommentsByPostId(@Argument("postId") Long postId) {
        Optional<Post> byId = postRepository.findById(postId);
        if (byId.isPresent()) {
            return commentRepository.findByPost(byId.get());
        } else {
            throw new RuntimeException("Post not found!");
        }
    }

    /**
     * Functionality will work same without this method as well.
     * Hibernate Lazy fetch prevents the post entity from being fetched even without this method.
     * So no unnecessary db call is made if post entity is not needed in the response even without this method.
     * However if there is any reason why we want to control a single field explicitly we can use this approach and define how that field gets data.
     * eg: You want to sort the comments
     */
    @SchemaMapping(typeName = "Comment", field = "post")
    public Post getPost(Comment comment) {
        return postRepository.findById(comment.getPost().getId()).orElseThrow(null);
    }

}
