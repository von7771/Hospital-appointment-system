package com.hospital.service;

import com.hospital.entity.Doctor;
import com.hospital.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DoctorService {
    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
    
    @Autowired
    private DoctorRepository doctorRepository;

    public Doctor findById(Long id) {
        if (id == null) {
            logger.error("医生ID为空");
            return null;
        }
        
        logger.info("按ID查询医生: {}", id);
        Optional<Doctor> doctor = doctorRepository.findById(id);
        
        if (doctor.isPresent()) {
            logger.info("找到医生: ID={}, 姓名={}", id, doctor.get().getName());
            return doctor.get();
        } else {
            logger.warn("未找到医生: ID={}", id);
            return null;
        }
    }

    public List<Doctor> findAll() {
        logger.info("查询所有医生");
        // 将 Iterable 转换为 List
        return StreamSupport.stream(doctorRepository.findAll().spliterator(), false)
                            .collect(Collectors.toList());
    }

    public List<Doctor> findByDepartment(String department) {
        logger.info("按科室查询医生: {}", department);
        return doctorRepository.findByDepartment(department);
    }

    public List<String> findAllDepartments() {
        return doctorRepository.findAllDepartments();
    }

    public Doctor save(Doctor doctor) {
        logger.info("保存医生信息: {}", doctor);
        return doctorRepository.save(doctor);
    }

    public void delete(Long id) {
        logger.info("删除医生: ID={}", id);
        try {
            doctorRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete doctor with id: " + id, e);
        }
    }

    public Doctor update(Long id, Doctor updatedDoctor) {
        logger.info("更新医生信息: ID={}", id);
        Doctor doctor = findById(id);
        if (doctor != null) {
            doctor.setName(updatedDoctor.getName());
            doctor.setDepartment(updatedDoctor.getDepartment());
            doctor.setTitle(updatedDoctor.getTitle());
            doctor.setDescription(updatedDoctor.getDescription());
            return doctorRepository.save(doctor);
        }
        return null;
    }

    public Doctor findByName(String name) {
        logger.info("按名称查询医生: {}", name);
        return doctorRepository.findByName(name);
    }
}