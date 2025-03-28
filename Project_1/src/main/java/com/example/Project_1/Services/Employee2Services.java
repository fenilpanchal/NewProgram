package com.example.Project_1.Services;

import com.example.Project_1.Model.Employee2;
import com.example.Project_1.Dto.EmployeeDto;
import com.example.Project_1.Model.EmployeePrincipal;
import com.example.Project_1.Repository.Employee2Repository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.Project_1.Model.Role.*;

@Service
@Slf4j
public class Employee2Services implements UserDetailsService {

    @Autowired
    @Lazy
    private AuthenticationManager manager;

    @Autowired
    private JWTServices services;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    private final Employee2Repository employee2Repository;
    private final ModelMapper modelMapper;

    public Employee2Services(Employee2Repository employee2Repository ,ModelMapper modelMapper) {
        this.employee2Repository = employee2Repository;
        this.modelMapper = modelMapper;
    }

    public List<EmployeeDto> getAll(){
        return employee2Repository.findAll()
                .stream()
                .map(emp-> modelMapper.map(emp,EmployeeDto.class))
                .collect(Collectors.toList());
    }

    public EmployeeDto createEmployee(EmployeeDto employeeDto){
        Employee2 employee2 = modelMapper.map(employeeDto, Employee2.class);
        employee2.setPassword(encoder.encode(employeeDto.getPassword()));
        employee2.setRole(employeeDto.getRole() != null ? employeeDto.getRole() : USER); // Default role
        return modelMapper.map(employee2Repository.save(employee2),EmployeeDto.class);
    }
//    manual
//    private EmployeeDto convertToDto(Employee2 employee2){
//        return new EmployeeDto(employee2.getUsername(),employee2.getPassword(),employee2.getRole());
//    }

//    public List<EmployeeDto> getAll(){
//        List<Employee2> employee2List = employee2Repository.findAll();
//        return employee2List
//                .stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }

//    public EmployeeDto createEmployee(EmployeeDto employeeDto){
//
//        Employee2 employee2 =new Employee2();
//
//        employee2.setUsername(employeeDto.getUsername());
//        employee2.setPassword(encoder.encode(employeeDto.getPassword()));
//        employee2.setRole(employeeDto.getRole() !=null ? employeeDto.getRole(): USER);
//
//        Employee2 saveEmp =employee2Repository.save(employee2);
//
//        return new EmployeeDto(saveEmp.getUsername(),saveEmp.getPassword(),saveEmp.getRole());
//    }

    public List<Employee2> GetAll(){
        log.info("Find All Users By The Get Method !!");
           return employee2Repository.findAll();
    }

    public Optional<Employee2> GetById(Integer id){
        log.info("Find GetById  :{}",id);
        return employee2Repository.findById(id);
    }

    public Employee2 register( Employee2 employee2,String role){
        log.info("Creating Employee with username: {}",employee2.getUsername());

        if (employee2Repository.findByUsername(employee2.getUsername()) != null) {
            throw new RuntimeException("Username already exists. Please choose a different username.");
        }
        if (employee2.getEmail()==null || employee2.getEmail().isEmpty()){
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
            throw new RuntimeException("Only a Super-Admin can create another Super-Admin.");
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
        log.info("Creating Employee with username.: {}",employee2.getUsername());

        String currentUserRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        if ("ADMIN".equals(currentUserRole) && "ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Admins are NOT allowed to Update Admin accounts.");
        }
        if (!"SUPER_ADMIN".equals(currentUserRole) && "SUPER_ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only a Super-Admin can Update another Super-Admin.");
        }
        if ("SUPER_ADMIN".equals(currentUserRole) && !"ADMIN".equalsIgnoreCase(role) && !"USER".equalsIgnoreCase(role)) {
                throw new RuntimeException("Super-Admin can only Update Admins or Users.");
        }

        if ("ADMIN".equals(currentUserRole) && !"USER".equalsIgnoreCase(role)) {
            throw new RuntimeException("Admins can only Update Users.");
        }
        Employee2 employee3 = GetById(id).orElseThrow(()->new RuntimeException("Employee Not Found With ID :" + id ));

        employee3.setRole(Role.valueOf(role.toUpperCase()));
        employee3.setUsername(employee2.getUsername());
        employee3.setEmail(employee2.getEmail());
        employee3.setPassword(encoder.encode(employee2.getPassword()));
        return employee2Repository.save(employee3);
    }
     public void Delete(Integer id,String currentUserRole){

        if (!"SUPER_ADMIN".equalsIgnoreCase(currentUserRole)) {
            throw new RuntimeException("Access Denied: Only Super-Admin can delete admins and users.");
        }
        if (employee2Repository.existsById(id)) {
            log.warn("ID exists in DB: {}", id);
        }
        employee2Repository.deleteById(id);
        log.warn("Delete SuccessFully :{}",id);
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
            return services.generateToken(employee2.getUsername());
        }
        return "Fail";
    }
//Search api
    public List<Employee2> searchName(String keyword){
        return employee2Repository.findByUsernameContaining(keyword);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Employee2 employee2 = employee2Repository.findByUsername(username);

        if (employee2==null){
            throw new UsernameNotFoundException("Not Found");
        }
        return new EmployeePrincipal(employee2);
    }

}
