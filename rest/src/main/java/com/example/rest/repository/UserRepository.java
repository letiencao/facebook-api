package com.example.rest.repository;

import com.example.rest.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByPhoneNumber(String phoneNumber);
    User save(User user);
}
