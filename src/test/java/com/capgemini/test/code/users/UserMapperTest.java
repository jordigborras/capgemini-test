package com.capgemini.test.code.users;

import com.capgemini.test.code.rooms.Room;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toResponse_shouldMapUserFieldsAndRoom() {
        Room room = new Room();
        room.setId(1L);
        room.setName("Room 1");

        User user = new User();
        user.setId(10L);
        user.setName("Carlos");
        user.setEmail("carlos@test.com");
        user.setDni("1234567A");
        user.setPhone("600000000");
        user.setRole("admin");
        user.setRoom(room);

        UserResponse response = userMapper.toResponse(user);

        assertEquals(user.getId(), response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getDni(), response.getDni());
        assertEquals(user.getPhone(), response.getPhone());
        assertEquals(user.getRole(), response.getRole());
        assertEquals(room.getId(), response.getRoom().getId());
        assertEquals(room.getName(), response.getRoom().getName());
    }
}