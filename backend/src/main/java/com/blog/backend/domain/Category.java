package com.blog.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    Long count=0L;

    public void increaseCount(){
        count++;
    }

    public void decreaseCount(){
        count--;
    }

    @Builder
    public Category(User user, String name){
        this.user = user;
        this.name = name;
    }
}
