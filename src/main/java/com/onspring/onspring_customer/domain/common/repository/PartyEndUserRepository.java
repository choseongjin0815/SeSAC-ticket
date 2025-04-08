package com.onspring.onspring_customer.domain.common.repository;

import com.onspring.onspring_customer.domain.common.entity.PartyEndUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface PartyEndUserRepository extends JpaRepository<PartyEndUser, Long> {
    List<PartyEndUser> findByParty_IdIn(@NonNull Collection<Long> ids);
}