package com.splitsmart.splitwise.controller;

import com.splitsmart.splitwise.dto.ExpenseDTO;
import com.splitsmart.splitwise.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO createdExpense = expenseService.createExpense(expenseDTO);
        return ResponseEntity.ok(createdExpense);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses() {
        List<ExpenseDTO> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/balances/{userId}")
    public ResponseEntity<Map<Long, BigDecimal>> getUserBalances(@PathVariable Long userId) {
        Map<Long, BigDecimal> balances = expenseService.getUserBalances(userId);
        return ResponseEntity.ok(balances);
    }
}