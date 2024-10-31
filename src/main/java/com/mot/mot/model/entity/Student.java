package com.mot.mot.model.entity;

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
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue
    private Long studentId;

    private Long Grade;
    private Long ClassName;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private User user;

}
