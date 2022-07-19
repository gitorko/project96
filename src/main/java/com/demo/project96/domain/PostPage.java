package com.demo.project96.domain;

import lombok.Builder;

@Builder
public class PostPage {
    Iterable<Post> posts;
    long totalElements;
    int totalPages;
    int currentPage;
    int size;
}
