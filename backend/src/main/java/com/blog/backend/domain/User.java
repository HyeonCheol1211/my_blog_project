package com.blog.backend.domain;

import com.blog.backend.dto.SignUpDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String email;
    private String password;
    private int age;
    private String gender;
    private LocalDate date;

    public User(String name, String email, String password, int age, String gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.date = LocalDate.now();
    }

    public User(SignUpDto signUpDto,  LocalDate date) {
        this.name = signUpDto.getName();
        this.email = signUpDto.getEmail();
        this.password = signUpDto.getPassword();
        this.age = signUpDto.getAge();
        this.gender = signUpDto.getGender();
        this.date = date;
    }
}
