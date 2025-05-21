package com.example.pioneerpixel.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
