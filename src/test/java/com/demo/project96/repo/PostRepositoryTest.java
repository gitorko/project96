package com.demo.project96.repo;

import java.time.ZonedDateTime;

import com.demo.project96.domain.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    void cleanUp() {
        postRepository.deleteAll();
    }

    private Post newPost(String header, String createdBy) {
        return postRepository.save(Post.builder()
                .header(header)
                .createdBy(createdBy)
                .createdDt(ZonedDateTime.now())
                .build());
    }

    @Test
    void savesAndFindsPostById() {
        Post saved = newPost("header_1", "Jack");

        assertThat(postRepository.findById(saved.getId())).isPresent();
        assertThat(postRepository.findById(saved.getId()).get().getHeader()).isEqualTo("header_1");
    }

    @Test
    void countsAllPosts() {
        newPost("header_1", "Jack");
        newPost("header_2", "Adam");

        assertThat(postRepository.count()).isEqualTo(2);
    }

    @Test
    void paginatesPosts() {
        newPost("header_1", "Jack");
        newPost("header_2", "Adam");
        newPost("header_3", "Raj");

        Page<Post> page = postRepository.findAll(PageRequest.of(0, 2));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        assertThat(postRepository.findById(-1L)).isEmpty();
    }
}
