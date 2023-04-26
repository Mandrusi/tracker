package com.mark.tracker.dto;

import com.mark.tracker.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "address.id", ignore = true)
    Employee toEmployee(EmployeeDTO employeeDto);

    EmployeeDTO toEmployeeDto(Employee employee);

}
