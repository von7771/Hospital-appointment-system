package com.hospital.entity;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String phone;
    private String role; // "user" 或 "admin" 或 "doctor"
    
    @OneToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor; // 如果用户是医生，关联到医生实体
}