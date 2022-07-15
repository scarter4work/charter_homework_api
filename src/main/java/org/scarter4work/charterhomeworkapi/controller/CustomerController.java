package org.scarter4work.charterhomeworkapi.controller;

import org.scarter4work.charterhomeworkapi.dto.CustomerDTO;
import org.scarter4work.charterhomeworkapi.model.Customer;
import org.scarter4work.charterhomeworkapi.repository.CustomerRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private static final String ID = "id";

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customerDTOs =
                this.customerRepository.findAll().stream()
                        .map(Customer::dto)
                        .collect(Collectors.toList());
        if (customerDTOs.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(customerDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable(ID) Integer id) {
        if (id == null) return ResponseEntity.badRequest().build();
        Optional<Customer> customerOptional;
        try {
            customerOptional = this.customerRepository.findById(id);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return customerOptional
                .map(customer -> ResponseEntity.ok(customer.dto()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") Integer id) {
        if (id == null) return ResponseEntity.badRequest().build();
        try {
            this.customerRepository.deleteById(id);
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        if (customerDTO == null) return ResponseEntity.badRequest().build();
        Customer customer;
        try {
            customer = new Customer(customerDTO);
            customer = this.customerRepository.save(customer);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.created(URI.create("/api/customer/" + customer.getId())).body(customer.dto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable(ID) Integer id,
                                                      @RequestBody CustomerDTO customerDTO) {
        /// TODO: Validation would normally be required here
        if (customerDTO == null) return ResponseEntity.badRequest().build();
        Customer customer;
        try {
            customer = new Customer(customerDTO);
            Optional<Customer> customerOptional = this.customerRepository.findById(id);

            if (customerOptional.isPresent()) {
                customer = customerOptional.get();
                customer.setName(customerDTO.getName());
                customer = this.customerRepository.save(customer);
            } else return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(customer.dto());
    }
}