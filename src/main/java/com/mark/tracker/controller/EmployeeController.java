package com.mark.tracker.controller;
import com.mark.tracker.dto.EmployeeDTO;
import com.mark.tracker.dto.EmployeeMapper;
import com.mark.tracker.exception.EmployeeNotFoundException;
import com.mark.tracker.exception.SkillNotFoundException;
import com.mark.tracker.exception.UnprocessableEntityException;
import com.mark.tracker.model.Employee;
import com.mark.tracker.model.Skill;
import com.mark.tracker.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Set;

@RestController
// CrossOrigin to facilitate front-end access
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/employees")
public class EmployeeController {

    // Define a few static strings used in status messages
    private static final String INVALID_EMPLOYEE_DATA_STRING = "Invalid Perficient employee data sent to server";
    private static final String INVALID_ID_FORMAT_STRING = "Invalid ID format";
    private static final String BADLY_FORMATTED_DELETE_STRING = "Badly formatted delete request";
    private static final String NOTFOUND_EMPLOYEE_STRING ="Perficient employee not found";
    private static final String NOTFOUND_SKILL_OR_EMPLOYEE_STRING = "Technical skill or Perficient employee not found";
    private static final String UNKNOWN_ERROR_STRING = "Unknown error";

    @Autowired
    private EmployeeService employeeService;

    // Check whether a string is validly formatted GUID or UUID
    public Boolean isValidID(String id) {
        // Source for the expressions was ChatGPT; did not personally verify their accuracy
        String guidRegex = "^\\{?[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}\\}?$";
        String uuidRegex = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
        return (id.matches(guidRegex) || id.matches(uuidRegex));
    }

    // Return all employees, status code 200, with number of records in header X-Total-Count
    @GetMapping("")
    public ResponseEntity<Employee[]> getAllEmployees() {
        HttpHeaders headers = new HttpHeaders();
        Employee[] responseData = employeeService.getAllEmployees();
        headers.add("X-Total-Count", String.valueOf(responseData.length));
        return ResponseEntity.ok().headers(headers).body(responseData); // code 200
    }

    // Post creates an employee record via employeeService.save, returns success (201) or error (422)
    // Uses ResponseEntity<?> to return <Employee> if successful, <String> otherwise
    @PostMapping("")
    public ResponseEntity<?> save(@Valid @RequestBody Employee employee) {
        try {
            Employee responseData = employeeService.save(employee);
            return new ResponseEntity(responseData, HttpStatus.CREATED); //.body("Employee record created");
        } catch (Exception e) {
            System.out.println("POST request unprocessable; verify required fields present, data valid, unique ID.");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(INVALID_EMPLOYEE_DATA_STRING); // code 422
        }
    }

    // Get a single employee by ID number, return data and code 200 (OK), 400 (invalid id), or 404 (not found)
    // Uses ResponseEntity<?> to return <Employee> if successful, <String> otherwise
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") String id) throws EmployeeNotFoundException {
        if (!isValidID(id)) {
            System.out.println("DELETE request requires valid GUID or UUID: " + id + " is not valid.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_FORMAT_STRING); // code 400
        }

        Employee employee = employeeService.getEmployeeById(id);
        return new ResponseEntity<Employee>(employee, new HttpHeaders(), HttpStatus.OK); // code 200
    }

