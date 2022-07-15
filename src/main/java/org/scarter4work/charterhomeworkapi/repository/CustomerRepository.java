package org.scarter4work.charterhomeworkapi.repository;

import org.scarter4work.charterhomeworkapi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}