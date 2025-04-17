package com.example.Project_1.Mapper;

import com.example.Project_1.Dto.EmployeeDto;
import com.example.Project_1.Model.Employee2;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-17T16:31:43+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.10 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public EmployeeDto dto(Employee2 employee2) {
        if ( employee2 == null ) {
            return null;
        }

        EmployeeDto employeeDto = new EmployeeDto();

        employeeDto.setId( employee2.getId() );
        employeeDto.setUsername( employee2.getUsername() );
        employeeDto.setPassword( employee2.getPassword() );
        employeeDto.setEmail( employee2.getEmail() );
        employeeDto.setRole( employee2.getRole() );

        return employeeDto;
    }

    @Override
    public Employee2 entity(EmployeeDto employeeDto) {
        if ( employeeDto == null ) {
            return null;
        }

        Employee2.Employee2Builder employee2 = Employee2.builder();

        employee2.id( employeeDto.getId() );
        employee2.username( employeeDto.getUsername() );
        employee2.password( employeeDto.getPassword() );
        employee2.email( employeeDto.getEmail() );
        employee2.role( employeeDto.getRole() );

        return employee2.build();
    }
}
