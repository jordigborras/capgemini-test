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

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Long>> createUser(@Valid @RequestBody UserRequest req) {
        User user = userService.createUser(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", user.getId()));
    }
}