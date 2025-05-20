package com.example.pioneerpixel.service;

import com.example.pioneerpixel.BaseUnitTest;
import com.example.pioneerpixel.dto.UserPhoneDtoRequest;
import com.example.pioneerpixel.dto.UserPhoneDtoResponse;
import com.example.pioneerpixel.entity.PhoneData;
import com.example.pioneerpixel.entity.User;
import com.example.pioneerpixel.exception.custom.PhoneAndEmailOperationException;
import com.example.pioneerpixel.exception.custom.UserNotFoundException;
import com.example.pioneerpixel.mapper.PhoneDataMapper;
import com.example.pioneerpixel.repositroy.PhoneDataRepository;
import com.example.pioneerpixel.repositroy.UserRepository;
import com.example.pioneerpixel.service.impl.PhoneDataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PhoneDataServiceTest extends BaseUnitTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PhoneDataRepository phoneDataRepository;

    @Mock
    private PhoneDataMapper phoneDataMapper;

    @InjectMocks
    private PhoneDataServiceImpl phoneDataService;

    private User user;
    private PhoneData phoneData;
    private UserPhoneDtoRequest phoneDtoRequest;
    private UserPhoneDtoResponse phoneDtoResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        phoneData = PhoneData.builder()
                .user(user)
                .phone("+1234567890")
                .build();

        phoneDtoRequest = new UserPhoneDtoRequest();
        phoneDtoRequest.setPhone("+1234567890");

        phoneDtoResponse = new UserPhoneDtoResponse();
        phoneDtoResponse.setPhone("+1234567890");
    }

    @Test
    void addPhoneOrUpdate_addNewPhone_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(phoneDataRepository.findByPhone("+1234567890")).thenReturn(Optional.empty());
        when(phoneDataRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(phoneDataRepository.save(any(PhoneData.class))).thenReturn(phoneData);
        when(phoneDataMapper.mapToUserPhoneDtoResponse(phoneData)).thenReturn(phoneDtoResponse);

        UserPhoneDtoResponse result = phoneDataService.addPhoneOrUpdate(1L, phoneDtoRequest);

        assertEquals(phoneDtoResponse, result);
        verify(userRepository).findById(1L);
        verify(phoneDataRepository).findByPhone("+1234567890");
        verify(phoneDataRepository).findByUserId(1L);
        verify(phoneDataRepository).save(any(PhoneData.class));
        verify(phoneDataMapper).mapToUserPhoneDtoResponse(phoneData);
    }

    @Test
    void addPhoneOrUpdate_updateExistingPhone_success() {
        PhoneData existingPhone = PhoneData.builder()
                .user(user)
                .phone("+0987654321")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(phoneDataRepository.findByPhone("+1234567890")).thenReturn(Optional.empty());
        when(phoneDataRepository.findByUserId(1L)).thenReturn(Optional.of(existingPhone));
        when(phoneDataRepository.save(any(PhoneData.class))).thenReturn(phoneData);
        when(phoneDataMapper.mapToUserPhoneDtoResponse(phoneData)).thenReturn(phoneDtoResponse);

        UserPhoneDtoResponse result = phoneDataService.addPhoneOrUpdate(1L, phoneDtoRequest);

        assertEquals(phoneDtoResponse, result);
        verify(userRepository).findById(1L);
        verify(phoneDataRepository).findByPhone("+1234567890");
        verify(phoneDataRepository).findByUserId(1L);
        verify(phoneDataRepository).save(existingPhone);
        verify(phoneDataMapper).mapToUserPhoneDtoResponse(phoneData);
    }

    @Test
    void addPhoneOrUpdate_nullPhone_throwsException() {
        phoneDtoRequest.setPhone(null);

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> phoneDataService.addPhoneOrUpdate(1L, phoneDtoRequest)
        );

        assertEquals("Phone cannot be null or empty", exception.getMessage());
        verifyNoInteractions(userRepository, phoneDataRepository, phoneDataMapper);
    }

    @Test
    void addPhoneOrUpdate_emptyPhone_throwsException() {
        phoneDtoRequest.setPhone("");

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> phoneDataService.addPhoneOrUpdate(1L, phoneDtoRequest)
        );

        assertEquals("Phone cannot be null or empty", exception.getMessage());
        verifyNoInteractions(userRepository, phoneDataRepository, phoneDataMapper);
    }

    @Test
    void addPhoneOrUpdate_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> phoneDataService.addPhoneOrUpdate(1L, phoneDtoRequest)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verifyNoInteractions(phoneDataRepository, phoneDataMapper);
    }

    @Test
    void deletePhone_success() {
        when(phoneDataRepository.findByPhoneAndUserId("+1234567890", 1L)).thenReturn(Optional.of(phoneData));
        when(phoneDataRepository.countByUserId(1L)).thenReturn(2L);

        phoneDataService.deletePhone(1L, phoneDtoRequest);

        verify(phoneDataRepository).findByPhoneAndUserId("+1234567890", 1L);
        verify(phoneDataRepository).countByUserId(1L);
        verify(phoneDataRepository).delete(phoneData);
        verifyNoInteractions(userRepository, phoneDataMapper);
    }

    @Test
    void deletePhone_nullPhone_throwsException() {
        phoneDtoRequest.setPhone(null);

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> phoneDataService.deletePhone(1L, phoneDtoRequest)
        );

        assertEquals("Phone cannot be null or empty", exception.getMessage());
        verifyNoInteractions(userRepository, phoneDataRepository, phoneDataMapper);
    }

    @Test
    void deletePhone_emptyPhone_throwsException() {
        phoneDtoRequest.setPhone("");

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> phoneDataService.deletePhone(1L, phoneDtoRequest)
        );

        assertEquals("Phone cannot be null or empty", exception.getMessage());
        verifyNoInteractions(userRepository, phoneDataRepository, phoneDataMapper);
    }

}