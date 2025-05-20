package com.example.pioneerpixel.repositroy;

import com.example.pioneerpixel.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
