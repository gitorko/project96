package com.demo.project96;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.demo.project96.domain.Comment;
import com.demo.project96.domain.Post;
import com.demo.project96.repo.CommentRepository;
import com.demo.project96.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner seedData(ApplicationContext ctx) {
        return args -> {
            List<String> person = Arrays.asList("Jack", "Adam", "Raj");
            commentRepository.deleteAll();
            postRepository.deleteAll();
            IntStream.range(1, 50).forEach(i -> {
                int rand1 = new Random().nextInt(2 - 0 + 1) + 0;
                Post post = postRepository.save(Post.builder()
                        .header("header_" + i)
                        .createdBy(person.get(rand1))
                        .createdDt(ZonedDateTime.now())
                        .build());
                IntStream.range(1, 4).forEach(j -> {
                    int rand2 = new Random().nextInt(2 - 0 + 1) + 0;
                    Comment comment = Comment.builder().message(post.getHeader() + "_comment_" + j)
                            .createdBy(person.get(rand2))
                            .createdDt(ZonedDateTime.now())
                            .post(post).build();
                    commentRepository.save(comment);
                });
            });
        };
    }

}
