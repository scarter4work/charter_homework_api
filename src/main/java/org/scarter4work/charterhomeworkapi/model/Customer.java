package org.scarter4work.charterhomeworkapi.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.scarter4work.charterhomeworkapi.dto.CustomerDTO;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "customer", schema = "rewards")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 250)
    private String name;

    public Customer(CustomerDTO customerDTO) {
        this.name = customerDTO.getName();
    }

    public CustomerDTO dto() {
        return new CustomerDTO(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Customer customer = (Customer) o;
        return id != null && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}