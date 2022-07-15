package org.scarter4work.charterhomeworkapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scarter4work.charterhomeworkapi.model.Customer;
import org.scarter4work.charterhomeworkapi.repository.CustomerRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void whenGetCustomer_thenSucceed() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("test");
        Mockito.when(this.customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        Customer result = this.customerService.getCustomer(1);
        Assertions.assertEquals(result.getName(), customer.getName());
    }

    @Test
    public void whenGetCustomer_thenFailOnRepositoryFetch() {
        RuntimeException expectedException = new RuntimeException("ex");
        Mockito.when(this.customerRepository.findById(anyInt())).thenThrow(expectedException);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            customerService.getCustomer(1);
        });
        Assertions.assertEquals(exception.getMessage(), expectedException.getMessage());
    }

    @Test
    public void whenGetLastQuarterTransactions_thenThrowOnEmptyOptional() {
        int customerId = 1;
        Optional<Customer> customerOptional = Optional.empty();
        RuntimeException expectedException = new RuntimeException(String.format("Customer was not found for id: %s", customerId));
        Mockito.when(this.customerRepository.findById(anyInt())).thenReturn(customerOptional);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            customerService.getCustomer(1);
        });
        Assertions.assertEquals(exception.getMessage(), expectedException.getMessage());
    }
}