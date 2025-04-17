package com.example.Project_1.Service;

import com.example.Project_1.Model.Employee2;
import com.example.Project_1.Model.Role;
import com.example.Project_1.Repository.Employee2Repository;
import com.example.Project_1.Services.Employee2Services;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@Service
@ExtendWith(MockitoExtension.class)
@Transactional //rolling back

    public class ServiceTest {

    @Mock
    private Employee2Repository employee2Repository;

    @InjectMocks
    private Employee2Services employee2Services;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void GetByIdTest(){

        Employee2 employee2 = new Employee2(25,"Vicky","Vic@123","Vic@gmail.com", Role.ADMIN);

        when(employee2Repository.findById(25)).thenReturn(Optional.of(employee2));
        Employee2 result =employee2Services.GetById(25);

        assertNotNull(result);
    }


    @Test
    public void CreateTest() {
        Employee2 employee2 = new Employee2(20, "Vicky", "Vic@123", "Vic@gmail.com", Role.ADMIN);
        Employee2 savedEmployee = new Employee2(26, "Vicky",encoder.encode( "Vic@123"), "Vic@gmail.com", Role.ADMIN);
        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singleton(() -> "SUPER_ADMIN");
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return "superadmin";
            }
        };

        when(employee2Repository.save(any(Employee2.class))).thenReturn(savedEmployee);
        Employee2 employee3 = employee2Services.register(employee2,"ADMIN",authentication);

        assertNotNull(employee3.getId());
        assertEquals("Vicky", employee3.getUsername());
        assertTrue(encoder.matches("Vic@123", employee3.getPassword()));
        assertEquals("Vicky", employee3.getUsername());
        System.out.println(" Create new user ==> "+ employee3.getUsername());
    }

    @Test
    public void UpdateTest(){

        Employee2 employee2 = new Employee2(25,"Vicky","Vis@123","Vis@gmail.com",Role.ADMIN);
        Employee2 saveEmp = new Employee2(null,"Vikram",encoder.encode("Vik@123"),"Vik@gmail.com",Role.USER);

        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singleton(()-> "SUPER_ADMIN");
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

            @Override
            public String getName() {
                return "superadmin";
            }
        };

        when(employee2Repository.findById(employee2.getId())).thenReturn(Optional.of(employee2));
        when(employee2Repository.save(any(Employee2.class))).thenReturn(saveEmp);

        Employee2 employee3 = employee2Services.UpdateE(employee2.getId(),saveEmp,"USER",authentication);

        assertNotNull(employee3);
        assertEquals("Vikram", employee3.getUsername());
        assertTrue(encoder.matches("Vik@123", employee3.getPassword()));
        assertEquals("Vik@gmail.com", employee3.getEmail());
        assertEquals(Role.USER, employee3.getRole());
        System.out.println("UpdateTest ==> " + employee3.getRole());
    }

    @Test
    public void DeleteTest(){
        Employee2 employee2 = new Employee2(25,"Vicky","Vis@123","Vis@gmail.com",Role.ADMIN);

        when(employee2Repository.findById(employee2.getId())).thenReturn(Optional.of(employee2));

        employee2Services.Delete(employee2.getId(),"SUPER_ADMIN");
        assertNotNull(employee2);

        assertEquals("Vicky", employee2.getUsername());
    }


//    @Test
//    public void GetByIdTest(){
//        Employee2 employee2 = new Employee2(null,"Sonu","S@123","Sonu@gmail.com",Role.USER);
//        Employee2 employee3 = employee2Repository.save(employee2);
//
//        Employee2 employee4 = employee2Services.GetById(employee3.getId());
//
//        assertNotNull(employee4);
//        assertEquals("Sonu",employee4.getUsername());
//        assertEquals("S@123",employee4.getPassword());
//        assertEquals("Sonu@gmail.com",employee4.getEmail());
//        assertEquals(Role.USER,employee4.getRole());
//    }

