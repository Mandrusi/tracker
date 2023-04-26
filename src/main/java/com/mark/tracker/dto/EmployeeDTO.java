package com.mark.tracker.dto;

import com.mark.tracker.model.Address;
import com.mark.tracker.model.BusinessUnit;
import com.mark.tracker.model.Employee;
import com.mark.tracker.model.Role;
import com.mark.tracker.model.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {

    private String id;
    private String firstName;
    private String lastName;
    private Address address;
    private String companyEmail;
    private String birthDate;
    private String hiredDate;
    private Role role;
    private String contactEmail;
    private BusinessUnit businessUnit;
    private Set<Skill> skills;
    private Employee assignedTo;

    public static EmployeeDTO from(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .address(employee.getAddress())
                .companyEmail(employee.getCompanyEmail())
                .birthDate(employee.getBirthDate())
                .hiredDate(employee.getHiredDate())
                .role(employee.getRole())
                .contactEmail(employee.getContactEmail())
                .businessUnit(employee.getBusinessUnit())
                .skills(employee.getSkills())
                .assignedTo(employee.getAssignedTo())
                .build();
    }

    public static Employee to(EmployeeDTO employeeDTO) {
        return Employee.builder()
                .id(employeeDTO.getId())
                .firstName(employeeDTO.getFirstName())
                .lastName(employeeDTO.getLastName())
                .address(employeeDTO.getAddress())
                .companyEmail(employeeDTO.getCompanyEmail())
                .birthDate(employeeDTO.getBirthDate())
                .hiredDate(employeeDTO.getHiredDate())
                .role(employeeDTO.getRole())
                .contactEmail(employeeDTO.getContactEmail())
                .businessUnit(employeeDTO.getBusinessUnit())
                .skills(employeeDTO.getSkills())
                .assignedTo(employeeDTO.getAssignedTo())
                .build();
    }
}
