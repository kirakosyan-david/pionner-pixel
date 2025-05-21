package com.example.pioneerpixel.repositroy;

import com.example.pioneerpixel.entity.PhoneData;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

  Optional<PhoneData> findByPhone(String phone);

  Optional<PhoneData> findByPhoneAndUserId(String phone, Long userId);

  Long countByUserId(Long userId);

  Optional<PhoneData> findByUserId(Long userId);
}
