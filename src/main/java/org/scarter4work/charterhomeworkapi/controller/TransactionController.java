package org.scarter4work.charterhomeworkapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.scarter4work.charterhomeworkapi.dto.TransactionDTO;
import org.scarter4work.charterhomeworkapi.model.Transaction;
import org.scarter4work.charterhomeworkapi.repository.TransactionRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transaction")
@Slf4j
public class TransactionController {
    private static final String ID = "id";

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactionDTOs =
                this.transactionRepository.findAll().stream()
                        .map(Transaction::dto)
                        .collect(Collectors.toList());
        if (transactionDTOs.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable(ID) Integer id) {
        if (id == null) return ResponseEntity.badRequest().build();
        Optional<Transaction> transactionOptional;
        try {
            transactionOptional = this.transactionRepository.findById(id);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return transactionOptional
                .map(transaction -> ResponseEntity.ok(transaction.dto()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTransaction(@PathVariable(ID) Integer id) {
        if (id == null) return ResponseEntity.badRequest().build();
        try {
            this.transactionRepository.deleteById(id);
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        /// TODO: Validation would normally be required here
        if (transactionDTO == null) return ResponseEntity.badRequest().build();
        Transaction transaction;
        try {
            transaction = new Transaction(transactionDTO);
            transaction = this.transactionRepository.save(transaction);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.created(URI.create("/api/transaction/" + transaction.getId())).body(transaction.dto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable(ID) Integer id,
                                                            @RequestBody TransactionDTO transactionDTO) {
        /// TODO: Validation would normally be required here
        if (transactionDTO == null) return ResponseEntity.badRequest().build();
        Transaction transaction;
        try {
            Optional<Transaction> transactionOptional = this.transactionRepository.findById(id);
            if (transactionOptional.isPresent()) {
                transaction = transactionOptional.get();
                transaction.setTransactionDate(transactionDTO.getTransactionDate());
                transaction.setAmount(transactionDTO.getAmount());
                transaction.setCustomerId(transactionDTO.getCustomerId());
                transaction.setType(transactionDTO.getType());
                transaction = this.transactionRepository.save(transaction);
            } else return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(transaction.dto());
    }
}