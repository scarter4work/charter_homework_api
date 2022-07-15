package org.scarter4work.charterhomeworkapi.service;

import lombok.extern.slf4j.Slf4j;
import org.scarter4work.charterhomeworkapi.model.Customer;
import org.scarter4work.charterhomeworkapi.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomer(int customerId) {
        Optional<Customer> customerOptional = this.customerRepository.findById(customerId);
        return customerOptional.orElseThrow(
                () -> new RuntimeException(String.format("Customer was not found for id: %s", customerId)));
    }
}