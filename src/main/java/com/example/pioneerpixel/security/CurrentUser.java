package com.example.pioneerpixel.security;

import com.example.pioneerpixel.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private final User user;
    private final String email;

    public CurrentUser(User user, String email) {
        super(email,
                user.getPassword(),
                true,
                true,
                true,
                true,
                AuthorityUtils.NO_AUTHORITIES);
        this.user = user;
        this.email = email;
    }

    @Override
    public String getUsername() {
        return email;
    }

}