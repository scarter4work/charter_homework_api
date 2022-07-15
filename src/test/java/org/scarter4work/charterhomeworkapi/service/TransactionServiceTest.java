package org.scarter4work.charterhomeworkapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scarter4work.charterhomeworkapi.model.Transaction;
import org.scarter4work.charterhomeworkapi.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
final class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void whenGetLastQuarterTransactions_thenSucceed() {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setType("type1");
        transaction.setAmount(BigDecimal.ONE);
        transaction.setCustomerId(1);
        List<Transaction> transactions = List.of(transaction);
        Mockito.when(
                transactionRepository.findAllByTransactionDateAfterAndCustomerIdEquals(any(), anyInt()))
                .thenReturn(transactions);
        List<Transaction> results =
                this.transactionService.getLastQuarterTransactions(1);
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(results, transactions);
    }

    @Test
    public void whenGetLastQuarterTransactions_thenThrowErrorOnRepositoryFetch() {
        RuntimeException expectedException = new RuntimeException("ex");
        Mockito.when(this.transactionRepository.findAllByTransactionDateAfterAndCustomerIdEquals(any(), anyInt())).thenThrow(expectedException);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            transactionService.getLastQuarterTransactions(1);
        });
        Assertions.assertEquals(exception.getMessage(), expectedException.getMessage());
    }
}