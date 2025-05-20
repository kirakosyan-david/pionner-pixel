package com.example.pioneerpixel.service.impl;

import com.example.pioneerpixel.dto.UserPhoneDtoRequest;
import com.example.pioneerpixel.dto.UserPhoneDtoResponse;
import com.example.pioneerpixel.entity.PhoneData;
import com.example.pioneerpixel.entity.User;
import com.example.pioneerpixel.exception.custom.PhoneAndEmailOperationException;
import com.example.pioneerpixel.exception.custom.UserNotFoundException;
import com.example.pioneerpixel.mapper.PhoneDataMapper;
import com.example.pioneerpixel.repositroy.PhoneDataRepository;
import com.example.pioneerpixel.repositroy.UserRepository;
import com.example.pioneerpixel.service.PhoneDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneDataServiceImpl implements PhoneDataService {

    private final UserRepository userRepository;
    private final PhoneDataRepository phoneDataRepository;
    private final PhoneDataMapper phoneDataMapper;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "phones", key = "#userId")
    public UserPhoneDtoResponse addPhoneOrUpdate(Long userId, UserPhoneDtoRequest phone) {
        if (phone == null || phone.getPhone() == null || phone.getPhone().trim().isEmpty()) {
            throw new PhoneAndEmailOperationException("Phone cannot be null or empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<PhoneData> existingPhone = phoneDataRepository.findByPhone(phone.getPhone());
        if (existingPhone.isPresent() && existingPhone.get().getUser().getId().equals(userId)) {
            throw new PhoneAndEmailOperationException("Phone already in use by another user");
        }

        PhoneData phoneData = phoneDataRepository.findByUserId(userId)
                .map(p -> {
                    p.setPhone(phone.getPhone());
                    return p;
                })
                .orElseGet(() -> PhoneData.builder()
                        .user(user)
                        .phone(phone.getPhone())
                        .build());

        PhoneData savePhone = phoneDataRepository.save(phoneData);
        return phoneDataMapper.mapToUserPhoneDtoResponse(savePhone);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = "phones", key = "#userId")
    public void deletePhone(Long userId, UserPhoneDtoRequest phone) {
        if (phone == null || phone.getPhone() == null || phone.getPhone().trim().isEmpty()) {
            throw new PhoneAndEmailOperationException("Phone cannot be null or empty");
        }

        PhoneData phoneData = phoneDataRepository.findByPhoneAndUserId(phone.getPhone(), userId)
                .orElseThrow(() -> new PhoneAndEmailOperationException("User not found"));

        Long phoneCount = phoneDataRepository.countByUserId(userId);
        if (phoneCount <= 1) {
            throw new PhoneAndEmailOperationException("User must have at least one email");
        }
        phoneDataRepository.delete(phoneData);
    }
}
