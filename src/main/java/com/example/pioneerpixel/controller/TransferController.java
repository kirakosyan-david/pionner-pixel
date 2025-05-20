package com.example.pioneerpixel.controller;

import com.example.pioneerpixel.dto.TransferMoneyRequestDto;
import com.example.pioneerpixel.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/transfer")
public class TransferController {

    private final TransferService transferService;


    @PostMapping
    public ResponseEntity<String> transfer(@RequestBody TransferMoneyRequestDto transfer) {
        try {
            transferService.transferMoney(transfer);
            return ResponseEntity.ok("Transfer completed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during the transfer: " + e.getMessage());
        }
    }
}
