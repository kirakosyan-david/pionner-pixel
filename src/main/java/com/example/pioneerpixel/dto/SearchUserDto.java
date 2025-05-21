package com.example.pioneerpixel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class SearchUserDto {

  private String name;
  private String phone;
  private String email;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private String dateOfBirth;

  private int page;
  private int size;
}
