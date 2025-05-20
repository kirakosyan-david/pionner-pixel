package com.example.pioneerpixel.scheduler;

import com.example.pioneerpixel.entity.Account;
import com.example.pioneerpixel.repositroy.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void increaseBalance() {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            BigDecimal currentBalance = account.getBalance();
            BigDecimal initialBalance = account.getInitialBalance();
            BigDecimal maxBalance = initialBalance.multiply(BigDecimal.valueOf(2.07));

            BigDecimal newBalance = currentBalance.multiply(BigDecimal.valueOf(1.10));

            if (newBalance.compareTo(maxBalance) > 0) {
                newBalance = maxBalance;
            }

            account.setBalance(newBalance);
            accountRepository.save(account);
        }
    }
}
