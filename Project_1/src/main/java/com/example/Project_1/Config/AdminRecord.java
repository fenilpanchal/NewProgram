package com.example.Project_1.Config;

import com.example.Project_1.Model.Employee2;
import com.example.Project_1.Model.Role;
import com.example.Project_1.Repository.Employee2Repository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminRecord {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @Bean
    public ApplicationRunner runner (Employee2Repository employee2Repository){
        return arguments->{
            if (!employee2Repository.existsByRole(Role.SUPER_ADMIN)){
                Employee2 employee2 =new Employee2();
                employee2.setUsername("Fenil");
                employee2.setPassword(encoder.encode("F@123"));
                employee2.setEmail("F@gmail.com");
                employee2.setRole(Role.SUPER_ADMIN);

                employee2Repository.save(employee2);
                System.out.println(" Super-Admin created SuccessFully!! ");
            } else {
                System.out.println(" Already Exist Super-Admin in DB !!" );
            }
        };
    }
}
