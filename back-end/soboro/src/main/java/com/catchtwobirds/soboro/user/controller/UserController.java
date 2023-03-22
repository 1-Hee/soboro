package com.catchtwobirds.soboro.user.controller;

import com.catchtwobirds.soboro.common.ApiResponseDto;
import com.catchtwobirds.soboro.user.dto.UserDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "user", description = "회원 관련 컨트롤러")
public class UserController {
    private final UserService userService;

//    @PostMapping
//    public void addUser(@RequestBody UserDto userDto) {
//        System.out.println(userDto);
//        userService.insertUser(userDto);
//    }

    @GetMapping
    @Operation(summary = "회원 정보 반환", description = "회원 정보를 반환한다. (ADMIN 요청)", tags = {"user"})
    public ApiResponseDto<?> getUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("getUser principal : {}", principal);
        User user = userService.getUser(principal.getUsername());
        log.info("getUser user : {}", user);
        return ApiResponseDto.success("user", user);
    }


//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
//        if(userService.getUser(signUpRequest.getUserId())!= null) {
//            throw new BadRequestException("Email address already in use.");
//        }
//
//        // Creating user's account
//        User user = new User();
//        user.setName(signUpRequest.getName());
//        user.setEmail(signUpRequest.getEmail());
//        user.setPassword(signUpRequest.getPassword());
//        user.setProvider(AuthProvider.local);
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        User result = userRepository.save(user);
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentContextPath().path("/user/me")
//                .buildAndExpand(result.getId()).toUri();
//
//        return ResponseEntity.created(location)
//                .body(new ApiResponse(true, "User registered successfully@"));
//    }
}
