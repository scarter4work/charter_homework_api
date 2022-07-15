package org.scarter4work.charterhomeworkapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class RewardDTO {
    private final CustomerDTO customer;
    private final List<TransactionDTO> transactions;
    private final Long points;

    public RewardDTO(CustomerDTO customer, List<TransactionDTO> transactions, Long points) {
        this.customer = customer;
        this.transactions = transactions;
        this.points = points;
    }
}