package com.ecommerce.utils;

import com.ecommerce.models.User;
import com.ecommerce.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final UserRepository userRepo;
    private final UserRepository userRepository;

    public AuthUtil(UserRepository userRepo, UserRepository userRepository) {
        this.userRepo = userRepo;
        this.userRepository = userRepository;
    }

    public String loggedInEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found " + authentication.getName()));

        return user.getEmail();
    }

    public Long loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found " + authentication.getName()));

        return user.getUserId();
    }

    public User loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found " + authentication.getName()));
    }

}
