package org.scarter4work.charterhomeworkapi.service;

import lombok.extern.slf4j.Slf4j;
import org.scarter4work.charterhomeworkapi.model.Transaction;
import org.scarter4work.charterhomeworkapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class TransactionService {
    private static final int QUARTER_YEAR = 3;

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getLastQuarterTransactions(int customerId) {
        LocalDateTime dateTime = LocalDateTime.now().minusMonths(QUARTER_YEAR);
        return this.transactionRepository.findAllByTransactionDateAfterAndCustomerIdEquals(dateTime, customerId);
    }
}