package com.hospital.controller;

import com.hospital.entity.Appointment;

import java.util.Map;

import com.hospital.entity.User;
import com.hospital.entity.Doctor;
import com.hospital.service.AppointmentService;
import com.hospital.service.UserService;
import com.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.opencsv.CSVWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private DoctorService doctorService;

    @PostMapping("/book")
    public Appointment book(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long doctorId = Long.valueOf(params.get("doctorId").toString());
        String appointmentTime = params.get("appointmentTime").toString();
        String status = params.get("status").toString();

        logger.info("预约请求: 用户ID={}, 医生ID={}, 时间={}, 状态={}", userId, doctorId, appointmentTime, status);

        User user = userService.findById(userId);
        Doctor doctor = doctorService.findById(doctorId);
        
        if (user == null) {
            logger.error("用户不存在: ID={}", userId);
            return null;
        }
        
        if (doctor == null) {
            logger.error("医生不存在: ID={}", doctorId);
            return null;
        }
        
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(LocalDateTime.parse(appointmentTime));
        appointment.setStatus(status);
        
        Appointment saved = appointmentService.book(appointment);
        logger.info("预约成功: ID={}, 用户={}, 医生={}", saved.getId(), user.getUsername(), doctor.getName());
        return saved;
    }

    @GetMapping("/{id}")
    public Appointment getAppointmentById(@PathVariable Long id) {
        logger.info("获取预约详情: ID={}", id);
        try {
            Appointment appointment = appointmentService.findById(id);
            if (appointment != null) {
                logger.info("预约详情获取成功: ID={}, 患者={}, 医生={}", 
                    appointment.getId(), 
                    appointment.getUser().getUsername(), 
                    appointment.getDoctor().getName());
            } else {
                logger.warn("预约不存在: ID={}", id);
            }
            return appointment;
        } catch (Exception e) {
            logger.error("获取预约详情失败: ID={}, 错误={}", id, e.getMessage(), e);
            return null;
        }
    }

    @GetMapping("/list")
    public List<Appointment> listAllAppointments() {
        return appointmentService.findAll();
    }

    @GetMapping("/list/{username}")
    public List<Appointment> list(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return appointmentService.findByUser(user);
    }

    @PutMapping("/updateStatus")
    public Appointment updateStatus(@RequestParam Long appointmentId, @RequestParam String status) {
        return appointmentService.updateStatus(appointmentId, status);
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportAppointments() {
        try {
            List<Appointment> appointments = appointmentService.findAll();
            
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter);
            
            // 写入CSV头部
            String[] header = {"预约ID", "患者姓名", "医生姓名", "预约时间", "状态"};
            csvWriter.writeNext(header);
            
            // 写入数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Appointment appointment : appointments) {
                String[] data = {
                    appointment.getId().toString(),
                    appointment.getUser().getUsername(),
                    appointment.getDoctor().getName(),
                    appointment.getAppointmentTime().format(formatter),
                    appointment.getStatus()
                };
                csvWriter.writeNext(data);
            }
            
            csvWriter.close();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "appointments.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body("\uFEFF" + stringWriter.toString()); // 添加BOM以支持中文
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("导出失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/export/{username}")
    public ResponseEntity<String> exportUserAppointments(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            List<Appointment> appointments = appointmentService.findByUser(user);
            
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter);
            
            // 写入CSV头部
            String[] header = {"预约ID", "患者姓名", "医生姓名", "预约时间", "状态"};
            csvWriter.writeNext(header);
            
            // 写入数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Appointment appointment : appointments) {
                String[] data = {
                    appointment.getId().toString(),
                    appointment.getUser().getUsername(),
                    appointment.getDoctor().getName(),
                    appointment.getAppointmentTime().format(formatter),
                    appointment.getStatus()
                };
                csvWriter.writeNext(data);
            }
            
            csvWriter.close();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", username + "_appointments.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body("\uFEFF" + stringWriter.toString()); // 添加BOM以支持中文
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("导出失败: " + e.getMessage());
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> listDoctorAppointments(@PathVariable Long doctorId) {
        logger.info("获取医生预约列表: 医生ID={}", doctorId);
        
        try {
            Doctor doctor = doctorService.findById(doctorId);
            
            if (doctor == null) {
                logger.error("医生不存在: ID={}", doctorId);
                return new ArrayList<>();
            }
            
            // 先通过ID查询预约
            List<Appointment> appointments = appointmentService.findByDoctor(doctor);
            
            // 如果没有找到预约，尝试通过医生名称查询
            if (appointments.isEmpty()) {
                logger.info("通过ID未找到预约，尝试通过医生名称查询: ID={}, 姓名={}", doctorId, doctor.getName());
                appointments = appointmentService.findByDoctorName(doctor.getName());
            }
            
            logger.info("医生预约列表: 医生ID={}, 预约数量={}", doctorId, appointments.size());
            
            // 打印预约详情用于调试
            if (appointments.isEmpty()) {
                logger.info("医生没有预约记录: ID={}, 姓名={}", doctor.getId(), doctor.getName());
            } else {
                for (Appointment appointment : appointments) {
                    logger.info("预约详情: ID={}, 患者={}, 医生={}, 时间={}, 状态={}",
                        appointment.getId(),
                        appointment.getUser().getUsername(),
                        appointment.getDoctor().getName(),
                        appointment.getAppointmentTime(),
                        appointment.getStatus());
                }
            }
            
            return appointments;
        } catch (Exception e) {
            logger.error("获取医生预约列表失败: 医生ID={}, 错误={}", doctorId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    // 添加通过医生名称查询预约的接口
    @GetMapping("/doctor/name/{doctorName}")
    public List<Appointment> listDoctorAppointmentsByName(@PathVariable String doctorName) {
        logger.info("通过名称获取医生预约列表: 医生名称={}", doctorName);
        
        try {
            List<Appointment> appointments = appointmentService.findByDoctorName(doctorName);
            logger.info("医生预约列表: 医生名称={}, 预约数量={}", doctorName, appointments.size());
            return appointments;
        } catch (Exception e) {
            logger.error("通过名称获取医生预约列表失败: 医生名称={}, 错误={}", doctorName, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}