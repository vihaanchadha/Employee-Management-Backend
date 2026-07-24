package com.challenge.api.service;

import com.challenge.api.model.DefaultEmployee;
import com.challenge.api.model.Employee;
import com.challenge.api.request.CreateEmployeeRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {

    private final Map<UUID, Employee> employees = new ConcurrentHashMap<>();

    public List<Employee> getAllEmployees() {
        return employees.values().stream()
                .sorted(Comparator.comparing(Employee::getLastName).thenComparing(Employee::getFirstName))
                .toList();
    }

    public Optional<Employee> getEmployeeByUuid(UUID uuid) {
        return Optional.ofNullable(employees.get(uuid));
    }

    public Employee createEmployee(CreateEmployeeRequest request) {
        validateCreateEmployeeRequest(request);

        String firstName = request.getFirstName().trim();
        String lastName = request.getLastName().trim();

        UUID uuid = UUID.randomUUID();

        Employee employee = DefaultEmployee.builder()
                .uuid(uuid)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(firstName + " " + lastName)
                .salary(request.getSalary())
                .age(request.getAge())
                .jobTitle(request.getJobTitle().trim())
                .email(request.getEmail().trim())
                .contractHireDate(request.getContractHireDate())
                .contractTerminationDate(request.getContractTerminationDate())
                .build();

        employees.put(uuid, employee);
        return employee;
    }

    private void validateCreateEmployeeRequest(CreateEmployeeRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        if (isBlank(request.getFirstName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name is required");
        }

        if (isBlank(request.getLastName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name is required");
        }

        if (request.getSalary() == null || request.getSalary() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary must be zero or greater");
        }

        if (request.getAge() == null || request.getAge() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age must be zero or greater");
        }

        if (isBlank(request.getJobTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job title is required");
        }

        if (isBlank(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        if (request.getContractHireDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contract hire date is required");
        }

        if (request.getContractTerminationDate() != null
                && request.getContractTerminationDate().isBefore(request.getContractHireDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Contract termination date cannot precede the hire date");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
