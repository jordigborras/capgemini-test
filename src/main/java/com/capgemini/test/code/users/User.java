package com.capgemini.test.code.users;

import com.capgemini.test.code.rooms.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 15)
    private String dni;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(nullable = false, length = 50)
    private String role;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}