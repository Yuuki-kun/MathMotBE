package com.mot.mot.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mot.mot.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "class_post")
public class ClassPost {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 500)
    private String title;

    //as text
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_user_id")
    @JsonIgnore
    private User user;

    private int likeCount;

    @ManyToOne
    @JoinColumn(name = "parent_class_id")
    @JsonIgnore
    private Class parentClass;

}
