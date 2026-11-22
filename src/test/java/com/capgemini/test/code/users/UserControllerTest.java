package com.capgemini.test.code.users;

import com.capgemini.test.code.exceptions.EmailAlreadyExistsException;
import com.capgemini.test.code.exceptions.InvalidDniException;
import com.capgemini.test.code.exceptions.RoomNotFoundException;
import com.capgemini.test.code.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Carlos");
        user.setEmail("carlos@test.com");

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Carlos");
        response.setEmail("carlos@test.com");

        when(userService.getUserById(1L)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@test.com"));
    }

    @Test
    void getUserById_shouldReturnNotFound() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("Carlos");
        request.setEmail("carlos@test.com");
        request.setDni("1234567A");
        request.setPhone("600000000");
        request.setRole("admin");

        User user = new User();
        user.setId(1L);

        String body = """
            {
                "name": "Carlos",
                "email": "carlos@test.com",
                "dni": "1234567A",
                "phone": "600000000",
                "role": "admin"
            }
        """;

        when(userService.createUser(any(UserRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users").contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createUser_shouldReturnEmailValidationConflict() throws Exception {
        String body = """
            {
                "name": "Carlos",
                "email": "123",
                "dni": "1234567A",
                "phone": "600000000",
                "role": "admin"
            }
        """;

        mockMvc.perform(post("/users").contentType("application/json").content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("error validation email"));
    }

    @Test
    void createUser_shouldReturnAlreadyExistingEmailConflict() throws Exception {
        String body = """
            {
                "name": "Carlos",
                "email": "carlos@test.com",
                "dni": "1234567A",
                "phone": "600000000",
                "role": "admin"
            }
        """;

        when(userService.createUser(any(UserRequest.class))).thenThrow(new EmailAlreadyExistsException());

        mockMvc.perform(post("/users").contentType("application/json").content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void createUser_shouldReturnInvalidDniConflict() throws Exception {
        String body = """
            {
                "name": "Carlos",
                "email": "carlos@test.com",
                "dni": "99999999w",
                "phone": "600000000",
                "role": "admin"
            }
        """;

        when(userService.createUser(any(UserRequest.class))).thenThrow(new InvalidDniException());

        mockMvc.perform(post("/users").contentType("application/json").content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void createUser_shouldReturnNameValidationConflict() throws Exception {
        String body = """
            {
                "name": "Carlos Manuel",
                "email": "carlos@test.com",
                "dni": "1234567A",
                "phone": "600000000",
                "role": "admin"
            }
        """;

        mockMvc.perform(post("/users").contentType("application/json").content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("error validation name"));
    }

    @Test
    void createUser_shouldReturnInternalServerError() throws Exception {
        String body = """
            {
                "name": "Carlos",
                "email": "carlos@test.com",
                "dni": "1234567A",
                "phone": "600000000",
                "role": "admin"
            }
        """;

        when(userService.createUser(any(UserRequest.class))).thenThrow(new RoomNotFoundException());

        mockMvc.perform(post("/users").contentType("application/json").content(body))
                .andExpect(status().isInternalServerError());
    }
}