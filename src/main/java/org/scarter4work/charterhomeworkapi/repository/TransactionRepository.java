package org.scarter4work.charterhomeworkapi.repository;

import org.scarter4work.charterhomeworkapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByTransactionDateAfterAndCustomerIdEquals(LocalDateTime dateTime, int customerId);
}