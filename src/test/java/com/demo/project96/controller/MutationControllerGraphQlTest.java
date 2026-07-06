package com.demo.project96.controller;

import java.time.ZonedDateTime;

import com.demo.project96.domain.Comment;
import com.demo.project96.domain.Post;
import com.demo.project96.repo.CommentRepository;
import com.demo.project96.repo.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MutationControllerGraphQlTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private HttpGraphQlTester graphQlTester;

    private Post post;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();

        WebTestClient client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port + "/api/graphql")
                .build();
        graphQlTester = HttpGraphQlTester.create(client);

        post = postRepository.save(Post.builder()
                .header("header_1")
                .createdBy("Jack")
                .createdDt(ZonedDateTime.now())
                .build());
    }

    @AfterEach
    void cleanUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void createPostPersistsNewPost() {
        String postId = graphQlTester.document(
                        "mutation { createPost(header: \"Hello world\", createdBy: \"John\") { id header createdBy } }")
                .execute()
                .path("createPost.header").entity(String.class).isEqualTo("Hello world")
                .path("createPost.createdBy").entity(String.class).isEqualTo("John")
                .path("createPost.id").entity(String.class).get();

        assertThat(postRepository.findById(Long.valueOf(postId))).isPresent();
    }

    @Test
    void createCommentLinksToExistingPost() {
        String commentId = graphQlTester.document(
                        "mutation { createComment(message: \"comment1\", createdBy: \"John\", postId: " + post.getId() + ") { id message post { id } } }")
                .execute()
                .path("createComment.message").entity(String.class).isEqualTo("comment1")
                .path("createComment.post.id").entity(String.class).isEqualTo(String.valueOf(post.getId()))
                .path("createComment.id").entity(String.class).get();

        assertThat(commentRepository.findById(Long.valueOf(commentId))).isPresent();
    }

    @Test
    void createCommentErrorsWhenPostMissing() {
        graphQlTester.document(
                        "mutation { createComment(message: \"comment1\", createdBy: \"John\", postId: 999999) { id } }")
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors).isNotEmpty());
    }

    @Test
    void updateCommentChangesMessage() {
        Comment comment = commentRepository.save(Comment.builder()
                .message("original")
                .createdBy("Jack")
                .createdDt(ZonedDateTime.now())
                .post(post)
                .build());

        graphQlTester.document("mutation { updateComment(id: " + comment.getId() + ", message: \"updated\") { id message } }")
                .execute()
                .path("updateComment.message").entity(String.class).isEqualTo("updated");

        assertThat(commentRepository.findById(comment.getId()).get().getMessage()).isEqualTo("updated");
    }

    @Test
    void updateCommentErrorsWhenMissing() {
        graphQlTester.document("mutation { updateComment(id: 999999, message: \"updated\") { id } }")
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors).isNotEmpty());
    }

    @Test
    void deleteCommentRemovesIt() {
        Comment comment = commentRepository.save(Comment.builder()
                .message("to-delete")
                .createdBy("Jack")
                .createdDt(ZonedDateTime.now())
                .post(post)
                .build());

        graphQlTester.document("mutation { deleteComment(id: " + comment.getId() + ") }")
                .execute()
                .path("deleteComment").entity(Boolean.class).isEqualTo(true);

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }
}
