package com.catchtwobirds.soboro.user.repository;

import com.catchtwobirds.soboro.user.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

}
