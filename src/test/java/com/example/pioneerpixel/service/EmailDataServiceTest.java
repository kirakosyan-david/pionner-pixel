package com.example.pioneerpixel.service;

import com.example.pioneerpixel.BaseUnitTest;
import com.example.pioneerpixel.dto.UserEmailDtoRequest;
import com.example.pioneerpixel.dto.UserEmailDtoResponse;
import com.example.pioneerpixel.entity.EmailData;
import com.example.pioneerpixel.entity.User;
import com.example.pioneerpixel.exception.custom.PhoneAndEmailOperationException;
import com.example.pioneerpixel.mapper.EmailDataMapper;
import com.example.pioneerpixel.repositroy.EmailDataRepository;
import com.example.pioneerpixel.repositroy.UserRepository;
import com.example.pioneerpixel.service.impl.EmailDataServiceImpl;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class EmailDataServiceTest extends BaseUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailDataRepository emailDataRepository;

    @Mock
    private EmailDataMapper emailDataMapper;

    @InjectMocks
    private EmailDataServiceImpl emailDataService;

    private User user;
    private EmailData emailData;
    private UserEmailDtoRequest emailDtoRequest;
    private UserEmailDtoResponse emailDtoResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        emailData = EmailData.builder()
                .user(user)
                .email("test@example.com")
                .build();

        emailDtoRequest = new UserEmailDtoRequest();
        emailDtoRequest.setEmail("test@example.com");

        emailDtoResponse = new UserEmailDtoResponse();
        emailDtoResponse.setEmail("test@example.com");
    }

    @Test
    void addEmailOrUpdate_addNewEmail_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(emailDataRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(emailDataRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(emailDataRepository.save(any(EmailData.class))).thenReturn(emailData);
        when(emailDataMapper.mapToUserPhoneDtoResponse(emailData)).thenReturn(emailDtoResponse);

        UserEmailDtoResponse result = emailDataService.addEmailOrUpdate(1L, emailDtoRequest);

        assertEquals(emailDtoResponse, result);
        verify(userRepository).findById(1L);
        verify(emailDataRepository).findByEmail("test@example.com");
        verify(emailDataRepository).findByUserId(1L);
        verify(emailDataRepository).save(any(EmailData.class));
        verify(emailDataMapper).mapToUserPhoneDtoResponse(emailData);
    }

    @Test
    void addEmailOrUpdate_updateExistingEmail_success() {
        EmailData existingEmail = EmailData.builder()
                .user(user)
                .email("old@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(emailDataRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(emailDataRepository.findByUserId(1L)).thenReturn(Optional.of(existingEmail));
        when(emailDataRepository.save(any(EmailData.class))).thenReturn(emailData);
        when(emailDataMapper.mapToUserPhoneDtoResponse(emailData)).thenReturn(emailDtoResponse);

        UserEmailDtoResponse result = emailDataService.addEmailOrUpdate(1L, emailDtoRequest);

        assertEquals(emailDtoResponse, result);
        verify(userRepository).findById(1L);
        verify(emailDataRepository).findByEmail("test@example.com");
        verify(emailDataRepository).findByUserId(1L);
        verify(emailDataRepository).save(existingEmail);
        verify(emailDataMapper).mapToUserPhoneDtoResponse(emailData);
    }

    @Test
    void addEmailOrUpdate_nullEmail_throwsException() {
        emailDtoRequest.setEmail(null);

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> emailDataService.addEmailOrUpdate(1L, emailDtoRequest)
        );

        assertEquals("Email cannot be null or empty", exception.getMessage());
        verifyNoInteractions(userRepository, emailDataRepository, emailDataMapper);
    }

    @Test
    void addEmailOrUpdate_emptyEmail_throwsException() {
        emailDtoRequest.setEmail("");

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> emailDataService.addEmailOrUpdate(1L, emailDtoRequest)
        );

        assertEquals("Email cannot be null or empty", exception.getMessage());
        verifyNoInteractions(userRepository, emailDataRepository, emailDataMapper);
    }

    @Test
    void addEmailOrUpdate_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> emailDataService.addEmailOrUpdate(1L, emailDtoRequest)
        );

        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verifyNoInteractions(emailDataRepository, emailDataMapper);
    }

    @Test
    void addEmailOrUpdate_emailUsedByAnotherUser_throwsException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        EmailData existingEmail = EmailData.builder()
                .user(anotherUser)
                .email("test@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(emailDataRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingEmail));

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> emailDataService.addEmailOrUpdate(1L, emailDtoRequest)
        );

        assertEquals("Email already in use by another user", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(emailDataRepository).findByEmail("test@example.com");
        verifyNoMoreInteractions(emailDataRepository);
        verifyNoInteractions(emailDataMapper);
    }

    @Test
    void deleteEmail_success() {
        when(emailDataRepository.findByEmailAndUserId("test@example.com", 1L)).thenReturn(Optional.of(emailData));
        when(emailDataRepository.countByUserId(1L)).thenReturn(2L);

        emailDataService.deleteEmail(1L, emailDtoRequest);

        verify(emailDataRepository).findByEmailAndUserId("test@example.com", 1L);
        verify(emailDataRepository).countByUserId(1L);
        verify(emailDataRepository).delete(emailData);
        verifyNoInteractions(userRepository, emailDataMapper);
    }

    @Test
    void deleteEmail_nullEmail_throwsException() {
        emailDtoRequest.setEmail(null);

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> emailDataService.deleteEmail(1L, emailDtoRequest)
        );

        assertEquals("Email cannot be null or empty", exception.getMessage());
        verifyNoInteractions(userRepository, emailDataRepository, emailDataMapper);
    }

    @Test
    void deleteEmail_emptyEmail_throwsException() {
        emailDtoRequest.setEmail("");

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> emailDataService.deleteEmail(1L, emailDtoRequest)
        );

        assertEquals("Email cannot be null or empty", exception.getMessage());
        verifyNoInteractions(userRepository, emailDataRepository, emailDataMapper);
    }

    @Test
    void deleteEmail_emailNotFound_throwsException() {
        when(emailDataRepository.findByEmailAndUserId("test@example.com", 1L)).thenReturn(Optional.empty());

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> emailDataService.deleteEmail(1L, emailDtoRequest)
        );

        assertEquals("User not found", exception.getMessage());
        verify(emailDataRepository).findByEmailAndUserId("test@example.com", 1L);
        verifyNoMoreInteractions(emailDataRepository);
        verifyNoInteractions(userRepository, emailDataMapper);
    }

    @Test
    void deleteEmail_lastEmail_throwsException() {
        when(emailDataRepository.findByEmailAndUserId("test@example.com", 1L)).thenReturn(Optional.of(emailData));
        when(emailDataRepository.countByUserId(1L)).thenReturn(1L);

        PhoneAndEmailOperationException exception = assertThrows(
                PhoneAndEmailOperationException.class,
                () -> emailDataService.deleteEmail(1L, emailDtoRequest)
        );

        assertEquals("User must have at least one email", exception.getMessage());
        verify(emailDataRepository).findByEmailAndUserId("test@example.com", 1L);
        verify(emailDataRepository).countByUserId(1L);
        verifyNoMoreInteractions(emailDataRepository);
        verifyNoInteractions(userRepository, emailDataMapper);
    }

}