    // Put handler to update an employee; employee id included in URL, data sent as argument.
    // Return data and 200 (OK), 400 (invalid id), 404 (id not found), or 422 (invalid data sent)
    // Uses ResponseEntity<?> to return <Employee> if successful, <String> otherwise
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployeeById(@PathVariable("id") String id, @RequestBody EmployeeDTO employeeDTO) {
        if (!isValidID(id)) {
            System.out.println("PUT request requires valid GUID or UUID: " + id + " is not valid.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_FORMAT_STRING); // code 400
        }

        try {
            Employee employee = EmployeeMapper.INSTANCE.toEmployee(employeeDTO);
            employeeService.updateEmployeeById(id, employee);
            return new ResponseEntity<Employee>(employee, HttpStatus.OK);
        } catch (EmployeeNotFoundException e) {
            // handle the EmployeeNotFoundException, returning 404
            System.out.println("PUT request employee ID not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOTFOUND_EMPLOYEE_STRING); // code 404
        } catch (Exception e) {
            // handle any other exception, probably TransactionSystemException, but whatever the cause return 422
            System.out.println("PUT request unprocessable; verify required fields present, data valid, unique ID.");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(INVALID_EMPLOYEE_DATA_STRING); // code 422
        }
    }

    // Delete handler if no "/<guid>" after "employees" in DELETE URL, e.g. localhost:8080/employees
    @DeleteMapping("")
    public ResponseEntity<String> removeEmployeeNoSlash() {
        System.out.println("DELETE request requires /<GUID> or /<UUID> with the employee ID.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BADLY_FORMATTED_DELETE_STRING); // code 400
    }

    // Delete handler accepts employee ID, returns 204 (success), 400 (bad request), or 404 (id not found)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeEmployeeById(@PathVariable("id")String id) throws EmployeeNotFoundException {
        if (!isValidID(id)) {
            System.out.println("DELETE request requires valid GUID or UUID: " + id + " is not valid.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_FORMAT_STRING); // code 400
        }

        // ID is valid; send it to employee removal function, and return success or error
        try {
            employeeService.removeEmployeeById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Employee deletion successful"); // code 204
        } catch (Exception e) {
            System.out.println("DELETE request requires valid GUID or UUID: " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee ID to delete not found"); // code 404
        }
    }

// *******
// The following all pertain to Employee Skills:
// *******

    // Get all of employee's skills, return code 200, and number of records in X-Total-Count in header
    @GetMapping("/{employeeId}/skills")
    public ResponseEntity<?> findAllSkillsByEmployee(@PathVariable String employeeId) {
        if (!isValidID(employeeId)) {
            System.out.println("GET skills request requires valid GUID or UUID: " + employeeId + " is not valid.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_FORMAT_STRING); // code 400
        }

        HttpHeaders headers = new HttpHeaders();
        Set<Skill> skills = employeeService.findAllSkillsByEmployee(employeeId);
        headers.add("X-Total-Count", String.valueOf(skills.size()));
        return ResponseEntity.ok().headers(headers).body(skills); // code 200
    }

    // Post to create new skill for employee, returns 201 (success), 400 (invalid ID), 404 (employee not found), or 422 (invalid skill)
    // Uses ResponseEntity<?> to return <Skill> if successful, <String> otherwise
    @PostMapping("/{employeeId}/skills")
    public ResponseEntity<?> addSkillToEmployee(@PathVariable String employeeId, @RequestBody Skill skill) {
        if (!isValidID(employeeId)) {
            System.out.println("POST skills request requires valid GUID or UUID: " + employeeId + " is not valid.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_FORMAT_STRING); // code 400
        }

        try {
            // if successful, return code 201 (created), and the skill
            Skill responseData = employeeService.addSkillToEmployee(employeeId, skill);
            return new ResponseEntity(responseData, HttpStatus.CREATED);
        } catch (EmployeeNotFoundException e) {
            // handle the EmployeeNotFoundException, returning 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOTFOUND_EMPLOYEE_STRING);
        } catch (Exception e) {
            // handle any other exception, probably TransactionSystemException, but whatever the cause return 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Invalid technical skill data sent to server");
        }
    }

    // GET handler for specific employee's skills; return 200 (OK) with skills, 400 (bad ID), or 404 (not found)
    // ResponseEntity <?> returns type <Skill> if it's successful, <String> with error message otherwise.
    @GetMapping("/{employeeId}/skills/{skillId}")
    public ResponseEntity<?> findSkillFromEmployeeById(
            @PathVariable String employeeId, @PathVariable String skillId) {

        if (!isValidID(employeeId) || !isValidID(skillId)) {
            System.out.println("DELETE skill request requires valid GUID or UUID for employee ID and skill ID");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_FORMAT_STRING); // code 400
        }

        Skill skill = employeeService.findSkillFromEmployeeById(employeeId, skillId);

        if (skill != null) {
            return ResponseEntity.ok(skill);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOTFOUND_SKILL_OR_EMPLOYEE_STRING);
//            return ResponseEntity.notFound().build();
        }
    }

    // PUT handler to update employee specific skill; return 200 (OK) with skills, 400 (bad ID), 404 (not found), or 422 (invalid data)
    // ResponseEntity <?> returns type <Skill> if it's successful, <String> with error message otherwise.
    @PutMapping("/{employeeId}/skills/{skillId}")
    public ResponseEntity<?> updateSkillFromEmployeeById(@PathVariable String employeeId, @PathVariable String skillId, @RequestBody Skill skill) {
        if (!isValidID(employeeId) || !isValidID(skillId)) {
            System.out.println("PUT skill request requires valid GUID or UUID for employeeId and skillId.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_FORMAT_STRING); // code 400
        }

        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            Skill updatedSkill = employeeService.updateSkillFromEmployeeById(employeeId, skillId, skill);
            return ResponseEntity.ok(updatedSkill);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_FORMAT_STRING);
        } catch (EmployeeNotFoundException | SkillNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOTFOUND_SKILL_OR_EMPLOYEE_STRING); // code 404
        } catch (UnprocessableEntityException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Invalid technical skill data sent to server");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UNKNOWN_ERROR_STRING);
        }
    }

    // Delete handler for employee's specific skill, returns 204 (success), 400 (bad request), or 404 (id not found)
    @DeleteMapping("/{employeeId}/skills/{skillId}")
    public ResponseEntity<String> deleteSkillFromEmployeeById(@PathVariable String employeeId, @PathVariable String skillId) {
        if (!isValidID(employeeId) || !isValidID(skillId)) {
            System.out.println("DELETE skill request requires valid GUID or UUID for employeeId and skillId.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_FORMAT_STRING); // code 400
        }

        try {
            employeeService.deleteSkillFromEmployeeById(employeeId, skillId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted a technical skill, from a Perficient employee, by ID"); // code 204
        } catch (EmployeeNotFoundException | SkillNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOTFOUND_SKILL_OR_EMPLOYEE_STRING); // code 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UNKNOWN_ERROR_STRING);
        }
    }

// *******
// The following mapping handlers are to catch some improper URL formats that slip through other mappings
// *******

    // Catch-all for invalid Post URLs, e.g. localhost:8080/employees/ with nothing after, send BAD_REQUEST (code 400)
    @PostMapping("/**")
    public ResponseEntity<String> postInvalidURL() {
        System.out.println("POST request invalid URL format.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Badly formatted POST URL"); // code 400
    }

    // Catch-all for invalid Get URLs, e.g. localhost:8080/employees/ with nothing after, send BAD_REQUEST (code 400)
    @GetMapping("/**")
    public ResponseEntity<String> getInvalidURL() {
        System.out.println("GET request invalid URL format.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Badly formatted GET URL"); // code 400
    }

    // Catch-all for invalid Put URLs, e.g. localhost:8080/employees/ with nothing after, send BAD_REQUEST (code 400)
    @PutMapping("/**")
    public ResponseEntity<String> putInvalidURL() {
        System.out.println("PUT request invalid URL format.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Badly formatted PUT URL"); // code 400
    }

    // Catch-all for invalid Delete URLs, e.g. localhost:8080/employees/ with nothing after, send BAD_REQUEST (code 400)
    @DeleteMapping("/**")
    public ResponseEntity<String> removeEmployeeInvalidArgument() {
        System.out.println("DELETE request invalid URL format.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Badly formatted DELETE URL"); // code 400
    }
}