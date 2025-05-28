package com.hospital.controller;

import com.hospital.entity.Message;
import com.hospital.entity.User;
import com.hospital.entity.Doctor;
import com.hospital.entity.Appointment;
import com.hospital.service.MessageService;
import com.hospital.service.UserService;
import com.hospital.service.DoctorService;
import com.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/send")
    public Message sendMessage(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long doctorId = Long.valueOf(params.get("doctorId").toString());
        Long appointmentId = params.get("appointmentId") != null ? 
                Long.valueOf(params.get("appointmentId").toString()) : null;
        String content = params.get("content").toString();
        String sender = params.get("sender").toString();

        User user = userService.findById(userId);
        Doctor doctor = doctorService.findById(doctorId);
        Appointment appointment = appointmentId != null ? 
                appointmentService.findById(appointmentId) : null;

        return messageService.sendMessage(user, doctor, appointment, content, sender);
    }

    @GetMapping("/appointment/{appointmentId}")
    public List<Message> getMessagesByAppointment(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.findById(appointmentId);
        return messageService.getMessagesByAppointment(appointment);
    }

    @GetMapping("/user/{username}")
    public List<Message> getUserMessages(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return messageService.getUserMessages(user);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Message> getDoctorMessages(@PathVariable Long doctorId) {
        Doctor doctor = doctorService.findById(doctorId);
        return messageService.getDoctorMessages(doctor);
    }

    @GetMapping("/unread/user/{username}")
    public Long countUnreadUserMessages(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return messageService.countUnreadMessagesByUser(user);
    }

    @GetMapping("/unread/doctor/{doctorId}")
    public Long countUnreadDoctorMessages(@PathVariable Long doctorId) {
        Doctor doctor = doctorService.findById(doctorId);
        return messageService.countUnreadMessagesByDoctor(doctor);
    }

    @GetMapping("/unread/appointment/doctor/{appointmentId}")
    public Long countUnreadDoctorMessagesByAppointment(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.findById(appointmentId);
        return messageService.countUnreadMessagesByDoctorAndAppointment(appointment.getDoctor(), appointment);
    }

    @PutMapping("/markread/doctor/{appointmentId}")
    public void markDoctorMessagesAsRead(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.findById(appointmentId);
        messageService.markDoctorMessagesAsRead(appointment);
    }
    
    @PutMapping("/markread/user/{appointmentId}")
    public void markUserMessagesAsRead(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.findById(appointmentId);
        messageService.markUserMessagesAsRead(appointment);
    }

    @PutMapping("/read/{messageId}")
    public void markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
    }
}