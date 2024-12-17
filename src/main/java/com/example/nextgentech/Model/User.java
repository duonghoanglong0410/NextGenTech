package com.example.nextgentech.Model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
    private String password;
    private String role; // Thêm trường role
    private String picture;
    private String givenName;
    private String familyName;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role); // Trả về vai trò của người dùng
    }
    // Constructor
    public User() {}

    public String getName() {
        return name;
    }
    public String getPicture() {
        return picture;
    }
    public String getEmail() {return email;}
    public String getRole() {return role;}
    public void setEmail(String email) {
        this.email = email;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setPicture(String picture) {this.picture = picture;}
    public void setGivenName(String givenName) {this.givenName = givenName;}
    public void setFamilyName(String familyName) {this.familyName=familyName;}
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
