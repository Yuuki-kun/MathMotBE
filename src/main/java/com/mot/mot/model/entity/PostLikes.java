package com.mot.mot.model.entity;

import com.mot.mot.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_likes")
public class PostLikes {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private ClassPost classPost;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Date likedAt;
}
