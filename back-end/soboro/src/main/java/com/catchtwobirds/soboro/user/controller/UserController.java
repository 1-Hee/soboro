package com.catchtwobirds.soboro.user.controller;

import com.catchtwobirds.soboro.user.dto.UserDto;
import com.catchtwobirds.soboro.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
}
