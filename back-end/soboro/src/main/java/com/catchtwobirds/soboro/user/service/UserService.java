package com.catchtwobirds.soboro.user.service;

import com.catchtwobirds.soboro.user.dto.UserRequestDto;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 회원 정보 가져오기
    public UserResponseDto getUser(String userId) {
        return new UserResponseDto(userRepository.findByUserId(userId));
    }

    // 회원 가입
    public UserResponseDto insertUser(UserRequestDto userDto) {
        return new UserResponseDto(userRepository.save(userDto.toEntity()));
    }


}
