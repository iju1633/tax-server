package com.tax.server.repository;

import com.tax.server.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> { // 유저 데이터에 활용

    Users findByUserId(String userId);

    boolean existsByUserId(String userId);

    Users findByName(String name);
}
