package com.mot.mot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(1234, Collections.emptySet()),
    TESTER(5678,Collections.emptySet()),
    ADMIN(9012,
            Set.of(
                    Permission.ADMIN_READ,
                    Permission.ADMIN_UPDATE,
                    Permission.ADMIN_CREATE,
                    Permission.ADMIN_DELETE
            )
    );

    private final int value;

    private final Set<Permission> permissions;

    //use for spring security
    public List<SimpleGrantedAuthority> getAuthorities(){
        var authorities =  getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
        System.out.println("autho="+authorities);
        return authorities;
    }

}
