package com.capgemini.test.code.users;

import com.capgemini.test.code.clients.*;
import com.capgemini.test.code.exceptions.EmailAlreadyExistsException;
import com.capgemini.test.code.exceptions.InvalidDniException;
import com.capgemini.test.code.exceptions.RoomNotFoundException;
import com.capgemini.test.code.exceptions.UserNotFoundException;
import com.capgemini.test.code.rooms.Room;
import com.capgemini.test.code.rooms.RoomRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private DniClient dniClient;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldSaveUserAndSendEmail() {
        UserRequest request = new UserRequest();
        request.setName("Carlos");
        request.setEmail("carlos@test.com");
        request.setDni("12345678A");
        request.setPhone("600000000");
        request.setRole("admin");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setRole("admin");

        Room room = new Room();
        room.setId(1L);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(request);

        verify(dniClient).check(any(CheckDniRequest.class));
        verify(userRepository).save(user);
        verify(notificationClient).sendEmail(any(EmailNotificationRequest.class));
        verify(notificationClient, never()).sendSms(any(SmsNotificationRequest.class));

        assertSame(user, result);
    }

    @Test
    void createUser_shouldSaveUserAndSendSms() {
        UserRequest request = new UserRequest();
        request.setName("Carlos");
        request.setEmail("carlos@test.com");
        request.setDni("12345678A");
        request.setPhone("600000000");
        request.setRole("superadmin");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setRole("superadmin");

        Room room = new Room();
        room.setId(1L);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(request);

        verify(dniClient).check(any(CheckDniRequest.class));
        verify(userRepository).save(user);
        verify(notificationClient).sendSms(any(SmsNotificationRequest.class));
        verify(notificationClient, never()).sendEmail(any(EmailNotificationRequest.class));

        assertSame(user, result);
    }

    @Test
    void createUser_shouldThrowEmailAlreadyExistsException() {
        UserRequest request = new UserRequest();
        request.setEmail("carlos@test.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));

        verify(dniClient, never()).check(any(CheckDniRequest.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowFeignException() {
        UserRequest request = new UserRequest();
        request.setEmail("carlos@test.com");
        request.setDni("99999999w");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(dniClient.check(any(CheckDniRequest.class))).thenThrow(mock(FeignException.Conflict.class));

        assertThrows(InvalidDniException.class, () -> userService.createUser(request));

        verify(userRepository, never()).save(any(User.class));
        verify(notificationClient, never()).sendEmail(any(EmailNotificationRequest.class));
        verify(notificationClient, never()).sendSms(any(SmsNotificationRequest.class));
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("carlos@test.com");

        when(userRepository.findByIdAndRoomId(1L, 1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertSame(user, result);
    }

    @Test
    void getUserById_shouldThrowUserNotFoundException() {
        when(userRepository.findByIdAndRoomId(999L, 1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void createUser_shouldThrowRoomNotFoundException() {
        UserRequest request = new UserRequest();
        request.setEmail("carlos@test.com");
        request.setDni("1234567A");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(new User());
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> userService.createUser(request));

        verify(userRepository, never()).save(any(User.class));
        verify(notificationClient, never()).sendEmail(any(EmailNotificationRequest.class));
        verify(notificationClient, never()).sendSms(any(SmsNotificationRequest.class));
    }
}