package org.scarter4work.charterhomeworkapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.scarter4work.charterhomeworkapi.dto.TransactionDTO;
import org.scarter4work.charterhomeworkapi.model.Transaction;
import org.scarter4work.charterhomeworkapi.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class)
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TransactionRepository transactionRepository;

    @Test
    void whenGetAllTransactions_thenReturnATransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setType("test");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(BigDecimal.ONE);
        transaction.setCustomerId(1);
        transaction.setId(1);
        Mockito.when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/transaction")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*].amount").isNotEmpty())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(transaction.dto()))));
    }

    @Test
    void whenGetAllTransactions_thenReturnEmptyList() throws Exception {
        Mockito.when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/transaction")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.[*]").doesNotExist());
    }

    @Test
    void whenGetTransaction_thenReturnATransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setType("test");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(BigDecimal.ONE);
        transaction.setCustomerId(1);
        transaction.setId(1);
        Mockito.when(transactionRepository.findById(anyInt())).thenReturn(Optional.of(transaction));

        mockMvc.perform(get("/api/transaction/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.amount").isNotEmpty())
                .andExpect(content().string(objectMapper.writeValueAsString(transaction.dto())));
    }

    @Test
    void whenGetTransaction_thenReturnNoContent() throws Exception {
        Mockito.when(transactionRepository.findById(anyInt())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/transaction/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteTransaction_thenSucceed() throws Exception {
        Mockito.doNothing().when(transactionRepository).deleteById(anyInt());
        mockMvc.perform(delete("/api/transaction/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteTransaction_thenCauseServerError() throws Exception {
        Mockito.doThrow(new RuntimeException("ex")).when(transactionRepository).deleteById(anyInt());
        mockMvc.perform(delete("/api/transaction/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    // I realize some of these methods could be parameterized tests...
    @Test
    void whenDeleteTransaction_thenDoNotFindTransactionToDelete() throws Exception {
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(transactionRepository).deleteById(anyInt());
        mockMvc.perform(delete("/api/transaction/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createTransaction_thenReturnATransaction() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(null, "test", BigDecimal.ONE, 1, LocalDateTime.now());
        Transaction transaction = new Transaction(transactionDTO);
        transaction.setId(1);
        Mockito.when(transactionRepository.save(any())).thenReturn(transaction);

        mockMvc.perform(post("/api/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.amount").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(BigDecimal.ONE.doubleValue()))
                .andExpect(content().string(objectMapper.writeValueAsString(transaction.dto())));
    }

    @Test
    void createTransaction_thenThrowExceptionOnSave() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(null, "test", BigDecimal.ONE, 1, LocalDateTime.now());
        Mockito.doThrow(new RuntimeException("ex")).when(transactionRepository).save(any());

        mockMvc.perform(post("/api/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenUpdateTransaction_thenReturnUpdatedTransaction() throws Exception {
        TransactionDTO newTxData = new TransactionDTO(1, "test", BigDecimal.ONE, 1, LocalDateTime.now());
        Transaction dbTxData = new Transaction(newTxData);
        // modify db data different from new data
        dbTxData.setType("coinage");
        dbTxData.setAmount(BigDecimal.TEN);

        Mockito.when(transactionRepository.findById(anyInt())).thenReturn(Optional.of(dbTxData));
        Mockito.when(transactionRepository.save(any())).thenReturn(dbTxData);

        mockMvc.perform(put("/api/transaction/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTxData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.amount").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(BigDecimal.ONE.doubleValue()))
                .andExpect(content().string(objectMapper.writeValueAsString(dbTxData.dto())));
    }

    @Test
    void whenUpdateTransaction_thenDoNotFindAnExistingTransaction() throws Exception {
        TransactionDTO newTxData = new TransactionDTO(1, "test", BigDecimal.ONE, 1, LocalDateTime.now());

        Mockito.when(transactionRepository.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/transaction/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTxData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateTransaction_thenGenericExceptionOccursOnSave() throws Exception {
        TransactionDTO newTxData = new TransactionDTO(1, "test", BigDecimal.ONE, 1, LocalDateTime.now());
        Transaction dbTxData = new Transaction(newTxData);
        // modify db data different from new data
        dbTxData.setType("coinage");
        dbTxData.setAmount(BigDecimal.TEN);

        Mockito.when(transactionRepository.findById(anyInt())).thenReturn(Optional.of(dbTxData));
        Mockito.doThrow(new RuntimeException("ex")).when(transactionRepository).save(any());

        mockMvc.perform(put("/api/transaction/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTxData)))
                .andExpect(status().isInternalServerError());
    }
}