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
@Table(name = "teacher")
public class Teacher {
    @Id
    @GeneratedValue
    private Long teacherId;

    @OneToOne(mappedBy = "teacher", cascade = CascadeType.ALL)
    private User user;
}
