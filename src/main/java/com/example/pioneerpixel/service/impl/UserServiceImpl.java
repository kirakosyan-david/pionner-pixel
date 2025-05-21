package com.example.pioneerpixel.service.impl;

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
import com.example.pioneerpixel.service.UserService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final EmailDataRepository emailDataRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
  public boolean userLogin(UserLoginRequestDto loginRequestDto) {
    Optional<EmailData> emailDataOpt = emailDataRepository.findByEmail(loginRequestDto.getEmail());
    if (emailDataOpt.isEmpty()) {
      log.error("User email is incorrect: {}", loginRequestDto.getEmail());
      return false;
    }

    User user = emailDataOpt.get().getUser();
    if (!Objects.equals(user.getPassword(), loginRequestDto.getPassword())) {
      log.error("Invalid password for user: {}", loginRequestDto.getEmail());
      return false;
    }
    return true;
  }

  @Override
  @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
  @Cacheable(value = "users", key = "#userId", unless = "#result == null")
  public UserDtoResponse getUserById(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    return userMapper.mapToUserDtoResponse(user);
  }

  @Override
  @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
  @Cacheable(value = "userContacts", key = "#id", unless = "#result == null")
  public UserContactDtoResponse getUserWithContact(Long id) {
    User user =
        userRepository
            .findByIdWithEmailsAndAccount(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    userRepository.findByIdWithPhones(id).ifPresent(u -> user.getPhones().addAll(u.getPhones()));
    return userMapper.mapToUserContactDtoResponse(user);
  }

  @Override
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Page<SearchUserDtoResponse> searchUser(SearchUserDto searchUserDto) {
    LocalDate dateOfBirth = null;
    if (searchUserDto.getDateOfBirth() != null) {
      try {
        dateOfBirth =
            LocalDate.parse(searchUserDto.getDateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
      } catch (Exception e) {
        log.warn("Failed to parse dateOfBirth: {}", searchUserDto.getDateOfBirth(), e);
      }
    }

    int page = searchUserDto.getPage() >= 0 ? searchUserDto.getPage() : 0;
    int size = searchUserDto.getSize() > 0 ? searchUserDto.getSize() : 10;
    Pageable pageable = PageRequest.of(page, size);

    Page<User> userPage;
    if (dateOfBirth != null) {
      userPage =
          userRepository.searchByUserWithDate(
              searchUserDto.getName(),
              searchUserDto.getPhone(),
              searchUserDto.getEmail(),
              dateOfBirth,
              pageable);
    } else {
      userPage =
          userRepository.searchByUserWithoutDate(
              searchUserDto.getName(),
              searchUserDto.getPhone(),
              searchUserDto.getEmail(),
              pageable);
    }

    if (page >= userPage.getTotalPages() && userPage.getTotalElements() > 0) {
      log.info(
          "Requested page {} is out of bounds (total pages: {}). Resetting to page 0.",
          page,
          userPage.getTotalPages());
      page = 0;
      pageable = PageRequest.of(page, size);
      if (dateOfBirth != null) {
        userPage =
            userRepository.searchByUserWithDate(
                searchUserDto.getName(),
                searchUserDto.getPhone(),
                searchUserDto.getEmail(),
                dateOfBirth,
                pageable);
      } else {
        userPage =
            userRepository.searchByUserWithoutDate(
                searchUserDto.getName(),
                searchUserDto.getPhone(),
                searchUserDto.getEmail(),
                pageable);
      }
    }

    List<Long> userIds =
        userPage.getContent().stream().map(User::getId).collect(Collectors.toList());
    if (!userIds.isEmpty()) {
      List<User> usersWithDetails = userRepository.findUsersWithDetailsByIds(userIds);
      userRepository.fetchEmailsByIds(userIds);
      userRepository.fetchPhonesByIds(userIds);
      userPage = new PageImpl<>(usersWithDetails, pageable, userPage.getTotalElements());
    }

    int finalPage = page;
    return userPage.map(user -> userMapper.mapToSearchUserDtoResponse(user, finalPage, size));
  }
}
