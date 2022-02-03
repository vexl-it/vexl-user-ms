package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("select case when count(u) > 0 THEN true ELSE false END from User u where u.username = :username ")
    boolean isUsernameAvailable(String username);

    @Query("select u.publicKey from User u where u.id = :userId ")
    Optional<String> retrievePublicKeyByUserId(long userId);
}
