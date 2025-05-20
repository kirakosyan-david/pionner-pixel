package com.example.pioneerpixel.controller;

import com.example.pioneerpixel.dto.SearchUserDto;
import com.example.pioneerpixel.dto.SearchUserDtoResponse;
import com.example.pioneerpixel.dto.UserAuthResponseDto;
import com.example.pioneerpixel.dto.UserContactDtoResponse;
import com.example.pioneerpixel.dto.UserDtoResponse;
import com.example.pioneerpixel.dto.UserLoginRequestDto;
import com.example.pioneerpixel.service.UserService;
import com.example.pioneerpixel.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil tokenUtil;


    @PostMapping("/login")
    public ResponseEntity<UserAuthResponseDto> auth(@RequestBody UserLoginRequestDto loginRequestDto) {
        log.info("Received login request with email: {}", loginRequestDto.getEmail());
        boolean result = userService.userLogin(loginRequestDto);
        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String token = tokenUtil.generateToken(loginRequestDto.getEmail());
        return ResponseEntity.ok(new UserAuthResponseDto(token));
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDtoResponse> getUserById(@PathVariable("id") Long id) {
        UserDtoResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/contact")
    public ResponseEntity<UserContactDtoResponse> getUserByContact(@PathVariable("id") Long id) {
        UserContactDtoResponse contact = userService.getUserWithContact(id);
        return ResponseEntity.ok(contact);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<SearchUserDtoResponse>> searchUser(@RequestBody SearchUserDto searchUserDto) {
        Page<SearchUserDtoResponse> users = userService.searchUser(searchUserDto);
        return ResponseEntity.ok(users);
    }
}
