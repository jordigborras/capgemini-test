package com.capgemini.test.code.users;

import com.capgemini.test.code.clients.*;
import com.capgemini.test.code.exceptions.EmailAlreadyExistsException;
import com.capgemini.test.code.exceptions.InvalidDniException;
import com.capgemini.test.code.exceptions.RoomNotFoundException;
import com.capgemini.test.code.exceptions.UserNotFoundException;
import com.capgemini.test.code.rooms.Room;
import com.capgemini.test.code.rooms.RoomRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final DniClient dniClient;
    private final NotificationClient notificationClient;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserMapper userMapper;

    public User getUserById(Long id) {
        return userRepository.findByIdAndRoomId(id, 1L).orElseThrow(UserNotFoundException::new);
    }

    public User createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        try {
            dniClient.check(new CheckDniRequest(request.getDni()));
        } catch (FeignException.Conflict e) {
            throw new InvalidDniException();
        }

        User user = userMapper.toEntity(request);
        Room room = roomRepository.findById(1L).orElseThrow(RoomNotFoundException::new);
        user.setRoom(room);
        User createdUser = userRepository.save(user);

        if ("admin".equalsIgnoreCase(createdUser.getRole())) {
            notificationClient.sendEmail(new EmailNotificationRequest(createdUser.getEmail(), "usuario guardado"));
        } else if ("superadmin".equalsIgnoreCase(createdUser.getRole())) {
            notificationClient.sendSms(new SmsNotificationRequest(createdUser.getPhone(), "usuario guardado"));
        }

        return createdUser;
    }
}