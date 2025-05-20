package com.example.pioneerpixel.service;

import com.example.pioneerpixel.BaseUnitTest;
import com.example.pioneerpixel.dto.TransferMoneyRequestDto;
import com.example.pioneerpixel.dto.UserDtoResponse;
import com.example.pioneerpixel.entity.Account;
import com.example.pioneerpixel.exception.custom.TransferException;
import com.example.pioneerpixel.repositroy.AccountRepository;
import com.example.pioneerpixel.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TransferServiceTest extends BaseUnitTest {


    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransferServiceImpl transferService;

    private TransferMoneyRequestDto transferDto;
    private UserDtoResponse fromUser;
    private UserDtoResponse toUser;
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        transferDto = new TransferMoneyRequestDto();
        transferDto.setFromUserId(1L);
        transferDto.setToUserId(2L);
        transferDto.setAmount(new BigDecimal("100.00"));

        fromUser = new UserDtoResponse();
        fromUser.setId(1L);
        fromUser.setBalance(new BigDecimal("500.00"));

        toUser = new UserDtoResponse();
        toUser.setId(2L);
        toUser.setBalance(new BigDecimal("200.00"));

        fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setBalance(new BigDecimal("500.00"));

        toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setBalance(new BigDecimal("200.00"));
    }

    @Test
    void transferMoney_success() {
        when(userService.getUserById(1L)).thenReturn(fromUser);
        when(userService.getUserById(2L)).thenReturn(toUser);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> transferService.transferMoney(transferDto));

        assertEquals(new BigDecimal("400.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("300.00"), toAccount.getBalance());

        verify(userService).getUserById(1L);
        verify(userService).getUserById(2L);
        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transferMoney_invalidAmount_throwsException() {
        transferDto.setAmount(BigDecimal.ZERO);

        TransferException exception = assertThrows(
                TransferException.class,
                () -> transferService.transferMoney(transferDto)
        );

        assertEquals("Invalid transfer amount", exception.getMessage());
        verifyNoInteractions(userService, accountRepository);
    }

    @Test
    void transferMoney_nullAmount_throwsException() {
        transferDto.setAmount(null);

        TransferException exception = assertThrows(
                TransferException.class,
                () -> transferService.transferMoney(transferDto)
        );

        assertEquals("Invalid transfer amount", exception.getMessage());
        verifyNoInteractions(userService, accountRepository);
    }

    @Test
    void transferMoney_fromUserNotFound_throwsException() {
        when(userService.getUserById(1L)).thenReturn(null);
        when(userService.getUserById(2L)).thenReturn(toUser);

        TransferException exception = assertThrows(
                TransferException.class,
                () -> transferService.transferMoney(transferDto)
        );

        assertEquals("One or both users not found", exception.getMessage());
        verify(userService).getUserById(1L);
        verify(userService).getUserById(2L);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void transferMoney_toUserNotFound_throwsException() {
        when(userService.getUserById(1L)).thenReturn(fromUser);
        when(userService.getUserById(2L)).thenReturn(null);

        TransferException exception = assertThrows(
                TransferException.class,
                () -> transferService.transferMoney(transferDto)
        );

        assertEquals("One or both users not found", exception.getMessage());
        verify(userService).getUserById(1L);
        verify(userService).getUserById(2L);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void transferMoney_insufficientFunds_throwsException() {
        transferDto.setAmount(new BigDecimal("600.00"));

        when(userService.getUserById(1L)).thenReturn(fromUser);
        when(userService.getUserById(2L)).thenReturn(toUser);

        TransferException exception = assertThrows(
                TransferException.class,
                () -> transferService.transferMoney(transferDto)
        );

        assertEquals("Insufficient funds for transfer", exception.getMessage());
        verify(userService).getUserById(1L);
        verify(userService).getUserById(2L);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void transferMoney_fromAccountNotFound_throwsException() {
        when(userService.getUserById(1L)).thenReturn(fromUser);
        when(userService.getUserById(2L)).thenReturn(toUser);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        TransferException exception = assertThrows(
                TransferException.class,
                () -> transferService.transferMoney(transferDto)
        );

        assertEquals("From account not found", exception.getMessage());
        verify(userService).getUserById(1L);
        verify(userService).getUserById(2L);
        verify(accountRepository).findById(1L);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void transferMoney_toAccountNotFound_throwsException() {
        when(userService.getUserById(1L)).thenReturn(fromUser);
        when(userService.getUserById(2L)).thenReturn(toUser);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        TransferException exception = assertThrows(
                TransferException.class,
                () -> transferService.transferMoney(transferDto)
        );

        assertEquals("To account not found", exception.getMessage());
        verify(userService).getUserById(1L);
        verify(userService).getUserById(2L);
        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verifyNoMoreInteractions(accountRepository);
    }

}