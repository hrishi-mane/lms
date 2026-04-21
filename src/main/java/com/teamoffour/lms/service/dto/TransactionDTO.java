package com.teamoffour.lms.service.dto;

import com.teamoffour.lms.domain.enums.TransactionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long memberId;
    private String memberUserName;
    private LocalDateTime borrowDate;
    private LocalDateTime returnedDate;
    private LocalDateTime dueDate;
    private TransactionStatus transactionStatus;
    private boolean overdue;
    private long daysOverdue;
}
