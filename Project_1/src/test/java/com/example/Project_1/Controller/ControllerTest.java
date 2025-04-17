package com.example.Project_1.Controller;

import com.example.Project_1.Model.Employee2;
import com.example.Project_1.Model.Role;
import com.example.Project_1.Services.Employee2Services;
import com.example.Project_1.Services.JWTServices;

import org.junit.jupiter.api.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "superadmin", authorities = {"SUPER_ADMIN"})
@Import(ControllerTest.EmployeeControllerTestConfig.class)
@ActiveProfiles("test")
public class ControllerTest {
    @MockitoBean
    private JWTServices jwtServices ;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Employee2Services employee2Services;

    @TestConfiguration
    static class EmployeeControllerTestConfig {
        @Bean
        public Employee2Services employee2Services (){
            return Mockito.mock(Employee2Services.class);
        }
    }

    @Test
     void TestCreateEmployee()throws Exception {

        Employee2 employee2 = new Employee2(null, "Abhi", "A@123", "A@gmail.com", Role.ADMIN);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("role","ADMIN");

        when(employee2Services.register(
                ArgumentMatchers.any(Employee2.class),
                ArgumentMatchers.eq("ADMIN"),
                ArgumentMatchers.any(Authentication.class)
        )).thenReturn(employee2);
       mockMvc.perform(MockMvcRequestBuilders.post("/Employee/register")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                              "username": "Abhi",
                              "password": "A@123",
                              "email": "A@gmail.com"
                          }
                        """))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$.username").value("Abhi"));
        System.out.println("UserName = " + employee2.getUsername());
        System.out.println("Password = " + employee2.getPassword());
        System.out.println("Email = " + employee2.getEmail());
        System.out.println("Role = " + employee2.getRole());
        verify(employee2Services).register(any(Employee2.class),eq("ADMIN"),any(Authentication.class));
    }

    @Test
    public void TestGetByIdEmployee()throws Exception{
        Employee2 employee2 = new Employee2(2,"Mihir","Mi@123","Mi@gmile.com",Role.USER);

        when(employee2Services.GetById(employee2.getId())).thenReturn(employee2);

        mockMvc.perform(MockMvcRequestBuilders.get("/Employee/{id}",employee2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employee2.getId()))
                .andExpect(jsonPath("$.username").value("Mihir"))
                .andExpect(jsonPath("$.role").value("USER"));

        System.out.println("Fetch Employees = " + employee2);
        verify(employee2Services).GetById(employee2.getId());
    }

    @Test
    public void TestUpdateByEmployee() throws Exception {
        Employee2 employee2 = new Employee2(2,"Varun","Va@123","Va@gmail.com",Role.USER);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("role","USER");

        when(employee2Services.UpdateE(eq(employee2.getId()),any(Employee2.class),eq("USER"),any(Authentication.class)))
                .thenReturn(employee2);

        mockMvc.perform(MockMvcRequestBuilders.put("/Employee/{id}",employee2.getId())
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                             "id": 2,
                                "username": "Varun",
                                "password": "Va@123",
                                "email": "Va@gmail.com",
                                "role": "USER"
                          }
                        """))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$.id").value(employee2.getId()))
                .andExpect( jsonPath("$.username").value("Varun"))
                .andExpect( jsonPath("$.role").value("USER"));
        System.out.println("Update Name  = " + employee2);
        verify(employee2Services).UpdateE(eq(employee2.getId()),any(Employee2.class),eq("USER"),any(Authentication.class));
    }

    @Test
    public void TestDeleteEmployee() throws Exception {
        Employee2 employee2 = new Employee2(2,"Varun","Va@123","Va@gmail.com",Role.ADMIN);

        Mockito.when(jwtServices.extraRole("fake.jwt.token.with.two.dots"))
                .thenReturn(List.of("SUPER_ADMIN"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/Employee/{id}", employee2.getId())
                .header("Authorization", "Bearer fake.jwt.token.with.two.dots"))
        .andExpect(status().isNoContent());
        System.out.println("Deleted Employee = " + employee2);
        Mockito.verify(employee2Services).Delete(eq(employee2.getId()), eq("SUPER_ADMIN"));
    }
}