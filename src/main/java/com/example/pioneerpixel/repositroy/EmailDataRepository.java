package com.example.pioneerpixel.repositroy;

import com.example.pioneerpixel.entity.EmailData;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

  Optional<EmailData> findByEmail(String email);

  Optional<EmailData> findByEmailAndUserId(String email, Long userId);

  Long countByUserId(Long userId);

  Optional<EmailData> findByUserId(Long userId);
}
