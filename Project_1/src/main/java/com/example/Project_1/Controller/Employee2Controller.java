package com.example.Project_1.Controller;

import com.example.Project_1.Model.Employee2;
import com.example.Project_1.Dto.EmployeeDto;
import com.example.Project_1.Services.Employee2Services;
import com.example.Project_1.Services.JWTServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/Employee")
public class Employee2Controller {

    @Autowired
    public Employee2Services employee2Services;

    @Autowired
    private JWTServices jwtServices;


    @GetMapping("/get")
    public List<EmployeeDto> getAll(){
        return employee2Services.getAll();
    }

    @PostMapping("/add")
    public EmployeeDto create(@RequestBody EmployeeDto employeeDto){
        return employee2Services.createEmployee(employeeDto);
    }

    @GetMapping
    public List<Employee2> Get(){
        return employee2Services.GetAll();
    }

    @GetMapping("/{id}")
    public Employee2 GetByIdD(@PathVariable Integer id){
        return employee2Services.GetById(id).orElseThrow();
    }

    @PostMapping("/register")
    public ResponseEntity<Employee2> register(@RequestBody Employee2 employee,
                                              @RequestParam(required = false, defaultValue = "USER") String role) {
        return ResponseEntity.ok(employee2Services.register(employee, role));
    }

    @PutMapping("/{id}")
    public Employee2 Update(@PathVariable Integer id, @RequestBody Employee2 employee2){
        return employee2Services.UpdateE(id, employee2);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        employee2Services.Delete(id);
        return ResponseEntity.noContent().build();
    }

/// Admin
//    @PostMapping("/register-admin")
//    public ResponseEntity<?> registerAdmin(@RequestBody Employee2 employee2) {
//
//        try {
//            Employee2 registeredAdmin = employee2Services.register(employee2);
//            return ResponseEntity.ok(registeredAdmin);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

/// Search
    @GetMapping("/search")
    public ResponseEntity<List<Employee2>>Search(@RequestParam String keyword){
        if (keyword.length()< 2){
            return ResponseEntity.badRequest().body(null);
        }
        List<Employee2> name = employee2Services.searchName(keyword);
        return ResponseEntity.ok(name);
    }
/// login , logout
    @PostMapping("/login")
    public ResponseEntity<String> login (@RequestBody Employee2 employee2 ){
         return ResponseEntity.ok(employee2Services.login(employee2));
    }

    @PostMapping("/logout/{id}")
    public ResponseEntity<String> logout(@PathVariable Integer id,@RequestHeader("Authorization") String token){
       Optional<Employee2> emp = employee2Services.GetById(id);

        if (emp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        Employee2 employee2 = emp.get();

        String usernameFormToken = jwtServices.First(token.substring(7));

        if (!employee2.getUsername().equals(usernameFormToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user. You can only logout with your own User-Id.");
        }
        return ResponseEntity.ok( "Log-out SuccessFully .. ");
    }

}
