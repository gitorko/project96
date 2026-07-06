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
class QueryControllerGraphQlTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private HttpGraphQlTester graphQlTester;

    private Post post1;
    private Post post2;
    private Comment comment1;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();

        WebTestClient client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port + "/api/graphql")
                .build();
        graphQlTester = HttpGraphQlTester.create(client);

        post1 = postRepository.save(Post.builder()
                .header("header_1")
                .createdBy("Jack")
                .createdDt(ZonedDateTime.now())
                .build());
        post2 = postRepository.save(Post.builder()
                .header("header_2")
                .createdBy("Adam")
                .createdDt(ZonedDateTime.now())
                .build());
        comment1 = commentRepository.save(Comment.builder()
                .message("comment_1")
                .createdBy("Jack")
                .createdDt(ZonedDateTime.now())
                .post(post1)
                .build());
    }

    @AfterEach
    void cleanUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void findAllPostsReturnsEveryPost() {
        graphQlTester.document("query { findAllPosts { id header createdBy } }")
                .execute()
                .path("findAllPosts")
                .entityList(Object.class)
                .hasSize(2);
    }

    @Test
    void findAllPostsPagePaginatesResults() {
        graphQlTester.document("query { findAllPostsPage(page: 0, size: 1) { totalElements totalPages currentPage size posts { id } } }")
                .execute()
                .path("findAllPostsPage.totalElements").entity(Long.class).isEqualTo(2L)
                .path("findAllPostsPage.totalPages").entity(Integer.class).isEqualTo(2)
                .path("findAllPostsPage.posts").entityList(Object.class).hasSize(1);
    }

    @Test
    void findPostByIdReturnsMatchingPost() {
        graphQlTester.document("query { findPostById(id: " + post1.getId() + ") { id header createdBy } }")
                .execute()
                .path("findPostById.header").entity(String.class).isEqualTo("header_1")
                .path("findPostById.createdBy").entity(String.class).isEqualTo("Jack");
    }

    @Test
    void findPostByIdReturnsNullWhenMissing() {
        graphQlTester.document("query { findPostById(id: 999999) { id } }")
                .execute()
                .path("findPostById").valueIsNull();
    }

    @Test
    void countPostsReturnsTotalCount() {
        graphQlTester.document("query { countPosts }")
                .execute()
                .path("countPosts").entity(Long.class).isEqualTo(2L);
    }

    @Test
    void findAllCommentsIncludesNestedPost() {
        graphQlTester.document("query { findAllComments { id message post { id header } } }")
                .execute()
                .path("findAllComments[0].message").entity(String.class).isEqualTo("comment_1")
                .path("findAllComments[0].post.id").entity(String.class).isEqualTo(String.valueOf(post1.getId()));
    }

    @Test
    void findCommentByIdIncludesNestedPost() {
        graphQlTester.document("query { findCommentById(id: " + comment1.getId() + ") { id message post { header } } }")
                .execute()
                .path("findCommentById.message").entity(String.class).isEqualTo("comment_1")
                .path("findCommentById.post.header").entity(String.class).isEqualTo("header_1");
    }

    @Test
    void findCommentByIdErrorsWhenMissing() {
        // The schema declares findCommentById as non-nullable (Comment!), so a missing
        // comment surfaces as a GraphQL execution error rather than a null result.
        graphQlTester.document("query { findCommentById(id: 999999) { id } }")
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors).isNotEmpty());
    }

    @Test
    void findCommentsByPostIdReturnsOnlyThatPostsComments() {
        graphQlTester.document("query { findCommentsByPostId(postId: " + post1.getId() + ") { id message } }")
                .execute()
                .path("findCommentsByPostId").entityList(Object.class).hasSize(1);
    }

    @Test
    void findCommentsByPostIdErrorsWhenPostMissing() {
        graphQlTester.document("query { findCommentsByPostId(postId: 999999) { id } }")
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors).isNotEmpty());
    }
}
