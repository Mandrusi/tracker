package com.mark.tracker.service;

import com.mark.tracker.exception.EmployeeNotFoundException;
import com.mark.tracker.exception.SkillNotFoundException;
import com.mark.tracker.model.Employee;
import com.mark.tracker.model.Skill;
import com.mark.tracker.repository.EmployeeRepository;
import com.mark.tracker.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    private SkillRepository skillRepository;

    public Employee[] getAllEmployees() {
        return employeeRepository.findAll().toArray(new Employee[0]);
    }

    public Employee save(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        return employee;
    }

    public Employee getEmployeeById(String id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return employee.get();
        } else {
            throw new EmployeeNotFoundException("Perficient employee not found.");
        }
    }

    public Employee updateEmployeeById(String id, Employee employee) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            Employee existingEmployee = optionalEmployee.get();
            //existingEmployee.setId(id);
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setAddress(employee.getAddress());
            existingEmployee.setBirthDate(employee.getBirthDate());
            existingEmployee.setHiredDate(employee.getHiredDate());
            existingEmployee.setBusinessUnit(employee.getBusinessUnit());
            existingEmployee.setRole(employee.getRole());
            existingEmployee.setAssignedTo(employee.getAssignedTo());
            existingEmployee.setSkills(employee.getSkills());
            existingEmployee.setContactEmail(employee.getContactEmail());
            existingEmployee.setCompanyEmail(employee.getCompanyEmail());
            if (existingEmployee.getAddress() != null) {
                existingEmployee.getAddress().setId(employee.getId());
            }

            return employeeRepository.save(existingEmployee);
        } else {
            throw new EmployeeNotFoundException("Perficient employee not found.");
        }
    }

    public void removeEmployeeById(String id) throws EmployeeNotFoundException {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()){

            employeeRepository.deleteById(id);
        } else {
            throw new EmployeeNotFoundException("Perficient employee not found ");
        }
    }
    public Set<Skill> findAllSkillsByEmployee(String employeeId) throws EmployeeNotFoundException  {
        Employee employee = getEmployeeById(employeeId);
        return employee.getSkills();
    }
    public Skill addSkillToEmployee(String employeeId, Skill skill) throws EmployeeNotFoundException {
        Employee employee = getEmployeeById(employeeId);
        Set<Skill> skills = employee.getSkills();
        skills.add(skill);
        employee.setSkills(skills);
        employeeRepository.save(employee);
        return skill;
    }

    public Skill findSkillFromEmployeeById(String employeeId, String skillId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            Set<Skill> skills = employee.getSkills();
            Optional<Skill> optionalSkill = skills.stream()
                    .filter(skill -> skill.getId().equals(skillId))
                    .findFirst();
            if (optionalSkill.isPresent()) {
                return optionalSkill.get();
            } else {
                throw new SkillNotFoundException("Skill not found for the given employee");
            }
        } else {
            throw new EmployeeNotFoundException("Employee not found");
        }
    }

    public Skill updateSkillFromEmployeeById(String employeeId, String skillId, Skill updatedSkill) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            Set<Skill> skills = employee.getSkills();
            Optional<Skill> optionalSkill = skills.stream()
                    .filter(skill -> skill.getId().equals(skillId))
                    .findFirst();
            if (optionalSkill.isPresent()) {
                Skill skillToUpdate = optionalSkill.get();
                skillToUpdate.setExperience(updatedSkill.getExperience());
                skillToUpdate.setSummary(updatedSkill.getSummary());
                skillToUpdate.setField(updatedSkill.getField());
                employeeRepository.save(employee);
                return skillToUpdate;
            } else {
                throw new SkillNotFoundException("Skill not found for the given employee");
            }
        } else {
            throw new EmployeeNotFoundException("Employee not found");
        }
    }

    public void deleteSkillFromEmployeeById(String employeeId, String skillId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            Set<Skill> skills = employee.getSkills();
            Optional<Skill> optionalSkill = skills.stream()
                    .filter(skill -> skill.getId().equals(skillId))
                    .findFirst();
            if (optionalSkill.isPresent()) {
                try {
                    Skill skillToDelete = optionalSkill.get();
                    skills.remove(skillToDelete);
                    employee.setSkills(skills);
                    employeeRepository.save(employee);
                }  catch (Exception e) {
                    throw new SkillNotFoundException("Skill not found for the given employee");
                }
            } else {
                throw new SkillNotFoundException("Skill not found for the given employee");
            }
        } else {
            throw new EmployeeNotFoundException("Employee not found");
        }
    }
}
