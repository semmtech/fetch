package com.semmtech.laces.fetch.security.repository;

import com.semmtech.laces.fetch.security.model.LacesFetchUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<LacesFetchUser, String> {
    Optional<LacesFetchUser> findByUsername(String username);
}
