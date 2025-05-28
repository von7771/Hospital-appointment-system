package com.hospital.repository;

import com.hospital.entity.Appointment;
import com.hospital.entity.User;
import com.hospital.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUser(User user);
    List<Appointment> findByDoctor(Doctor doctor);
    
    // 根据医生名称查询预约
    @Query("SELECT a FROM Appointment a JOIN a.doctor d WHERE d.name = :doctorName")
    List<Appointment> findByDoctorName(@Param("doctorName") String doctorName);
}