package com.example.pioneerpixel.service;

import com.example.pioneerpixel.dto.SearchUserDto;
import com.example.pioneerpixel.dto.SearchUserDtoResponse;
import com.example.pioneerpixel.dto.UserContactDtoResponse;
import com.example.pioneerpixel.dto.UserDtoResponse;
import com.example.pioneerpixel.dto.UserLoginRequestDto;
import org.springframework.data.domain.Page;

public interface UserService {

    UserDtoResponse getUserById(Long id);

    UserContactDtoResponse getUserWithContact(Long id);

    Page<SearchUserDtoResponse> searchUser(SearchUserDto searchUserDto);

    boolean userLogin(UserLoginRequestDto loginRequestDto);
}