//    @Test
//    public void CreateTest(){
//        Employee2 employee2 = new Employee2(null,"Sanjay","San@123","San@gmail.com",Role.ADMIN);
//
//        Authentication authentication = new Authentication() {
//            @Override
//            public Collection<? extends GrantedAuthority> getAuthorities() {
//                return Collections.singleton(()-> "SUPER_ADMIN");
//            }
//            @Override
//            public Object getCredentials() {
//                return null;
//            }
//            @Override
//            public Object getDetails() {
//                return null;
//            }
//            @Override
//            public Object getPrincipal() {
//                return null;
//            }
//            @Override
//            public boolean isAuthenticated() {
//                return true;
//            }
//            @Override
//            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}
//
//            @Override
//            public String getName() {
//                return "superadmin";
//            }
//        };
//
//        Employee2 employee3 = employee2Services.register(employee2,"ADMIN",authentication);
//
//        assertNotNull(employee3.getId());
//        assertEquals("Sanjay",employee3.getUsername());
//        assertTrue(encoder.matches("San@123",employee3.getPassword()));
//        assertEquals("San@gmail.com",employee3.getEmail());
//        assertEquals(Role.ADMIN,employee3.getRole());
//    }

//    @Test
//    public void UpdateTest(){
//        Employee2 employee2 = new Employee2(null,"Vansh","V@123","V@gmail.com",Role.USER);
//        Authentication authentication = new Authentication() {
//            @Override
//            public Collection<? extends GrantedAuthority> getAuthorities() {
//                return Collections.singleton(()-> "SUPER_ADMIN");
//            }
//            @Override
//            public Object getCredentials() {
//                return null;
//            }
//            @Override
//            public Object getDetails() {
//                return null;
//            }
//            @Override
//            public Object getPrincipal() {
//                return null;
//            }
//            @Override
//            public boolean isAuthenticated() {
//                return true;
//            }
//            @Override
//            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
//
//            }
//            @Override
//            public String getName() {
//                return "superadmin";
//            }
//        };
//
//        Employee2 employee3 = employee2Services.register(employee2,"USER",authentication);
//
//        employee2Repository.findById(employee2.getId());
//        Employee2 update = new Employee2(null,"VANSH","V@123","V@gmail.com",Role.ADMIN);
//
//        String rowPass = encoder.encode(update.getPassword());
//        assertNotNull(employee2Services.UpdateE(employee3.getId(),update,"Admin",authentication));
//
//        assertEquals(employee3.getId(),employee2.getId());
//        assertEquals("VANSH",update.getUsername());
//        assertTrue(encoder.matches("V@123",rowPass));
//        assertEquals("V@gmail.com",update.getEmail());
//        assertEquals(Role.ADMIN,update.getRole());
//    }
//
//    @Test
//    public void DeleteTest2(){
//        Employee2 employee2 = new Employee2(null,"Xzy","X@123","X@gmail.com",Role.USER);
//
//        Authentication authentication = new Authentication() {
//            @Override
//            public Collection<? extends GrantedAuthority> getAuthorities() {
//                return Collections.singleton(()-> "SUPER_ADMIN");
//            }
//            @Override
//            public Object getCredentials() {
//                return null;
//            }
//            @Override
//            public Object getDetails() {
//                return null;
//            }
//            @Override
//            public Object getPrincipal() {
//                return null;
//            }
//            @Override
//            public boolean isAuthenticated() {
//                return true;
//            }
//            @Override
//            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
//
//            }
//            @Override
//            public String getName() {
//                return "superadmin";
//            }
//        };
//
//        Employee2 employee3 = employee2Services.register(employee2,"ADMIN",authentication);
//
//        assertNotNull(employee3);
//        employee2Services.Delete(employee3.getId(),"SUPER_ADMIN");
//
//        Optional<Employee2> deleteEmployee = employee2Repository.findById(employee3.getId());
//        assertTrue(deleteEmployee.isEmpty());
//    }
}