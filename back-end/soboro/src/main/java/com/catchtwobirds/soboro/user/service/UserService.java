package com.catchtwobirds.soboro.user.service;

import com.catchtwobirds.soboro.common.error.errorcode.UserErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
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

    // 회원 정보 가져오기 (아이디)
    public UserResponseDto getUser(String userId) {
        Optional<User> result = userRepository.findByUserId(userId);
        return result.map(UserResponseDto::new).orElse(null);
    }

    // 회원 정보 가져오기 (이메일)
    public UserResponseDto getUserByEmail(String email) {
        Optional<User> result = userRepository.findByUserEmail(email);
        return result.map(UserResponseDto::new).orElse(null);
    }

    // 회원 정보 가져오기 User 그대로
    public User getUserEntity(String userId) {
        return userRepository.findEntityByUserId(userId);
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
        if (userModifyDto.getUserName() != null) {
            user.setUserName(userModifyDto.getUserName());
        }
        if (userModifyDto.getUserEmail() != null) {
            user.setUserEmail(userModifyDto.getUserEmail());
        }
        if (userModifyDto.getUserPhone() != null) {
            user.setUserPhone(userModifyDto.getUserPhone());
        }
        user.setUserActive(true);
        return new UserModifyDto(user);
    }
    
    // 회원 삭제
    @Transactional
    public boolean deleteUser(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(()-> new RestApiException(UserErrorCode.USER_402));
        try {
            user.setUserActive(false);
            return true;
        } catch (RuntimeException e) {
            throw new RestApiException(UserErrorCode.USER_501);
        }
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(String userId, String userPassword) {
        Optional<User> result = userRepository.findByUserId(userId);
        if (result.isEmpty()) {
            throw new RestApiException(UserErrorCode.USER_402);
        }
        User getUser = result.get();
        getUser.setUserPassword(userPassword);
        result.map(UserResponseDto::new);
    }

}
