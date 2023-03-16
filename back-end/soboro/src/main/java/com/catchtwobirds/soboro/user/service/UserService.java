package com.catchtwobirds.soboro.user.service;

import com.catchtwobirds.soboro.user.dto.UserDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    public void insertUser(UserDto userDto) {
        userRepository.save(userDto.toEntity());
    }


}
