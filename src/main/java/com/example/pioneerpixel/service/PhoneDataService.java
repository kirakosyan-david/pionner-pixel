package com.example.pioneerpixel.service;

import com.example.pioneerpixel.dto.UserPhoneDtoRequest;
import com.example.pioneerpixel.dto.UserPhoneDtoResponse;

public interface PhoneDataService {

    UserPhoneDtoResponse addPhoneOrUpdate(Long userId, UserPhoneDtoRequest phone);


    void deletePhone(Long userId, UserPhoneDtoRequest email);
}
