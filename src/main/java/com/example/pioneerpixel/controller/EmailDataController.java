package com.example.pioneerpixel.controller;

import com.example.pioneerpixel.dto.UserEmailDtoRequest;
import com.example.pioneerpixel.dto.UserEmailDtoResponse;
import com.example.pioneerpixel.entity.EmailData;
import com.example.pioneerpixel.service.EmailDataService;
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
@RequestMapping("/api/users/email")
public class EmailDataController {

    private final EmailDataService emailDataService;

    @PostMapping("{id}")
    public ResponseEntity<UserEmailDtoResponse> sendEmail(@PathVariable("id") Long userId,
                                               @RequestBody UserEmailDtoRequest userEmailDtoRequest) {
        UserEmailDtoResponse userEmailDtoResponse = emailDataService.addEmailOrUpdate(userId, userEmailDtoRequest);
        return ResponseEntity.ok(userEmailDtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmailData> deleteEmail(@PathVariable("id") Long userId, @RequestBody UserEmailDtoRequest email) {
        emailDataService.deleteEmail(userId, email);
        return ResponseEntity.ok().build();
    }

}
