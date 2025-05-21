package com.example.pioneerpixel.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.pioneerpixel.BaseUnitTest;
import com.example.pioneerpixel.entity.Account;
import com.example.pioneerpixel.repositroy.AccountRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class AccountServiceTest extends BaseUnitTest {

  @Mock private AccountRepository accountRepository;

  @InjectMocks private AccountService accountService;

  @BeforeEach
  void setUp() {
    accountRepository = mock(AccountRepository.class);
    accountService = new AccountService(accountRepository);
  }

  @Test
  void testIncreaseBalance_MultipleAccounts() {
    Account acc1 = new Account();
    acc1.setBalance(new BigDecimal("50.00"));
    acc1.setInitialBalance(new BigDecimal("50.00"));

    Account acc2 = new Account();
    acc2.setBalance(new BigDecimal("200.00"));
    acc2.setInitialBalance(new BigDecimal("100.00"));

    when(accountRepository.findAll()).thenReturn(Arrays.asList(acc1, acc2));

    accountService.increaseBalance();

    verify(accountRepository, times(2)).save(any(Account.class));
  }
}
