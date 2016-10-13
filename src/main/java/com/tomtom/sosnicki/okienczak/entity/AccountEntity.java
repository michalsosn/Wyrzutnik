package com.tomtom.sosnicki.okienczak.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Account")
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue
    @Column(name = "account_id", nullable = false, updatable = false)
    private long id;

    @NotNull
    @Size(min = 4, max = 32)
    @Column(name = "username", nullable = false, length = 32, unique = true)
    private String username;

    @NotNull
    @Size(min = 59, max = 60)
    @Column(name = "password", columnDefinition = "char(60) not null")
    private String password;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER,
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthorityEntity> authorities = new ArrayList<>();

    public AccountEntity() {
    }

    public AccountEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<AuthorityEntity> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "AccountEntity{"
             + "username='" + username + '\''
             + ", id=" + id
             + '}';
    }
}
