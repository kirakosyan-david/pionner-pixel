package com.example.pioneerpixel.service.impl;

import com.example.pioneerpixel.dto.UserEmailDtoRequest;
import com.example.pioneerpixel.dto.UserEmailDtoResponse;
import com.example.pioneerpixel.entity.EmailData;
import com.example.pioneerpixel.entity.User;
import com.example.pioneerpixel.exception.custom.PhoneAndEmailOperationException;
import com.example.pioneerpixel.mapper.EmailDataMapper;
import com.example.pioneerpixel.repositroy.EmailDataRepository;
import com.example.pioneerpixel.repositroy.UserRepository;
import com.example.pioneerpixel.service.EmailDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailDataServiceImpl implements EmailDataService {

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final EmailDataMapper emailDataMapper;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "emails", key = "#userId")
    public UserEmailDtoResponse addEmailOrUpdate(Long userId, UserEmailDtoRequest email) {
        if (email == null || email.getEmail() == null || email.getEmail().trim().isEmpty()) {
            throw new PhoneAndEmailOperationException("Email cannot be null or empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PhoneAndEmailOperationException("User not found with id: " + userId));

        Optional<EmailData> existingEmail = emailDataRepository.findByEmail(email.getEmail());
        if (existingEmail.isPresent() && !existingEmail.get().getUser().getId().equals(userId)) {
            throw new PhoneAndEmailOperationException("Email already in use by another user");
        }

        EmailData emailData = emailDataRepository.findByUserId(userId)
                .map(e -> {
                    e.setEmail(email.getEmail());
                    return e;
                })
                .orElseGet(() -> EmailData.builder()
                        .user(user)
                        .email(email.getEmail())
                        .build());

        EmailData saveEmail = emailDataRepository.save(emailData);
        return emailDataMapper.mapToUserPhoneDtoResponse(saveEmail);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = "emails", key = "#userId")
    public void deleteEmail(Long userId, UserEmailDtoRequest email) {

        if (email == null || email.getEmail() == null || email.getEmail().trim().isEmpty()) {
            throw new PhoneAndEmailOperationException("Email cannot be null or empty");
        }

        EmailData emailData = emailDataRepository.findByEmailAndUserId(email.getEmail(), userId)
                .orElseThrow(() -> new PhoneAndEmailOperationException("User not found"));

        Long emailCount = emailDataRepository.countByUserId(userId);
        if (emailCount <= 1) {
            throw new PhoneAndEmailOperationException("User must have at least one email");
        }
        emailDataRepository.delete(emailData);
    }
}
