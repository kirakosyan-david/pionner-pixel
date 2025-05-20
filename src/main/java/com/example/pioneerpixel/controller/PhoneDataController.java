package com.example.pioneerpixel.controller;

import com.example.pioneerpixel.dto.UserPhoneDtoRequest;
import com.example.pioneerpixel.dto.UserPhoneDtoResponse;
import com.example.pioneerpixel.entity.PhoneData;
import com.example.pioneerpixel.service.PhoneDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/phone")
public class PhoneDataController {

    private final PhoneDataService phoneDataService;

    @PostMapping("{id}")
    public ResponseEntity<UserPhoneDtoResponse> sendEmail(@PathVariable("id") Long userId,
                                               @RequestBody UserPhoneDtoRequest phone) {
        UserPhoneDtoResponse userPhoneDtoResponse = phoneDataService.addPhoneOrUpdate(userId, phone);
        return ResponseEntity.ok(userPhoneDtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PhoneData> deleteEmail(@PathVariable("id") Long userId, @RequestBody UserPhoneDtoRequest phone) {
        phoneDataService.deletePhone(userId, phone);
        return ResponseEntity.ok().build();
    }

}
