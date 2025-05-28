package com.hospital.repository;

import com.hospital.entity.Message;
import com.hospital.entity.User;
import com.hospital.entity.Doctor;
import com.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserOrderBySendTimeDesc(User user);
    List<Message> findByDoctorOrderBySendTimeDesc(Doctor doctor);
    List<Message> findByAppointmentOrderBySendTimeAsc(Appointment appointment);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.doctor = ?1 AND m.isRead = false AND m.sender = 'user'")
    Long countUnreadMessagesByDoctor(Doctor doctor);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.user = ?1 AND m.isRead = false AND m.sender = 'doctor'")
    Long countUnreadMessagesByUser(User user);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.doctor = ?1 AND m.appointment = ?2 AND m.isRead = false AND m.sender = 'user'")
    Long countUnreadMessagesByDoctorAndAppointment(Doctor doctor, Appointment appointment);
    
    @Query("SELECT m FROM Message m WHERE m.doctor = ?1 AND m.appointment = ?2 AND m.isRead = false AND m.sender = 'user'")
    List<Message> findUnreadMessagesByDoctorAndAppointment(Doctor doctor, Appointment appointment);
    
    @Query("SELECT m FROM Message m WHERE m.user = ?1 AND m.appointment = ?2 AND m.isRead = false AND m.sender = 'doctor'")
    List<Message> findUnreadMessagesByUserAndAppointment(User user, Appointment appointment);
}