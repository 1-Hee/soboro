package com.catchtwobirds.soboro.user.service;

import com.catchtwobirds.soboro.common.error.errorcode.UserErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.user.dto.UserRequestDto;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 회원 정보 가져오기
    public UserResponseDto getUser(String userId) {
        Optional<User> result = userRepository.findByUserId(userId);
        return new UserResponseDto(result.orElseThrow(()->new RestApiException(UserErrorCode.USER_402)));
    }

    // 회원 ID 체크하기
//    public UserResponseDto getUserId(String userId) {
//        Optional<User> result = userRepository.findByUserId(userId);
//        return new UserResponseDto(result.orElseThrow(()->new RestApiResponse("회원 ID 없음")));
//    }

    // 회원 가입
    public UserResponseDto insertUser(UserRequestDto userDto) {
        return new UserResponseDto(userRepository.save(userDto.toEntity()));
    }


}
