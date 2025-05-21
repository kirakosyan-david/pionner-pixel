package com.example.pioneerpixel.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoRequest {

  private String name;

  private String dateOfBirth;

  private BigDecimal balance;

  private BigDecimal initialBalance;

  private String email;

  private String phone;
}
