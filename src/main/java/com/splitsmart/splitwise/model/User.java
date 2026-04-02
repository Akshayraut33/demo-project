package com.splitsmart.splitwise.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @OneToMany(mappedBy = "paidBy", cascade = CascadeType.ALL)
    private List<Expense> expensesPaid;

    @ManyToMany(mappedBy = "participants")
    private List<Expense> expensesParticipated;
}