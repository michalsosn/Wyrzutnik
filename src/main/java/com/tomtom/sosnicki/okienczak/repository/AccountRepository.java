package com.tomtom.sosnicki.okienczak.repository;

import com.tomtom.sosnicki.okienczak.entity.AccountEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY)
public interface AccountRepository extends PagingAndSortingRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByUsername(String username);
}
