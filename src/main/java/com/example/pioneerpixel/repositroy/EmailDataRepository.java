package com.example.pioneerpixel.repositroy;

import com.example.pioneerpixel.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    Optional<EmailData> findByEmail(String email);

    Optional<EmailData> findByEmailAndUserId(String email, Long userId);

    Long countByUserId(Long userId);

    Optional<EmailData> findByUserId(Long userId);
}
