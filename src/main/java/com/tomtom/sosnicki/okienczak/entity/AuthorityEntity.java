package com.tomtom.sosnicki.okienczak.entity;

import javax.persistence.*;

@Entity(name = "Authority")
@Table(name = "authority")
public class AuthorityEntity {

    public enum Name {
        ROLE_USER, ROLE_ADMIN
    }

    @Id
    @GeneratedValue
    @Column(name = "authority_id", nullable = false, updatable = false)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 20, nullable = false)
    private Name name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", updatable = false, nullable = false)
    private AccountEntity account;

    public AuthorityEntity() {
    }

    public AuthorityEntity(Name name, AccountEntity account) {
        this.name = name;
        this.account = account;
    }

    public long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "AuthorityEntity{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
