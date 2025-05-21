package com.example.pioneerpixel.service;

import com.example.pioneerpixel.dto.UserEmailDtoRequest;
import com.example.pioneerpixel.dto.UserEmailDtoResponse;

public interface EmailDataService {

  UserEmailDtoResponse addEmailOrUpdate(Long userId, UserEmailDtoRequest email);

  void deleteEmail(Long userId, UserEmailDtoRequest email);
}
