package com.mot.mot.model;

import com.mot.mot.model.entity.Student;
import com.mot.mot.model.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "__user")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    private String password;

    @Column(length = 255)
    private String fullName;

    @Column(columnDefinition = "boolean default false")
    private Boolean locked=false;

    @Column(columnDefinition = "boolean default true")
    private Boolean enabled=true;

    private Date createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", referencedColumnName = "studentId", nullable = true)
    private Student student;

    public void setStudent(Student student) {
        this.student = student;
        if (student != null) {
            student.setUser(this);
        }
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "teacher_id", referencedColumnName = "teacherId", nullable = true)
    private Teacher teacher;

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        if (teacher != null) {
            teacher.setUser(this);
        }
    }

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(Role::getAuthorities)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
