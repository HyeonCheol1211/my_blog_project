package com.blog.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Getter @Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate date;

    private String writer;

    public Post(String title, String content, LocalDate date, String writer) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.writer = writer;
    }

}