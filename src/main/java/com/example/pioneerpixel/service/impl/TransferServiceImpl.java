package com.example.pioneerpixel.service.impl;

import com.example.pioneerpixel.dto.TransferMoneyRequestDto;
import com.example.pioneerpixel.dto.UserDtoResponse;
import com.example.pioneerpixel.entity.Account;
import com.example.pioneerpixel.exception.custom.TransferException;
import com.example.pioneerpixel.kafka.KafkaProducer;
import com.example.pioneerpixel.repositroy.AccountRepository;
import com.example.pioneerpixel.service.TransferService;
import com.example.pioneerpixel.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final KafkaProducer kafkaProducer;


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transferMoney(TransferMoneyRequestDto transfer) {
        if (transfer.getAmount() == null || transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Invalid transfer amount");
        }

        UserDtoResponse fromUser = userService.getUserById(transfer.getFromUserId());
        UserDtoResponse toUser = userService.getUserById(transfer.getToUserId());

        if (fromUser == null || toUser == null) {
            throw new TransferException("One or both users not found");
        }

        BigDecimal fromBalance = fromUser.getBalance();
        if (fromBalance == null || fromBalance.compareTo(transfer.getAmount()) < 0) {
            throw new TransferException("Insufficient funds for transfer");
        }

        Long fromUserId = fromUser.getId();
        Long toUserId = toUser.getId();

        Account fromAccount = accountRepository.findById(fromUserId)
                .orElseThrow(() -> new TransferException("From account not found"));
        Account toAccount = accountRepository.findById(toUserId)
                .orElseThrow(() -> new TransferException("To account not found"));

        log.info("Transferring {} from user {} to user {}", transfer.getAmount(), fromUserId, toUserId);

        fromAccount.setBalance(fromAccount.getBalance().subtract(transfer.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(transfer.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        log.info("Transfer completed successfully");

        kafkaProducer.send("my-topic", String.valueOf(fromUserId), transfer);

    }
}
