package com.example.Project_1.Repository;

import com.example.Project_1.Model.Employee2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Employee2Repository extends JpaRepository<Employee2,Integer> {
    Employee2 findByUsername (String username);
    List<Employee2> findByUsernameContaining(String keyword);
}
