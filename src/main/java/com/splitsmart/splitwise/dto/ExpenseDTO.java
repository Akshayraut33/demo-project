package com.splitsmart.splitwise.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExpenseDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
    private Long paidById;
    private List<Long> participantIds;
}