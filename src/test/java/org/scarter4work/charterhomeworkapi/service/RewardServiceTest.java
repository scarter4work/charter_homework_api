package org.scarter4work.charterhomeworkapi.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.scarter4work.charterhomeworkapi.dto.CustomerDTO;
import org.scarter4work.charterhomeworkapi.dto.RewardDTO;
import org.scarter4work.charterhomeworkapi.dto.TransactionDTO;
import org.scarter4work.charterhomeworkapi.model.Customer;
import org.scarter4work.charterhomeworkapi.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {
    @Mock
    private CustomerService customerService;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private RewardService rewardService;

    @ParameterizedTest(name = "{index} => {0}: ({1}, {2}) and customerId: {3}")
    @CsvSource(delimiter = '|', textBlock = """
        '120 = 90 points'       |  120  |  90  |  1
        'negative dollars'      |  -1   |  0   |  2
        '49 points edge case'   |  49   |  0   |  3
        '50 points edge case'   |  50   |  0   |  3
        '51 points edge case'   |  51   |  1   |  3
        '99 points edge case'   |  99   |  49  |  3
        '100 points edge case'  |  100  |  50  |  4
        '101 points edge case'  |  101  |  52  |  4
        '200 points edge case'  |  200  |  250 |  5
    """)
    public void whenCalculateRewardPoints(String description, Integer dollarsSpent,
                                          Long expectedPoints, Integer customerId) {
        CustomerDTO customerDTO = new CustomerDTO(customerId,"tester" + customerId);
        Customer customer = new Customer(customerDTO);
        customer.setId(customerId);

        TransactionDTO transactionDTO =
                new TransactionDTO(1, "test", BigDecimal.valueOf(dollarsSpent),
                        customerDTO.getId(), LocalDateTime.now());
        Transaction transaction = new Transaction(transactionDTO);
        transaction.setId(1);
        List<Transaction> transactions = List.of(transaction);

        Mockito.when(customerService.getCustomer(anyInt())).thenReturn(customer);
        Mockito.when(transactionService.getLastQuarterTransactions(anyInt())).thenReturn(transactions);

        RewardDTO result = this.rewardService.calculateRewardPoints(customerId);
        Assertions.assertEquals(result.getPoints(), expectedPoints, description);
    }

    @Test
    public void whenGetLastQuarterTransactions_thenThrowErrorOnTransactionServiceFetch() {
        RuntimeException expectedException = new RuntimeException("ex");
        Mockito.when(this.transactionService.getLastQuarterTransactions(anyInt())).thenThrow(expectedException);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            transactionService.getLastQuarterTransactions(1);
        });
        Assertions.assertEquals(exception.getMessage(), expectedException.getMessage());
    }

    @Test
    public void whenGetLastQuarterTransactions_thenThrowErrorOnCustomerServiceFetch() {
        RuntimeException expectedException = new RuntimeException("ex");
        Mockito.when(this.customerService.getCustomer(anyInt())).thenThrow(expectedException);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            customerService.getCustomer(1);
        });
        Assertions.assertEquals(exception.getMessage(), expectedException.getMessage());
    }
}