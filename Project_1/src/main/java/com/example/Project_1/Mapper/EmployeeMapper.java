package com.example.Project_1.Mapper;

import com.example.Project_1.Dto.EmployeeDto;
import com.example.Project_1.Model.Employee2;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") public interface EmployeeMapper {

    EmployeeDto dto (Employee2 employee2);
    Employee2 entity (EmployeeDto employeeDto);

}
