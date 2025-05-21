package com.example.pioneerpixel.security;

import com.example.pioneerpixel.entity.EmailData;
import com.example.pioneerpixel.entity.User;
import com.example.pioneerpixel.repositroy.EmailDataRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final EmailDataRepository emailDataRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<EmailData> emailDataOptional = emailDataRepository.findByEmail(username);

    if (emailDataOptional.isEmpty()) {
      throw new UsernameNotFoundException("User with email " + username + " not found");
    }

    EmailData emailData = emailDataOptional.get();
    User user = emailData.getUser();

    return new CurrentUser(user, emailData.getEmail());
  }
}
