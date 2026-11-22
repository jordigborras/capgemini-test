package com.capgemini.test.code.users;

import com.capgemini.test.code.rooms.Room;
import com.capgemini.test.code.rooms.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void existsByEmail_shouldReturnTrue() {
        User user = new User();
        user.setName("Carlos");
        user.setEmail("carlos@test.com");
        user.setDni("1234567A");
        user.setPhone("600000000");
        user.setRole("admin");

        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("carlos@test.com"));
    }

    @Test
    void existsByEmail_shouldReturnFalse() {
        assertFalse(userRepository.existsByEmail("carlos@test.com"));
    }

    @Test
    void findByIdAndRoomId_shouldReturnUser() {
        Room room = new Room();
        room.setName("Sala 1");

        User user = new User();
        user.setName("Carlos");
        user.setEmail("carlos@test.com");
        user.setDni("1234567A");
        user.setPhone("600000000");
        user.setRole("admin");
        user.setRoom(room);

        roomRepository.save(room);
        User savedUser = userRepository.save(user);

        Optional<User> result = userRepository.findByIdAndRoomId(savedUser.getId(), room.getId());

        assertTrue(result.isPresent());
        assertEquals("carlos@test.com", result.get().getEmail());
    }

    @Test
    void findByIdAndRoomId_shouldReturnEmpty() {
        Room room1 = new Room();
        room1.setName("Sala 1");
        roomRepository.save(room1);

        Room room2 = new Room();
        room2.setName("Sala 2");
        roomRepository.save(room2);

        User user = new User();
        user.setName("Carlos");
        user.setEmail("carlos@test.com");
        user.setDni("1234567A");
        user.setPhone("600000000");
        user.setRole("admin");
        user.setRoom(room1);

        User _user = userRepository.save(user);

        Optional<User> result = userRepository.findByIdAndRoomId(_user.getId(), room2.getId());

        assertFalse(result.isPresent());
    }
}