// This file was created by Matthew Schwartzkopf

package com.reliaquest.api.model;

import java.util.List;
import lombok.Data;

@Data
public class EmployeeResponse {
    private List<Employee> data;
    private String status;
}
