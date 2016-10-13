package com.tomtom.sosnicki.okienczak.service;

import com.tomtom.sosnicki.okienczak.entity.AccountEntity;
import com.tomtom.sosnicki.okienczak.entity.AuthorityEntity;
import com.tomtom.sosnicki.okienczak.repository.AccountRepository;
import com.tomtom.sosnicki.okienczak.rest.dto.AccountDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    private AuthorityEntity.Name defaultAuthority = AuthorityEntity.Name.ROLE_USER;

    @Autowired
    public AccountService(PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    public AccountDto getAccountWithoutAuthorities(String username) {
        final AccountEntity entity = accountRepository.findByUsername(username).get();
        final AccountDto dto = new AccountDto(entity);
        dto.unsetAuthorities();
        return dto;
    }

    public AccountDto getAccountWithAuthorities(String username) {
        final AccountEntity account = accountRepository.findByUsername(username).get();
        return new AccountDto(account);
    }

    public AccountEntity addAccountWithDefaultAuthorities(AccountDto dto) {
        if (dto.areAuthoritiesSet()) {
            throw new IllegalArgumentException("Authorities are set when they should not.");
        }
        dto.setAuthorities(defaultAuthority);
        return addAccount(dto);
    }

    public AccountEntity addAccountWithSetAuthorities(AccountDto dto) {
        if (!dto.areAuthoritiesSet()) {
            throw new IllegalArgumentException("Authorities not set for creation.");
        }
        return addAccount(dto);
    }

    private AccountEntity addAccount(AccountDto dto) {
        AccountEntity entity = dto.toEntity(passwordEncoder);

        log.info("Adding account {}", entity);

        entity = accountRepository.save(entity);

        return entity;
    }

    public void updateAccountWithoutAuthorities(String username, AccountDto dto) {
        if (dto.areAuthoritiesSet()) {
            throw new IllegalArgumentException("Authorities are set when they should not be.");
        }
        updateAccount(username, dto);
    }

    public void updateAccountWithAuthorities(String username, AccountDto dto) {
        if (!dto.areAuthoritiesSet()) {
            throw new IllegalArgumentException("Authorities not set for update.");
        }
        updateAccount(username, dto);
    }

    private void updateAccount(String username, AccountDto dto) {
        final AccountEntity entity = accountRepository.findByUsername(username).get();

        log.info("Updating account {} to {}", entity, dto);

        dto.applyToEntity(passwordEncoder, entity);
    }

    public void deleteAccount(String username) {
        final AccountEntity entity = accountRepository.findByUsername(username).get();

        log.info("Deleting account with id {}", username);

        accountRepository.delete(entity);
    }

}
