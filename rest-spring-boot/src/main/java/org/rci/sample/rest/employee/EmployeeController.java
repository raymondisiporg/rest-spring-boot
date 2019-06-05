package org.rci.sample.rest.employee;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
 
@RestController
@RequestMapping(path = "/employees")
public class EmployeeController
{
    @Autowired
    private EmployeeDAO employeeDao;
     
    @GetMapping(path="", produces = "application/json")
    public Employees getEmployees()
    {
        return employeeDao.getAllEmployees();
    }
    
   @GetMapping(path="/{employeeId}", produces = "application/json")
   public Employee getEmployee(@PathVariable("employeeId") int employeeId)
   {
	   Employee matchingEmployee = employeeDao.getAllEmployees().getEmployeeList().stream()
			   		.filter(emp -> emp.getId().intValue() == employeeId).findFirst().orElse(null);
        if (matchingEmployee == null) {
        	throw new ResponseStatusException(
        			  HttpStatus.NOT_FOUND, "Employee not found"
        			);
        }
        return employeeDao.getAllEmployees().getEmployeeList().get(employeeId - 1);
   }
     
    @PostMapping(path= "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addEmployee(@RequestBody Employee employee)
    {
        Integer id = employeeDao.getAllEmployees().getEmployeeList().size() + 1;
        employee.setId(id);
         
        employeeDao.addEmployee(employee);
         
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                    .path("/{id}")
                                    .buildAndExpand(employee.getId())
                                    .toUri();
         
        return ResponseEntity.created(location).build();
    }
    
   @PutMapping(path= "", consumes = "application/json", produces = "application/json")
   public ResponseEntity<Object> updateEmployee(@RequestBody Employee employee)
   {
	   Employee matchingEmployee = employeeDao.getAllEmployees().getEmployeeList().stream()
		   		.filter(emp -> emp.getId() == employee.getId()).findFirst().orElse(null);
       if (matchingEmployee == null) {
       	throw new ResponseStatusException(
       			  HttpStatus.NOT_FOUND, "Employee not found"
       			);
       }
	   matchingEmployee.setEmail(employee.getEmail());
	   matchingEmployee.setFirstName(employee.getFirstName());
	   matchingEmployee.setLastName(employee.getLastName());
        
       return ResponseEntity.status(HttpStatus.OK).body(matchingEmployee);
   }
   

   
   @DeleteMapping(path="/{employeeId}", produces = "application/json")
   public ResponseEntity<Object> deleteEmployee(@PathVariable("employeeId") int employeeId)
   {
	   Employee matchingEmployee = employeeDao.getAllEmployees().getEmployeeList().stream()
		   		.filter(emp -> emp.getId() == employeeId).findFirst().orElse(null);
	   
        if (matchingEmployee == null) {
        	throw new ResponseStatusException(
        			  HttpStatus.NOT_FOUND, "Employee not found"
        			);
        }
 	   List<Employee> matchingEmployees = employeeDao.getAllEmployees().getEmployeeList().stream()
		   		.filter(emp -> emp.getId().intValue() != employeeId).collect(Collectors.toList());
        
        employeeDao.getAllEmployees().getEmployeeList().clear();
        employeeDao.getAllEmployees().getEmployeeList().addAll(matchingEmployees);
        return ResponseEntity.accepted().build();
   }
}
