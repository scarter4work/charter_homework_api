package org.scarter4work.charterhomeworkapi.service;

import lombok.extern.slf4j.Slf4j;
import org.scarter4work.charterhomeworkapi.dto.CustomerDTO;
import org.scarter4work.charterhomeworkapi.dto.RewardDTO;
import org.scarter4work.charterhomeworkapi.dto.TransactionDTO;
import org.scarter4work.charterhomeworkapi.model.Customer;
import org.scarter4work.charterhomeworkapi.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@Slf4j
public class RewardService {
    private static final int DOUBLE_POINTS = 2;
    private static final int ONE_HUNDRED = 100;
    private static final int FIFTY = 50;

    private final CustomerService customerService;
    private final TransactionService transactionService;

    public RewardService(CustomerService customerService, TransactionService transactionService) {
        this.transactionService = transactionService;
        this.customerService = customerService;
    }

    public RewardDTO calculateRewardPoints(Integer customerId) {
        // get customer and transaction data
        Customer customer = customerService.getCustomer(customerId);
        List<Transaction> transactions = transactionService.getLastQuarterTransactions(customerId);

        // calculate total amount for this customer for the previous quarter
        BigDecimal transactionTotalForQuarter =
                transactions.stream()
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        // calculate points based on total amount
        long pointsOverOneHundred = BigDecimal.ZERO.longValue();
        long pointsUnderOneHundred = BigDecimal.ZERO.longValue();
        if (transactionTotalForQuarter.signum() >= BigDecimal.ZERO.longValue()) {
            pointsOverOneHundred = transactionTotalForQuarter.subtract(BigDecimal.valueOf(ONE_HUNDRED)).longValue();
            pointsOverOneHundred = Math.max(pointsOverOneHundred, BigDecimal.ZERO.longValue());

            pointsUnderOneHundred = transactionTotalForQuarter.subtract(BigDecimal.valueOf(pointsOverOneHundred))
                    .subtract(BigDecimal.valueOf(FIFTY)).longValue();
            pointsUnderOneHundred = Math.max(pointsUnderOneHundred, BigDecimal.ZERO.longValue());
        }

        Long points = (DOUBLE_POINTS * pointsOverOneHundred) + pointsUnderOneHundred;

        // create the response object
        CustomerDTO customerDTO = customer.dto();
        List<TransactionDTO> transactionDTOs = transactions.stream().map(Transaction::dto).toList();

        return new RewardDTO(customerDTO, transactionDTOs, points);
    }
}