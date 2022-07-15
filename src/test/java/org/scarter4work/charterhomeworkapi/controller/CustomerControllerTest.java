package org.scarter4work.charterhomeworkapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.scarter4work.charterhomeworkapi.dto.CustomerDTO;
import org.scarter4work.charterhomeworkapi.model.Customer;
import org.scarter4work.charterhomeworkapi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CustomerRepository customerRepository;

    @Test
    void whenGetAllCustomers_thenReturnACustomer() throws Exception {
        Customer customer = new Customer();
        customer.setName("test");
        customer.setId(1);
        Mockito.when(customerRepository.findAll()).thenReturn(List.of(customer));

        mockMvc.perform(get("/api/customer")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*].name").isNotEmpty())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(customer.dto()))));
    }

    @Test
    void whenGetAllCustomers_thenReturnEmptyList() throws Exception {
        Mockito.when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/customer")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.[*]").doesNotExist());
    }

    @Test
    void whenGetCustomer_thenReturnACustomer() throws Exception {
        Customer customer = new Customer();
        customer.setName("test");
        customer.setId(1);
        Mockito.when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/api/customer/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(content().string(objectMapper.writeValueAsString(customer.dto())));
    }

    @Test
    void whenGetCustomer_thenReturnNoContent() throws Exception {
        Mockito.when(customerRepository.findById(anyInt())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/customer/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteCustomer_thenSucceed() throws Exception {
        Mockito.doNothing().when(customerRepository).deleteById(anyInt());
        mockMvc.perform(delete("/api/customer/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteCustomer_thenCauseServerError() throws Exception {
        Mockito.doThrow(new RuntimeException("ex")).when(customerRepository).deleteById(anyInt());
        mockMvc.perform(delete("/api/customer/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    // I realize some of these methods could be parameterized tests...
    @Test
    void whenDeleteCustomer_thenDoNotFindCustomerToDelete() throws Exception {
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(customerRepository).deleteById(anyInt());
        mockMvc.perform(delete("/api/customer/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createCustomer_thenReturnACustomer() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "test");
        Customer customer = new Customer(customerDTO);
        customer.setId(1);
        Mockito.when(customerRepository.save(any())).thenReturn(customer);

        mockMvc.perform(post("/api/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(content().string(objectMapper.writeValueAsString(customer.dto())));
    }

    @Test
    void createCustomer_thenThrowExceptionOnSave() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(null, "test");
        Mockito.doThrow(new RuntimeException("ex")).when(customerRepository).save(any());

        mockMvc.perform(post("/api/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenUpdateCustomer_thenReturnUpdatedCustomer() throws Exception {
        CustomerDTO newTxData = new CustomerDTO(1, "test");
        Customer dbTxData = new Customer(newTxData);
        // modify db data different from new data
        dbTxData.setName("coinage");

        Mockito.when(customerRepository.findById(anyInt())).thenReturn(Optional.of(dbTxData));
        Mockito.when(customerRepository.save(any())).thenReturn(dbTxData);

        mockMvc.perform(put("/api/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTxData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(content().string(objectMapper.writeValueAsString(dbTxData.dto())));
    }

    @Test
    void whenUpdateCustomer_thenDoNotFindAnExistingCustomer() throws Exception {
        CustomerDTO newTxData = new CustomerDTO(1, "test");

        Mockito.when(customerRepository.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTxData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateCustomer_thenGenericExceptionOccursOnSave() throws Exception {
        CustomerDTO newTxData = new CustomerDTO(1, "test");
        Customer dbTxData = new Customer(newTxData);
        // modify db data different from new data
        dbTxData.setName("coinage");

        Mockito.when(customerRepository.findById(anyInt())).thenReturn(Optional.of(dbTxData));
        Mockito.doThrow(new RuntimeException("ex")).when(customerRepository).save(any());

        mockMvc.perform(put("/api/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTxData)))
                .andExpect(status().isInternalServerError());
    }
}