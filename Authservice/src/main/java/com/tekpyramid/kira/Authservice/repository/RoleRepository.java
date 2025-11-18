package com.tekpyramid.kira.Authservice.repository;

import com.tekpyramid.kira.Authservice.entity.Auth;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Auth, String> {

    <T> Optional<T> findByRoleName(String admin);
}
