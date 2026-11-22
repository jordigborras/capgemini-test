package com.capgemini.test.code.users;

import com.capgemini.test.code.rooms.RoomResponse;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String dni;
    private String phone;
    private String role;
    private RoomResponse room;
}