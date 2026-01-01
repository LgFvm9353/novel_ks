package org.example.novel_backened.auth;

import org.example.novel_backened.common.ApiResponse;
import org.example.novel_backened.user.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<UserDto> register(@RequestBody RegisterRequest request) {
        try {
            UserDto user = userService.register(request.getUsername(), request.getPassword());
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request.getUsername(), request.getPassword());
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ApiResponse<UserDto> me(@RequestAttribute("userId") String userId) {
        UserDto user = userService.getCurrentUser(userId);
        return ApiResponse.success(user);
    }
}

