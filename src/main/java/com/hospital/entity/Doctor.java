package com.hospital.entity;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String department;
    private String title;
    private String description;
}