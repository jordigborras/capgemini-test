package com.capgemini.test.code.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return userMapper.toResponse(user);
    }

    @PostMapping
    public ResponseEntity<Map<String, Long>> createUser(@Valid @RequestBody UserRequest req) {
        User user = userService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", user.getId()));
    }
}