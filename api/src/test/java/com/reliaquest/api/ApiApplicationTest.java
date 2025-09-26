// This file was created by Matthew Schwartzkopf

package com.reliaquest.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.model.EmployeeSingleResponse;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class ApiApplicationTest {

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private EmployeeController controller;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        controller = new EmployeeController(restTemplate);
    }

    @Test
    void getAllEmployees_returnsListOfEmployees() {
        Employee e1 = new Employee();
        e1.setId("1");
        e1.setEmployee_name("Alice");
        Employee e2 = new Employee();
        e2.setId("2");
        e2.setEmployee_name("Bob");

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Arrays.asList(e1, e2));

        when(restTemplate.getForEntity(anyString(), eq(EmployeeResponse.class))).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<List<Employee>> result = controller.getAllEmployees();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    void getAllEmployees_handlesEmptyList() {
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(List.of());

        when(restTemplate.getForEntity(anyString(), eq(EmployeeResponse.class))).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<List<Employee>> result = controller.getAllEmployees();

        assertThat(result.getBody()).isEmpty();
    }

    @Test
    void getEmployeesByNameSearch_returnsMatches() {
        Employee e1 = new Employee();
        e1.setEmployee_name("Alice Wonderland");
        Employee e2 = new Employee();
        e2.setEmployee_name("Bob");

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Arrays.asList(e1, e2));

        when(restTemplate.getForEntity(anyString(), eq(EmployeeResponse.class))).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<List<Employee>> result = controller.getEmployeesByNameSearch("Alice");

        assertThat(result.getBody()).extracting(Employee::getEmployee_name).containsExactly("Alice Wonderland");
    }

    @Test
    void getEmployeesByNameSearch_returnsEmptyIfNoMatch() {
        Employee e1 = new Employee();
        e1.setEmployee_name("Alice");
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(List.of(e1));

        when(restTemplate.getForEntity(anyString(), eq(EmployeeResponse.class))).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<List<Employee>> result = controller.getEmployeesByNameSearch("Zoe");

        assertThat(result.getBody()).isEmpty();
    }

    @Test
    void getEmployeeById_returnsEmployee() {
        Employee e1 = new Employee();
        e1.setId("123");
        e1.setEmployee_name("Charlie");

        EmployeeSingleResponse singleResponse = new EmployeeSingleResponse();
        singleResponse.setData(e1);

        when(restTemplate.getForEntity(contains("/123"), eq(EmployeeSingleResponse.class))).thenReturn(ResponseEntity.ok(singleResponse));

        ResponseEntity<Employee> result = controller.getEmployeeById("123");

        assertThat(result.getBody().getEmployee_name()).isEqualTo("Charlie");
    }

    @Test
    void getEmployeeById_returnsNullIfNotFound() {
        when(restTemplate.getForEntity(contains("/999"), eq(EmployeeSingleResponse.class))).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        ResponseEntity<Employee> result = controller.getEmployeeById("999");

        assertThat(result.getBody()).isNull();
    }

    @Test
    void getHighestSalaryOfEmployees_returnsMaxSalary() {
        Employee e1 = new Employee();
        e1.setEmployee_salary(50000);
        Employee e2 = new Employee();
        e2.setEmployee_salary(100000);

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Arrays.asList(e1, e2));

        when(restTemplate.getForEntity(anyString(), eq(EmployeeResponse.class))).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Integer> result = controller.getHighestSalaryOfEmployees();

        assertThat(result.getBody()).isEqualTo(100000);
    }

    @Test
    void getHighestSalaryOfEmployees_returnsZeroWhenNoEmployees() {
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(List.of());

        when(restTemplate.getForEntity(anyString(), eq(EmployeeResponse.class))).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Integer> result = controller.getHighestSalaryOfEmployees();

        assertThat(result.getBody()).isEqualTo(0);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_returnsSortedTop10() {
        Employee e1 = new Employee();
        e1.setEmployee_name("Alice");
        e1.setEmployee_salary(50000);
        Employee e2 = new Employee();
        e2.setEmployee_name("Bob");
        e2.setEmployee_salary(200000);

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Arrays.asList(e1, e2));

        when(restTemplate.getForEntity(anyString(), eq(EmployeeResponse.class))).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<List<String>> result = controller.getTopTenHighestEarningEmployeeNames();

        assertThat(result.getBody()).containsExactly("Bob", "Alice");
    }

    @Test
    void createEmployee_returnsCreatedEmployee() {
        EmployeeInput input = new EmployeeInput();
        input.setName("Dora");
        input.setAge(30);
        input.setSalary(90000);
        input.setTitle("Dev");

        Employee e1 = new Employee();
        e1.setId("42");
        e1.setEmployee_name("Dora");

        EmployeeSingleResponse singleResponse = new EmployeeSingleResponse();
        singleResponse.setData(e1);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(EmployeeSingleResponse.class))).thenReturn(ResponseEntity.ok(singleResponse));

        ResponseEntity<Employee> result = controller.createEmployee(input);

        assertThat(result.getBody().getEmployee_name()).isEqualTo("Dora");
    }

    @Test
    void deleteEmployeeById_returnsOkWhenDeleted() {
        Employee e1 = new Employee();
        e1.setId("123");
        e1.setEmployee_name("Eve");

        EmployeeSingleResponse singleResponse = new EmployeeSingleResponse();
        singleResponse.setData(e1);

        when(restTemplate.getForEntity(contains("/123"), eq(EmployeeSingleResponse.class)))
                .thenReturn(ResponseEntity.ok(singleResponse));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<String> result = controller.deleteEmployeeById("123");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).contains("Employee Eve with id 123 deleted");
    }

    @Test
    void deleteEmployeeById_returns404IfNotFound() {
        when(restTemplate.getForEntity(contains("/999"), eq(EmployeeSingleResponse.class))).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        ResponseEntity<String> result = controller.deleteEmployeeById("999");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).contains("Employee with id 999 not found");
    }
}
