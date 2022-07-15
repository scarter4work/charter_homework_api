package org.scarter4work.charterhomeworkapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.scarter4work.charterhomeworkapi.dto.RewardDTO;
import org.scarter4work.charterhomeworkapi.model.Customer;
import org.scarter4work.charterhomeworkapi.model.Transaction;
import org.scarter4work.charterhomeworkapi.repository.CustomerRepository;
import org.scarter4work.charterhomeworkapi.service.CustomerService;
import org.scarter4work.charterhomeworkapi.service.RewardService;
import org.scarter4work.charterhomeworkapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RewardsController.class)
class RewardsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RewardService rewardService;

    @Test
    void whenGetRewards_thenReturnValidRewards() throws Exception {
        Customer customer = new Customer();
        customer.setName("test");
        customer.setId(1);

        Transaction transaction = new Transaction();
        transaction.setType("test");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(BigDecimal.valueOf(120));
        transaction.setCustomerId(1);
        transaction.setId(1);

        RewardDTO rewardDTO = new RewardDTO(customer.dto(), List.of(transaction.dto()), 90L);

        Mockito.when(rewardService.calculateRewardPoints(anyInt())).thenReturn(rewardDTO);

        mockMvc.perform(get("/api/rewards/quarterly/customer/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.points").isNotEmpty())
                .andExpect(jsonPath("$.points").value(90));
    }

    @Test
    void whenGetRewards_thenReturnNoContent() throws Exception {
        Mockito.when(rewardService.calculateRewardPoints(anyInt())).thenReturn(null);
        mockMvc.perform(get("/api/rewards/quarterly/customer/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetRewards_thenThrowExceptionInCalculation() throws Exception {
        Mockito.doThrow(new RuntimeException("ex")).when(rewardService).calculateRewardPoints(anyInt());
        mockMvc.perform(get("/api/rewards/quarterly/customer/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}