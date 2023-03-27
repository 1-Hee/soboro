package com.catchtwobirds.soboro.user.service;

import com.catchtwobirds.soboro.common.error.errorcode.UserErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.catchtwobirds.soboro.user.dto.UserModifyDto;
import com.catchtwobirds.soboro.user.dto.UserRequestDto;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    // 회원 정보 가져오기
    public UserResponseDto getUser(String userId) {
        Optional<User> result = userRepository.findByUserId(userId);
        return result.map(UserResponseDto::new).orElse(null);
    }

    // 회원 가입
    @Transactional
    public UserResponseDto insertUser(UserRequestDto userDto) {
        userDto.setUserActive(true);
        userDto.setUserTerms(true);
        return new UserResponseDto(userRepository.save(userDto.toEntity()));
    }
    
    // 회원 수정
    @Transactional
    public UserModifyDto modifyUser(UserModifyDto userModifyDto) {
        User user = userRepository.findByUserId(userModifyDto.getUserId()).orElseThrow(()-> new RestApiException(UserErrorCode.USER_402));
        user.setUserName(userModifyDto.getUserName());
        user.setUserEmail(userModifyDto.getUserEmail());
        user.setUserPhone(userModifyDto.getUserPhone());
        return new UserModifyDto(user);
    }
    
    // 회원 삭제
    @Transactional
    public boolean deleteUser(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(()-> new RestApiException(UserErrorCode.USER_402));
        try {
            user.setUserActive(false);
        } catch (RestApiException e) {
            throw new RestApiException(UserErrorCode.USER_501);
        }
        return true;
    }

}
