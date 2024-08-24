package com.ecommerce.controllers;

import com.ecommerce.dto.UserDto;
import com.ecommerce.repositories.UserRepository;
import com.ecommerce.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/api/users/adduser")
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto){
        UserDto userDto1 = userService.addUser(userDto);
        return new ResponseEntity<>(userDto1, HttpStatus.CREATED);
    }


}
