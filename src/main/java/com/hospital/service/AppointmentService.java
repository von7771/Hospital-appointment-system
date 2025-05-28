package com.hospital.service;

import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import com.hospital.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    
    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment book(Appointment appointment) {
        if (appointment == null) {
            logger.error("预约对象为空");
            return null;
        }
        
        if (appointment.getUser() == null) {
            logger.error("预约中的用户为空");
            return null;
        }
        
        if (appointment.getDoctor() == null) {
            logger.error("预约中的医生为空");
            return null;
        }
        
        logger.info("创建预约: 用户={}, 医生={}, 时间={}", 
            appointment.getUser().getUsername(),
            appointment.getDoctor().getName(),
            appointment.getAppointmentTime());
            
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> findByUser(User user) {
        if (user == null) {
            logger.error("用户对象为空");
            return new ArrayList<>();
        }
        
        logger.info("查询用户的预约: ID={}, 用户名={}", user.getId(), user.getUsername());
        List<Appointment> appointments = appointmentRepository.findByUser(user);
        logger.info("用户预约数量: {}", appointments.size());
        return appointments;
    }

    public List<Appointment> findByDoctor(Doctor doctor) {
        if (doctor == null) {
            logger.error("医生对象为空");
            return new ArrayList<>();
        }
        
        logger.info("查询医生的预约: ID={}, 姓名={}", doctor.getId(), doctor.getName());
        List<Appointment> appointments = appointmentRepository.findByDoctor(doctor);
        logger.info("医生预约数量: {}", appointments.size());
        
        // 打印详细的预约信息用于调试
        if (!appointments.isEmpty()) {
            for (Appointment appointment : appointments) {
                logger.debug("预约详情: ID={}, 患者={}, 医生={}, 时间={}, 状态={}",
                    appointment.getId(),
                    appointment.getUser().getUsername(),
                    appointment.getDoctor().getName(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus());
            }
        }
        
        return appointments;
    }

    public List<Appointment> findByDoctorName(String doctorName) {
        if (doctorName == null || doctorName.isEmpty()) {
            logger.error("医生名称为空");
            return new ArrayList<>();
        }
        
        logger.info("根据名称查询医生的预约: 姓名={}", doctorName);
        List<Appointment> appointments = appointmentRepository.findByDoctorName(doctorName);
        logger.info("医生预约数量: {}", appointments.size());
        
        return appointments;
    }

    public List<Appointment> findAll() {
        logger.info("查询所有预约");
        return appointmentRepository.findAll();
    }
    
    public Appointment findById(Long id) {
        if (id == null) {
            logger.error("预约ID为空");
            return null;
        }
        
        logger.info("按ID查询预约: {}", id);
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        
        if (appointment.isPresent()) {
            logger.info("找到预约: ID={}", id);
            return appointment.get();
        } else {
            logger.warn("未找到预约: ID={}", id);
            return null;
        }
    }

    public Appointment updateStatus(Long appointmentId, String status) {
        if (appointmentId == null) {
            logger.error("预约ID为空");
            return null;
        }
        
        logger.info("更新预约状态: ID={}, 新状态={}", appointmentId, status);
        Appointment appointment = findById(appointmentId);
        
        if (appointment == null) {
            logger.error("预约不存在: ID={}", appointmentId);
            return null;
        }
        
        appointment.setStatus(status);
        Appointment updated = appointmentRepository.save(appointment);
        logger.info("预约状态已更新: ID={}, 状态={}", updated.getId(), updated.getStatus());
        return updated;
    }
}