package com.example.pioneerpixel.service;

import com.example.pioneerpixel.BaseUnitTest;
import com.example.pioneerpixel.dto.SearchUserDto;
import com.example.pioneerpixel.dto.SearchUserDtoResponse;
import com.example.pioneerpixel.dto.UserContactDtoResponse;
import com.example.pioneerpixel.dto.UserDtoResponse;
import com.example.pioneerpixel.dto.UserLoginRequestDto;
import com.example.pioneerpixel.entity.EmailData;
import com.example.pioneerpixel.entity.User;
import com.example.pioneerpixel.exception.custom.UserNotFoundException;
import com.example.pioneerpixel.mapper.UserMapper;
import com.example.pioneerpixel.repositroy.EmailDataRepository;
import com.example.pioneerpixel.repositroy.UserRepository;
import com.example.pioneerpixel.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class UserServiceTest extends BaseUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailDataRepository emailDataRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private EmailData emailData;
    private UserLoginRequestDto loginRequestDto;
    private SearchUserDto searchUserDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setPassword("password");

        emailData = new EmailData();
        emailData.setEmail("test@example.com");
        emailData.setUser(user);

        loginRequestDto = new UserLoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("password");

        searchUserDto = new SearchUserDto();
        searchUserDto.setPage(0);
        searchUserDto.setSize(10);
    }

    @Test
    void userLogin_success() {
        when(emailDataRepository.findByEmail("test@example.com")).thenReturn(Optional.of(emailData));

        boolean result = userService.userLogin(loginRequestDto);

        assertTrue(result);
        verify(emailDataRepository).findByEmail("test@example.com");
    }

    @Test
    void userLogin_emailNotFound() {
        when(emailDataRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        boolean result = userService.userLogin(loginRequestDto);

        assertFalse(result);
        verify(emailDataRepository).findByEmail("test@example.com");
    }

    @Test
    void userLogin_invalidPassword() {
        loginRequestDto.setPassword("wrong");
        when(emailDataRepository.findByEmail("test@example.com")).thenReturn(Optional.of(emailData));

        boolean result = userService.userLogin(loginRequestDto);

        assertFalse(result);
        verify(emailDataRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserById_success() {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserDtoResponse(user)).thenReturn(userDtoResponse);

        UserDtoResponse result = userService.getUserById(1L);

        assertEquals(userDtoResponse, result);
        verify(userRepository).findById(1L);
        verify(userMapper).mapToUserDtoResponse(user);
    }

    @Test
    void getUserById_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserWithContact_success() {
        UserContactDtoResponse contactDtoResponse = new UserContactDtoResponse();
        when(userRepository.findByIdWithEmailsAndAccount(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByIdWithPhones(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserContactDtoResponse(user)).thenReturn(contactDtoResponse);

        UserContactDtoResponse result = userService.getUserWithContact(1L);

        assertEquals(contactDtoResponse, result);
        verify(userRepository).findByIdWithEmailsAndAccount(1L);
        verify(userRepository).findByIdWithPhones(1L);
        verify(userMapper).mapToUserContactDtoResponse(user);
    }

    @Test
    void getUserWithContact_userNotFound() {
        when(userRepository.findByIdWithEmailsAndAccount(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserWithContact(1L));
        verify(userRepository).findByIdWithEmailsAndAccount(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void searchUser_withDateOfBirth_success() {
        searchUserDto.setDateOfBirth("2000-01-01");
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);
        SearchUserDtoResponse searchResponse = new SearchUserDtoResponse();

        when(userRepository.searchByUserWithDate(isNull(), isNull(), isNull(), any(LocalDate.class), eq(pageable)))
                .thenReturn(userPage);
        when(userRepository.findUsersWithDetailsByIds(Collections.singletonList(1L))).thenReturn(users);
        when(userMapper.mapToSearchUserDtoResponse(eq(user), eq(0), eq(10))).thenReturn(searchResponse);

        Page<SearchUserDtoResponse> result = userService.searchUser(searchUserDto);

        assertEquals(1, result.getTotalElements());
        assertEquals(searchResponse, result.getContent().get(0));
        verify(userRepository).searchByUserWithDate(isNull(), isNull(), isNull(), any(LocalDate.class), eq(pageable));
        verify(userRepository).findUsersWithDetailsByIds(Collections.singletonList(1L));
        verify(userRepository).fetchEmailsByIds(Collections.singletonList(1L));
        verify(userRepository).fetchPhonesByIds(Collections.singletonList(1L));
        verify(userMapper).mapToSearchUserDtoResponse(eq(user), eq(0), eq(10));
    }

    @Test
    void searchUser_withoutDateOfBirth_success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);
        SearchUserDtoResponse searchResponse = new SearchUserDtoResponse();

        when(userRepository.searchByUserWithoutDate(isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(userPage);
        when(userRepository.findUsersWithDetailsByIds(Collections.singletonList(1L))).thenReturn(users);
        when(userMapper.mapToSearchUserDtoResponse(eq(user), eq(0), eq(10))).thenReturn(searchResponse);

        Page<SearchUserDtoResponse> result = userService.searchUser(searchUserDto);

        assertEquals(1, result.getTotalElements());
        assertEquals(searchResponse, result.getContent().get(0));
        verify(userRepository).searchByUserWithoutDate(isNull(), isNull(), isNull(), eq(pageable));
        verify(userRepository).findUsersWithDetailsByIds(Collections.singletonList(1L));
        verify(userRepository).fetchEmailsByIds(Collections.singletonList(1L));
        verify(userRepository).fetchPhonesByIds(Collections.singletonList(1L));
        verify(userMapper).mapToSearchUserDtoResponse(eq(user), eq(0), eq(10));
    }

    @Test
    void searchUser_invalidDateOfBirth_logsWarningAndSearchesWithoutDate() {
        searchUserDto.setDateOfBirth("invalid-date");
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);
        SearchUserDtoResponse searchResponse = new SearchUserDtoResponse();

        when(userRepository.searchByUserWithoutDate(isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(userPage);
        when(userRepository.findUsersWithDetailsByIds(Collections.singletonList(1L))).thenReturn(users);
        when(userMapper.mapToSearchUserDtoResponse(eq(user), eq(0), eq(10))).thenReturn(searchResponse);

        Page<SearchUserDtoResponse> result = userService.searchUser(searchUserDto);

        assertEquals(1, result.getTotalElements());
        assertEquals(searchResponse, result.getContent().get(0));
        verify(userRepository).searchByUserWithoutDate(isNull(), isNull(), isNull(), eq(pageable));
        verify(userRepository).findUsersWithDetailsByIds(Collections.singletonList(1L));
        verify(userRepository).fetchEmailsByIds(Collections.singletonList(1L));
        verify(userRepository).fetchPhonesByIds(Collections.singletonList(1L));
        verify(userMapper).mapToSearchUserDtoResponse(eq(user), eq(0), eq(10));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void searchUser_emptyResult() {
        searchUserDto.setDateOfBirth("2000-01-01");
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.searchByUserWithDate(isNull(), isNull(), isNull(), any(LocalDate.class), eq(pageable)))
                .thenReturn(emptyPage);

        Page<SearchUserDtoResponse> result = userService.searchUser(searchUserDto);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(userRepository).searchByUserWithDate(isNull(), isNull(), isNull(), any(LocalDate.class), eq(pageable));
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void searchUser_negativePageAndSize_defaultsToZeroAndTen() {
        searchUserDto.setPage(-1);
        searchUserDto.setSize(-5);
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);
        SearchUserDtoResponse searchResponse = new SearchUserDtoResponse();

        when(userRepository.searchByUserWithoutDate(isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(userPage);
        when(userRepository.findUsersWithDetailsByIds(Collections.singletonList(1L))).thenReturn(users);
        when(userMapper.mapToSearchUserDtoResponse(eq(user), eq(0), eq(10))).thenReturn(searchResponse);

        Page<SearchUserDtoResponse> result = userService.searchUser(searchUserDto);

        assertEquals(1, result.getTotalElements());
        assertEquals(searchResponse, result.getContent().get(0));
        verify(userRepository).searchByUserWithoutDate(isNull(), isNull(), isNull(), eq(pageable));
        verify(userRepository).findUsersWithDetailsByIds(Collections.singletonList(1L));
        verify(userRepository).fetchEmailsByIds(Collections.singletonList(1L));
        verify(userRepository).fetchPhonesByIds(Collections.singletonList(1L));
        verify(userMapper).mapToSearchUserDtoResponse(eq(user), eq(0), eq(10));
    }
}