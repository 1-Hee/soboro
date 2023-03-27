package com.catchtwobirds.soboro.user.repository;

import com.catchtwobirds.soboro.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(" SELECT u FROM User as u WHERE u.userId=:userId AND u.userActive=true")
    Optional<User> findByUserId(String userId);

    @Query(" SELECT u FROM User as u WHERE u.userId=:userId AND u.userActive=true")
    User findEntityByUserId(String userId);

    @Query(" SELECT u FROM User as u WHERE u.userName=:userName AND u.userActive=true")
    Optional<User> findByUserName(String userName);

    @Query(" SELECT u FROM User as u WHERE u.userEmail=:userEmail AND u.userActive=true")
    Optional<User> findByUserEmail(String userEmail);

    @Query(" SELECT u FROM User as u WHERE u.userId=:userId AND u.userEmail=:userEmail AND u.userActive=true")
    Optional<User> findByUserEmailAndUserId(String userId, String userEmail);
}
