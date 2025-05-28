package com.hospital.service;

import com.hospital.entity.Message;
import com.hospital.entity.User;
import com.hospital.entity.Doctor;
import com.hospital.entity.Appointment;
import com.hospital.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(User user, Doctor doctor, Appointment appointment, String content, String sender) {
        Message message = new Message();
        message.setUser(user);
        message.setDoctor(doctor);
        message.setAppointment(appointment);
        message.setContent(content);
        message.setSender(sender);
        message.setSendTime(LocalDateTime.now());
        message.setRead(false);
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByAppointment(Appointment appointment) {
        return messageRepository.findByAppointmentOrderBySendTimeAsc(appointment);
    }

    public List<Message> getUserMessages(User user) {
        return messageRepository.findByUserOrderBySendTimeDesc(user);
    }

    public List<Message> getDoctorMessages(Doctor doctor) {
        return messageRepository.findByDoctorOrderBySendTimeDesc(doctor);
    }

    public Long countUnreadMessagesByDoctor(Doctor doctor) {
        return messageRepository.countUnreadMessagesByDoctor(doctor);
    }

    public Long countUnreadMessagesByUser(User user) {
        return messageRepository.countUnreadMessagesByUser(user);
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }

    public Long countUnreadMessagesByDoctorAndAppointment(Doctor doctor, Appointment appointment) {
        return messageRepository.countUnreadMessagesByDoctorAndAppointment(doctor, appointment);
    }

    public void markDoctorMessagesAsRead(Appointment appointment) {
        List<Message> messages = messageRepository.findUnreadMessagesByDoctorAndAppointment(
                appointment.getDoctor(), appointment);
        for (Message message : messages) {
            if (message.getSender().equals("user")) {
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }

    public void markUserMessagesAsRead(Appointment appointment) {
        List<Message> messages = messageRepository.findUnreadMessagesByUserAndAppointment(
                appointment.getUser(), appointment);
        for (Message message : messages) {
            if (message.getSender().equals("doctor")) {
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }
}