package com.hospital.controller;

import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import com.hospital.service.DoctorService;
import com.hospital.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {
    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<Doctor> list(@RequestParam(required = false) String department) {
        if (department != null && !department.isEmpty()) {
            return doctorService.findByDepartment(department);
        }
        return doctorService.findAll();
    }

    @GetMapping("/departments")
    public List<String> getDepartments() {
        return doctorService.findAllDepartments();
    }

    @PostMapping("/add")
    public Doctor addDoctor(@RequestBody Doctor doctor) {
        return doctorService.save(doctor);
    }

    @PutMapping("/update")
    public Doctor updateDoctor(@RequestBody Doctor doctor) {
        return doctorService.save(doctor);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        doctorService.delete(id);
    }

    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable Long id) {
        return doctorService.findById(id);
    }

    @GetMapping("/fix-doctor-user-association/{username}")
    public Map<String, Object> fixDoctorUserAssociation(@PathVariable String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            logger.info("尝试修复医生用户关联: 用户名={}", username);
            
            // 查找用户
            User user = userService.findByUsername(username);
            if (user == null) {
                logger.error("用户不存在: {}", username);
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 检查用户角色
            if (!"doctor".equals(user.getRole())) {
                logger.error("用户不是医生角色: {}", username);
                result.put("success", false);
                result.put("message", "用户不是医生角色");
                return result;
            }
            
            // 检查医生关联
            if (user.getDoctor() != null) {
                logger.info("用户已有医生关联: 用户ID={}, 医生ID={}", user.getId(), user.getDoctor().getId());
                result.put("success", true);
                result.put("message", "用户已有医生关联");
                result.put("user", user);
                result.put("doctor", user.getDoctor());
                return result;
            }
            
            // 查找同名医生
            Doctor doctor = doctorService.findByName(username);
            if (doctor == null) {
                logger.error("未找到同名医生: {}", username);
                result.put("success", false);
                result.put("message", "未找到同名医生");
                return result;
            }
            
            // 更新用户关联
            user.setDoctor(doctor);
            User updatedUser = userService.update(user);
            
            logger.info("成功修复医生用户关联: 用户ID={}, 医生ID={}", updatedUser.getId(), updatedUser.getDoctor().getId());
            result.put("success", true);
            result.put("message", "成功修复医生用户关联");
            result.put("user", updatedUser);
            result.put("doctor", updatedUser.getDoctor());
            
            return result;
        } catch (Exception e) {
            logger.error("修复医生用户关联失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "修复失败: " + e.getMessage());
            return result;
        }
    }
}