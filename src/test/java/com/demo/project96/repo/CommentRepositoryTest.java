package com.demo.project96.repo;

import java.time.ZonedDateTime;

import com.demo.project96.domain.Comment;
import com.demo.project96.domain.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void cleanUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    private Post newPost() {
        return postRepository.save(Post.builder()
                .header("header_1")
                .createdBy("Jack")
                .createdDt(ZonedDateTime.now())
                .build());
    }

    private Comment newComment(Post post, String message) {
        return commentRepository.save(Comment.builder()
                .message(message)
                .createdBy("Jack")
                .createdDt(ZonedDateTime.now())
                .post(post)
                .build());
    }

    @Test
    void findsCommentsByPost() {
        Post post = newPost();
        newComment(post, "comment_1");
        newComment(post, "comment_2");

        Iterable<Comment> comments = commentRepository.findByPost(post);

        assertThat(comments).hasSize(2)
                .extracting(Comment::getMessage)
                .containsExactlyInAnyOrder("comment_1", "comment_2");
    }

    @Test
    void findAllCommentsEagerlyFetchesPost() {
        Post post = newPost();
        newComment(post, "comment_1");

        var comments = commentRepository.findAllComments();

        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getPost().getId()).isEqualTo(post.getId());
    }
}
