package com.example.demo.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.exception.EmployeeNotFoundException;
import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeTest;
import com.example.demo.repository.EmployeeRepository;

//@RunWith(MockitoJUnitRunner.class)
public class EmployeeControllerTest {

	//Mock: cria uma instancia de uma classe, porém Mockada. 
	//Se você chamar um metodo ele não irá chamar o metodo real, 
	//a não ser que você queira.
	@Mock
	private EmployeeRepository employeeRepository;

	//InjectMocks: criar uma intancia e injeta as dependências necessárias que estão anotadas com @Mock.
	@InjectMocks
	private EmployeeController employeeController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void all_test() {
		 List<Employee> inputemployees = createEmployeeMock();
		 when(employeeRepository.findAll()).thenReturn(inputemployees);
		 ResponseEntity<CollectionModel<EntityModel<Employee>>> employees = employeeController.all();
		 assertNotNull(employees);
		 assertEquals(HttpStatus.OK, employees.getStatusCode());
		 assertEquals(inputemployees.size(), employees.getBody().getContent().size());
		 int i = 0;
		 for (EntityModel<Employee> employee : employees.getBody().getContent()) {
			 assertEquals(inputemployees.get(i), employee.getContent());
			 ++i;
		}
	}

	@Test
	public void one_ok() {
		Employee employeeInput = EmployeeTest.create();
		//Matchers: permite a verificação por meio de matchers de argumentos (anyObject(), anyString() …)
		when(employeeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(employeeInput));
		ResponseEntity<EntityModel<Employee>> employeeOutput = employeeController.one(1L);
		assertEquals(employeeInput.getName(), employeeOutput.getBody().getContent().getName());
		assertEquals(employeeInput.getRole(), employeeOutput.getBody().getContent().getRole());
	}

	@Test(expected = EmployeeNotFoundException.class)
	public void one_NotOk() {
		employeeController.one(2L);
	}

	@Test
	public void newEmployee_test() {
		Employee employee = EmployeeTest.create();
		when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
		ResponseEntity<Employee> newEmployee = employeeController.newEmployee(employee);
		assertEquals(employee.getName(), newEmployee.getBody().getName());
		assertEquals(employee.getRole(), newEmployee.getBody().getRole());
	}

	@Test
	public void replaceEmployee() {
		Employee employee = EmployeeTest.create();
		//When: Após um mock ser criado, você pode configurar ações na chamada e o retorno.
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
		when(employeeRepository.save(employee)).thenReturn(employee);
		employee.setName("altName");
		ResponseEntity<Employee> employeereplace = employeeController.replaceEmployee(employee, 1L);
		assertEquals("altName", employeereplace.getBody().getName());
	}

	@Test()
	public void replaceEmployeeNotFound() {
		Employee employee = new Employee();
		assertEquals(HttpStatus.NO_CONTENT, employeeController.replaceEmployee(employee, 5L).getStatusCode());
	}

	/*
	 * Testando um metodo void
	 * Primeiro chamamos o método que vai ser testado e passamos os
	 * parâmetros necessários.
	 * Em seguida usamos o Mockito.verify para verificar se durante a execução das classes mocadas foi chamado o método em questão. 
	 * Podemos verificar varias coisas com Mockito.verify numero de vezes que executou, parâmetros recebidos e etc.
	 */
	@Test
	public void deleteEmployee() {
		employeeController.deleteEmployee(1L);
		//Verify: verifica a quantidade de vezes e quais parametros 
		//utilizados para acessar um determinado metodo.
		Mockito.verify(employeeRepository, Mockito.times(1)).deleteById(1L);
	}
	
	@Spy
	List<String> list = new ArrayList<String>();

	@Test
	public void spyTest() {
		//Spy: cria uma instancia de uma classe, que você pode mockar ou chamar 
		//os metodos reais. 
		//É uma alternativa ao InjectMocks, quando é preciso mockar 
		//metodos da propria classe que esta sendo testada.
//		List<Employee> employees = Mockito.spy(new ArrayList<Employee>());
//		Employee one = new Employee("Bilbo Baggins", "burglar");
//		Employee two = new Employee("Frodo Baggins", "thief");
//
//		employees.add(one);
//		employees.add(two);
//
//		Mockito.verify(employees).add(one);
//		Mockito.verify(employees).add(two);
//		assertEquals(2, employees.size());
//
//		Mockito.doReturn(100).when(employees).size();
//		assertEquals(100, employees.size());
		
		
//	    List<String> spyList = Mockito.spy(list);
//
//	    spyList.add("one");
//	    spyList.add("two");
//
//	    Mockito.verify(spyList).add("one");
//	    Mockito.verify(spyList).add("two");
//
//	    assertEquals(2, spyList.size());
	}

	private List<Employee> createEmployeeMock() {
		List<Employee> all = new ArrayList<Employee>();
		all.add(new Employee("Bilbo Baggins", "burglar"));
		all.add(new Employee("Frodo Baggins", "thief"));
		return all;
	}
}
