package com.capgemini.test.code.users;

import com.capgemini.test.code.rooms.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAcceptanceTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @AfterEach
    void cleanUp() {
        userRepository.findByEmail("test@test.com").ifPresent(userRepository::delete);
    }

    @Test
    void createAdminUser_shouldReturnCreatedUser() {
        String body = """
            {
                "name": "123",
                "email": "test@test.com",
                "dni": "0",
                "phone": "0",
                "role": "admin"
            }
        """;

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/users",
                new HttpEntity<>(body, createJsonHeaders()),
                String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"id\""));
    }

    @Test
    void createUserWithInvalidDni_shouldReturnConflict() {
        String body = """
            {
                "name": "123",
                "email": "test@test.com",
                "dni": "99999999w",
                "phone": "600000000",
                "role": "admin"
            }
        """;

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/users",
                new HttpEntity<>(body, createJsonHeaders()),
                String.class
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("error validation dni"));
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setName("123");
        user.setEmail("test@test.com");
        user.setDni("0");
        user.setPhone("0");
        user.setRole("admin");
        user.setRoom(roomRepository.findById(1L).orElseThrow());

        User savedUser = userRepository.save(user);

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/users/" + savedUser.getId(),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("123"));
    }

    @Test
    void createSuperAdminUser_shouldReturnCreatedUser() {
        String body = """
            {
                "name": "123",
                "email": "test@test.com",
                "dni": "0",
                "phone": "0",
                "role": "superadmin"
            }
        """;

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/users",
                new HttpEntity<>(body, createJsonHeaders()),
                String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"id\""));
    }
}