package com.catchtwobirds.soboro.user.controller;

import com.catchtwobirds.soboro.auth.dto.SignUpRequest;
import com.catchtwobirds.soboro.common.ApiResponse;
import com.catchtwobirds.soboro.user.dto.UserDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public void addUser(@RequestBody UserDto userDto) {
        System.out.println(userDto);
        userService.insertUser(userDto);
    }

    @GetMapping
    public ApiResponse<?> getUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("getUser principal : {}", principal);
        User user = userService.getUser(principal.getUsername());
        log.info("getUser user : {}", user);
        return ApiResponse.success("user", user);
    }


//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
//        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
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
