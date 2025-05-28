package com.hospital.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import com.hospital.service.DoctorService;
import com.hospital.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DoctorService doctorService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        logger.info("用户登录请求: 用户名={}", user.getUsername());
        
        User dbUser = userService.findByUsername(user.getUsername());
        if (dbUser != null && dbUser.getPassword().equals(user.getPassword())) {
            logger.info("用户登录成功: ID={}, 用户名={}, 角色={}", dbUser.getId(), dbUser.getUsername(), dbUser.getRole());
            return dbUser;
        }
        
        logger.warn("用户登录失败: 用户名={}", user.getUsername());
        return null;
    }

    @PostMapping("/doctor/login")
    public Map<String, Object> doctorLogin(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        logger.info("医生登录请求: 用户名={}", user.getUsername());
        
        User dbUser = userService.findByUsername(user.getUsername());
        
        if (dbUser != null && dbUser.getPassword().equals(user.getPassword()) && dbUser.getRole().equals("doctor")) {
            logger.info("医生登录成功: ID={}, 用户名={}", dbUser.getId(), dbUser.getUsername());
            
            result.put("success", true);
            result.put("user", dbUser);
            
            // 获取医生信息
            Doctor doctorInfo = dbUser.getDoctor();
            
            // 如果用户没有关联的医生实体，尝试查找同名医生并关联
            if (doctorInfo == null) {
                logger.warn("医生用户没有关联的医生实体: ID={}, 用户名={}", dbUser.getId(), dbUser.getUsername());
                
                // 尝试查找同名医生
                Doctor existingDoctor = doctorService.findByName(dbUser.getUsername());
                
                if (existingDoctor != null) {
                    logger.info("找到同名医生，进行关联: 医生ID={}, 医生名称={}", existingDoctor.getId(), existingDoctor.getName());
                    
                    // 更新用户的医生关联
                    dbUser.setDoctor(existingDoctor);
                    userService.update(dbUser);
                    
                    result.put("doctor", existingDoctor);
                    logger.info("已关联医生信息: ID={}, 姓名={}", existingDoctor.getId(), existingDoctor.getName());
                } else {
                    // 创建临时医生信息
                    Doctor tempDoctor = new Doctor();
                    tempDoctor.setId(dbUser.getId());
                    tempDoctor.setName(dbUser.getUsername());
                    tempDoctor.setDepartment("未指定科室");
                    tempDoctor.setTitle("医生");
                    
                    result.put("doctor", tempDoctor);
                    logger.info("已创建临时医生信息: ID={}, 姓名={}", tempDoctor.getId(), tempDoctor.getName());
                }
            } else {
                result.put("doctor", doctorInfo);
                logger.info("已获取医生信息: ID={}, 姓名={}", doctorInfo.getId(), doctorInfo.getName());
            }
        } else {
            logger.warn("医生登录失败: 用户名={}", user.getUsername());
            result.put("success", false);
        }
        
        return result;
    }

    @GetMapping("/list")
    public List<User> listAllUsers() {
        return userService.findAllUsers();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/doctor/info")
    public Doctor getDoctorInfo(@RequestParam Long userId) {
        logger.info("获取医生信息: 用户ID={}", userId);
        
        try {
            // 查找用户
            User user = userService.findById(userId);
            if (user == null) {
                logger.error("用户不存在: ID={}", userId);
                return null;
            }
            
            // 检查用户角色
            if (!"doctor".equals(user.getRole())) {
                logger.error("用户不是医生角色: ID={}", userId);
                return null;
            }
            
            // 获取医生信息
            Doctor doctor = user.getDoctor();
            
            // 如果用户没有关联的医生实体，尝试查找同名医生
            if (doctor == null) {
                logger.warn("医生用户没有关联的医生实体: ID={}, 用户名={}", user.getId(), user.getUsername());
                doctor = doctorService.findByName(user.getUsername());
                
                if (doctor != null) {
                    logger.info("找到同名医生: ID={}, 姓名={}", doctor.getId(), doctor.getName());
                    
                    // 更新用户的医生关联
                    user.setDoctor(doctor);
                    userService.update(user);
                    logger.info("已更新用户的医生关联: 用户ID={}, 医生ID={}", user.getId(), doctor.getId());
                } else {
                    logger.warn("未找到同名医生，创建临时医生信息");
                    Doctor tempDoctor = new Doctor();
                    tempDoctor.setId(user.getId());
                    tempDoctor.setName(user.getUsername());
                    tempDoctor.setDepartment("未指定科室");
                    tempDoctor.setTitle("医生");
                    return tempDoctor;
                }
            }
            
            return doctor;
        } catch (Exception e) {
            logger.error("获取医生信息失败: {}", e.getMessage(), e);
            return null;
        }
    }
}