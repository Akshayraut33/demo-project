package com.splitsmart.splitwise.service;

import com.splitsmart.splitwise.dto.ExpenseDTO;
import com.splitsmart.splitwise.model.Expense;
import com.splitsmart.splitwise.model.User;
import com.splitsmart.splitwise.repository.ExpenseRepository;
import com.splitsmart.splitwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        Expense expense = new Expense();
        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDate(expenseDTO.getDate() != null ? expenseDTO.getDate() : LocalDateTime.now());

        Optional<User> paidBy = userRepository.findById(expenseDTO.getPaidById());
        if (paidBy.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        expense.setPaidBy(paidBy.get());

        List<User> participants = userRepository.findAllById(expenseDTO.getParticipantIds());
        expense.setParticipants(participants);

        expense = expenseRepository.save(expense);
        expenseDTO.setId(expense.getId());
        return expenseDTO;
    }

    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Map<Long, BigDecimal> getUserBalances(Long userId) {
        List<Expense> expenses = expenseRepository.findAll();
        Map<Long, BigDecimal> balances = new HashMap<>();

        for (Expense expense : expenses) {
            BigDecimal share = expense.getAmount().divide(BigDecimal.valueOf(expense.getParticipants().size()), 2, RoundingMode.HALF_UP);
            
            if (expense.getPaidBy().getId().equals(userId)) {
                // User paid, so others owe him
                for (User participant : expense.getParticipants()) {
                    if (!participant.getId().equals(userId)) {
                        balances.merge(participant.getId(), share, BigDecimal::add);
                    }
                }
            } else if (expense.getParticipants().stream().anyMatch(p -> p.getId().equals(userId))) {
                // User is participant but not payer, so he owes the payer
                balances.merge(expense.getPaidBy().getId(), share.negate(), BigDecimal::add);
            }
        }

        return balances;
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setPaidById(expense.getPaidBy().getId());
        dto.setParticipantIds(expense.getParticipants().stream().map(User::getId).collect(Collectors.toList()));
        return dto;
    }
}