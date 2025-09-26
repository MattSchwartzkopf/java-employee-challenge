// This file was created by Matthew Schwartzkopf

package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.model.EmployeeSingleResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final RestTemplate restTemplate;
    private static final String SERVER_URL = "http://localhost:8112/api/v1/employee";

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        ResponseEntity<EmployeeResponse> response = restTemplate.getForEntity(SERVER_URL, EmployeeResponse.class);
        return ResponseEntity.ok(response.getBody().getData());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> employees = getAllEmployees().getBody();
        List<Employee> filtered = employees.stream()
                .filter(e -> e.getEmployee_name().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        ResponseEntity<EmployeeSingleResponse> response =
                restTemplate.getForEntity(SERVER_URL + "/" + id, EmployeeSingleResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().getData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(response.getBody().getData());
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        List<Employee> employees = getAllEmployees().getBody();
        Integer highestSalary = employees.stream()
                .map(Employee::getEmployee_salary)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);

        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<Employee> employees = getAllEmployees().getBody();
        List<String> topTenHighestSalary = employees.stream()
                .sorted(Comparator.comparing(Employee::getEmployee_salary).reversed())
                .limit(10)
                .map(Employee::getEmployee_name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(topTenHighestSalary);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(EmployeeInput employeeInput) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EmployeeInput> request = new HttpEntity<>(employeeInput, headers);
        ResponseEntity<EmployeeSingleResponse> response =
                restTemplate.postForEntity(SERVER_URL, request, EmployeeSingleResponse.class);
        return ResponseEntity.ok(response.getBody().getData());
    }

    /*
    This method is where the intentional bug is. The method in IEmployeeController for `deleteEmployeeById` intends on
    sending a String id, but the server-side is expecting a requestBody with a name (not id). Below I worked the method
    so that it retrieves the employee information by id, grabs the employee name, and sends the employee name to the
    server-side DELETE to allow for employee account to be deleted. :)
     */
    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        // Fetch all data for the employee by ID
        ResponseEntity<EmployeeSingleResponse> response =
                restTemplate.getForEntity(SERVER_URL + "/" + id, EmployeeSingleResponse.class);

        // Handle error cases
        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().getData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with id " + id + " not found");
        }

        // Get the employee's name
        String name = response.getBody().getData().getEmployee_name();

        // Build request body and headers for DELETE
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"name\": \"%s\"}", name);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        restTemplate.exchange(SERVER_URL, HttpMethod.DELETE, request, Void.class);

        return ResponseEntity.ok("Employee " + name + " with id " + id + " deleted");
    }
}
