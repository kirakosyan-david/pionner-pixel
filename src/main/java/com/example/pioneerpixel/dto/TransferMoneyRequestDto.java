package com.example.pioneerpixel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoneyRequestDto {

    private Long fromUserId;

    private Long toUserId;

    private BigDecimal amount;
}