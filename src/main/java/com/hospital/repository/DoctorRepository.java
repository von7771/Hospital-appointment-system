package com.hospital.repository;

import com.hospital.entity.Doctor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface DoctorRepository extends CrudRepository<Doctor, Long> {
    List<Doctor> findByDepartment(String department);
    
    Doctor findByName(String name);

    @Query("SELECT DISTINCT d.department FROM Doctor d")
    List<String> findAllDepartments();
}