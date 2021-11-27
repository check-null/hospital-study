package com.sub.hosp.repository;

import com.sub.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Europa
 */
public interface DepartmentRepository extends MongoRepository<Department, String> {
    Department getByHoscodeAndDepcode(String hoscode, String depcode);

    Department findDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
