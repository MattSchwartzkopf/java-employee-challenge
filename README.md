# ReliaQuest Coding Challenge

#### In this assessment you will be tasked with filling out the functionality of different methods that will be listed further down.

These methods will require some level of API interactions with Mock Employee API at http://localhost:8112/api/v1/employee.

Please keep the following in mind when doing this assessment: 
* clean coding practices
* test driven development 
* logging
* scalability

See the section **How to Run Mock Employee API** for further instruction on starting the Mock Employee API.

### Endpoints to implement (API module)

_See `com.reliaquest.api.controller.IEmployeeController` for details._

getAllEmployees()

    output - list of employees
    description - this should return all employees

getEmployeesByNameSearch(...)

    path input - name fragment
    output - list of employees
    description - this should return all employees whose name contains or matches the string input provided

getEmployeeById(...)

    path input - employee ID
    output - employee
    description - this should return a single employee

getHighestSalaryOfEmployees()

    output - integer of the highest salary
    description - this should return a single integer indicating the highest salary of amongst all employees

getTop10HighestEarningEmployeeNames()

    output - list of employees
    description - this should return a list of the top 10 employees based off of their salaries

createEmployee(...)

    body input - attributes necessary to create an employee
    output - employee
    description - this should return a single employee, if created, otherwise error

deleteEmployeeById(...)

    path input - employee ID
    output - name of the employee
    description - this should delete the employee with specified id given, otherwise error

### Endpoints from Mock Employee API (Server module)

    request:
        method: GET
        full route: http://localhost:8112/api/v1/employee
    response:
        {
            "data": [
                {
                    "id": "4a3a170b-22cd-4ac2-aad1-9bb5b34a1507",
                    "employee_name": "Tiger Nixon",
                    "employee_salary": 320800,
                    "employee_age": 61,
                    "employee_title": "Vice Chair Executive Principal of Chief Operations Implementation Specialist",
                    "employee_email": "tnixon@company.com",
                },
                ....
            ],
            "status": "Successfully processed request."
        }
---
    request:
        method: GET
        path: 
            id (String)
        full route: http://localhost:8112/api/v1/employee/{id}
        note: 404-Not Found, if entity is unrecognizable
    response:
        {
            "data": {
                "id": "5255f1a5-f9f7-4be5-829a-134bde088d17",
                "employee_name": "Bill Bob",
                "employee_salary": 89750,
                "employee_age": 24,
                "employee_title": "Documentation Engineer",
                "employee_email": "billBob@company.com",
            },
            "status": ....
        }
---
    request:
        method: POST
        body: 
            name (String | not blank),
            salary (Integer | greater than zero),
            age (Integer | min = 16, max = 75),
            title (String | not blank)
        full route: http://localhost:8112/api/v1/employee
    response:
        {
            "data": {
                "id": "d005f39a-beb8-4390-afec-fd54e91d94ee",
                "employee_name": "Jill Jenkins",
                "employee_salary": 139082,
                "employee_age": 48,
                "employee_title": "Financial Advisor",
                "employee_email": "jillj@company.com",
            },
            "status": ....
        }
---
    request:
        method: DELETE
        body:
            name (String | not blank)
        full route: http://localhost:8112/api/v1/employee/{name}
    response:
        {
            "data": true,
            "status": ....
        }

### How to Run Mock Employee API (Server module)

Start **Server** Spring Boot application.
`./gradlew server:bootRun`

Each invocation of **Server** application triggers a new list of mock employee data. While live testing, you'll want to keep 
this server running if you require consistent data. Additionally, the web server will randomly choose when to rate
limit requests, so keep this mind when designing/implementing the actual Employee API.

_Note_: Console logs each mock employee upon startup.

### Code Formatting

This project utilizes Gradle plugin [Diffplug Spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle) to enforce format
and style guidelines with every build. 

To resolve any errors, you must run **spotlessApply** task.

# Below are example Insomnia requests I sent to test the functionality
### Get All Employee:
<img width="1448" height="882" alt="image" src="https://github.com/user-attachments/assets/80522225-ea11-4c87-b249-34877f5cfeeb" />

### Get Employees By Name Search
<img width="1467" height="608" alt="image" src="https://github.com/user-attachments/assets/52641f78-6896-4594-9da9-dfa293b78b25" />

### Get Employee By Id
<img width="1482" height="382" alt="image" src="https://github.com/user-attachments/assets/de4fbe35-42ae-4293-b417-abcbe9edd280" />

### Get Highest Salary of Employees
<img width="1489" height="364" alt="image" src="https://github.com/user-attachments/assets/1e3ec6f4-6254-450d-bac9-a8199eebf8b0" />

### Get Top Ten Highest Earning Employee Names
<img width="1498" height="383" alt="image" src="https://github.com/user-attachments/assets/39af3ccb-9c9a-4cee-b999-f24a105f1c14" />

### Create Employee
<img width="1490" height="411" alt="image" src="https://github.com/user-attachments/assets/58674996-2414-4b70-9675-4953c4924297" />

### Proof of Created Employee
<img width="1488" height="456" alt="image" src="https://github.com/user-attachments/assets/af233f92-1839-4fcb-bd5d-e6f943bdddb4" />

### Delete Employee
<img width="1499" height="509" alt="image" src="https://github.com/user-attachments/assets/54e80d57-a252-4129-b15e-81f15dbd4efa" />

### Proof of Deleted Employee
<img width="1499" height="405" alt="image" src="https://github.com/user-attachments/assets/446ef781-d189-4f35-b9ea-af1c524a7998" />




`./gradlew spotlessApply`

