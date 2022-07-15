package org.scarter4work.charterhomeworkapi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.scarter4work.charterhomeworkapi.dto.TransactionDTO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "transaction", schema = "rewards")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "type", length = 256)
    private String type;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    public Transaction(TransactionDTO transactionDTO) {
        this.type = transactionDTO.getType();
        this.amount = transactionDTO.getAmount();
        this.customerId = transactionDTO.getCustomerId();
        this.transactionDate = transactionDTO.getTransactionDate();
    }

    public TransactionDTO dto() {
        return new TransactionDTO(id, type, amount, customerId, transactionDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transaction that = (Transaction) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}