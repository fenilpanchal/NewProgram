package com.example.Project_1.Services;

import com.example.Project_1.Mapper.EmployeeMapper;
import com.example.Project_1.Model.Employee2;
import com.example.Project_1.Dto.EmployeeDto;
import com.example.Project_1.Model.EmployeePrincipal;
import com.example.Project_1.Model.Role;
import com.example.Project_1.Repository.Employee2Repository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class Employee2Services implements UserDetailsService {

    @Autowired
    @Lazy
    private AuthenticationManager manager;

    @Autowired
    private JWTServices services;

    @Autowired
    private Employee2Repository employee2Repository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final EmployeeMapper employeeMapper;

    @Autowired
    public Employee2Services( EmployeeMapper employeeMapper,Employee2Repository employee2Repository) {
        this.employeeMapper = employeeMapper;
        this.employee2Repository= employee2Repository;
    }
/// dto
    public List<EmployeeDto> getAll(){
        return employee2Repository.findAll()
                .stream()
                .map(employeeMapper::dto)
                .collect(Collectors.toList());
    }

    public EmployeeDto getById(Integer id){
        Employee2 employee2 = employee2Repository.findById(id).orElseThrow(()-> new RuntimeException("not found"));
        return employeeMapper.dto(employee2);
    }

    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        Employee2 employee2 = employeeMapper.entity(employeeDto);
        employee2.setPassword(encoder.encode(employeeDto.getPassword()));
        return employeeMapper.dto(employee2Repository.save(employee2));
    }

//    public EmployeeDto updateEmployee(Integer id,EmployeeDto employeeDto){
//        Employee2 employee2 = employee2Repository.findById(id).orElseThrow(()-> new RuntimeException("Not Found"));
//        employee2.setUsername(employeeDto.getUsername());
//        employee2.setPassword(encoder.encode(employeeDto.getPassword()));
//        employee2.setEmail(employeeDto.getEmail());
//        employee2.setRole(employeeDto.getRole());
//        return employeeMapper.dto(employee2Repository.save(employee2));
//    }
//
//    public void deleteEmployee(Integer id){
//        employee2Repository.deleteById(id);
//    }
/// dto
    
    public List<Employee2> GetAll() {
        log.info(" GetAll Method !!");
        return employee2Repository.findAll();
    }

    public Employee2 GetById(Integer id) {
        log.info(" GetById Method !!  :{}", id);
       return employee2Repository.findById(id)
               .orElseThrow(() -> new RuntimeException("Employee not found!"));
    }

    public Employee2 register(Employee2 employee2, String role,Authentication authentication) {
        log.info("Creating Employee with username: {}", employee2.getUsername());

        if (employee2Repository.findByUsername(employee2.getUsername()) != null) {
            throw new RuntimeException("Username already exists. Please choose a different username.");
        }
        if (employee2.getEmail() == null || employee2.getEmail().isEmpty()) {
            throw new RuntimeException("Please enter your Email ID!");
        }
        if (!employee2.getEmail().matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            throw new RuntimeException("Email must be a valid Gmail address (e.g., example@gmail.com)");
        }

        String currentUserRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        if ("ADMIN".equals(currentUserRole) && "ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Admins are NOT allowed to create Admin accounts.");
        }
        if (!"SUPER_ADMIN".equals(currentUserRole) && "SUPER_ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only a Super-Admin can create another Admin.");
        }
        if ("SUPER_ADMIN".equals(currentUserRole)) {
            if (!"ADMIN".equalsIgnoreCase(role) && !"USER".equalsIgnoreCase(role)) {
                throw new RuntimeException("Super-Admin can only create Admins or Users.");
            }
        }
        if ("ADMIN".equals(currentUserRole) && !"USER".equalsIgnoreCase(role)) {
            throw new RuntimeException("Admins can only create Users.");
        }

        employee2.setRole(Role.valueOf(role.toUpperCase()));
        employee2.setPassword(encoder.encode(employee2.getPassword()));
        return employee2Repository.save(employee2);
    }

    public Employee2 UpdateE(Integer id,Employee2 employee2, String role,Authentication authentication) {
        log.info("Update Employee with username.: {}",employee2.getUsername());

        String currentUserRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        if ("ADMIN".equals(currentUserRole) && "ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Admins are NOT allowed to Update Admin accounts.");
        }
        if (!"SUPER_ADMIN".equals(currentUserRole) && "SUPER_ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only a Super-Admin can Update another Admin.");
        }
        if ("SUPER_ADMIN".equals(currentUserRole) && !"ADMIN".equalsIgnoreCase(role) && !"USER".equalsIgnoreCase(role)) {
                throw new RuntimeException("Super-Admin can only Update Admins or Users.");
        }
        if ("ADMIN".equals(currentUserRole) && !"USER".equalsIgnoreCase(role)) {
            throw new RuntimeException("Admins can only Update Users.");
        }
        Employee2 employee3 = GetById(id);
//                .orElseThrow(()->new RuntimeException("Employee Not Found With ID :" + id ));

        employee3.setRole(Role.valueOf(role.toUpperCase()));
        employee3.setUsername(employee2.getUsername());
        employee3.setEmail(employee2.getEmail());
        employee3.setPassword(encoder.encode(employee2.getPassword()));
        return employee2Repository.save(employee3);
    }

    public void Delete(Integer id,String currentUserRole){

        Employee2 deleteUser = employee2Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        if ("SUPER_ADMIN".equalsIgnoreCase(currentUserRole) ||
                ("ADMIN".equalsIgnoreCase(currentUserRole) && "USER"
                        .equalsIgnoreCase(deleteUser.getRole().name()))) {
            employee2Repository.deleteById(id);
            log.warn("Deleted successfully: {}", id);
            return;
        }
        throw new RuntimeException("Access Denied: Only Super-Admin can delete Admins and Users.");
    }

    public String login(Employee2 employee2){
        log.info(" Login SuccessFully :{}",employee2.getUsername());

        if (employee2.getEmail()==null || employee2.getEmail().isEmpty()){
            throw new RuntimeException("Please enter your Email ID!");
        }
        if (!employee2.getEmail().matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            throw new RuntimeException("Email must be a valid Gmail address (e.g., example@gmail.com)");
        }
        Authentication authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(employee2.getUsername(),employee2.getPassword()));
        if (authentication.isAuthenticated()) {
            Employee2 employee3 = employee2Repository.findByUsername(employee2.getUsername());
            return services.generateToken(employee3.getUsername(), employee3.getRole());
        }
        return "Fail";
    }

///Search api
    public List<Employee2> searchName(String keyword){
        return employee2Repository.findByUsernameContaining(keyword);
    }
///
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee2 employee2 = employee2Repository.findByUsername(username);

        if (employee2 == null) {
            throw new UsernameNotFoundException("Not Found");
        }
        return new EmployeePrincipal(employee2);
    }
}
