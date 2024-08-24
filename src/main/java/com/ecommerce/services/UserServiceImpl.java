package com.ecommerce.services;

import com.ecommerce.dto.UserDto;
import com.ecommerce.models.AppRole;
import com.ecommerce.models.Role;
import com.ecommerce.models.User;
import com.ecommerce.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepo;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    public UserServiceImpl(UserRepository userRepo, ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(encoder.encode(userDto.getPassword()));
        Role role = new Role(AppRole.ROLE_ADMIN);
        user.getRoles().add(role);
        userRepo.save(user);
        return modelMapper.map(userRepo.save(user), UserDto.class);
    }
}
