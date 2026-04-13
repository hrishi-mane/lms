package com.teamoffour.lms.service;

import com.teamoffour.lms.domain.*;
import com.teamoffour.lms.domain.enums.NotificationType;
import com.teamoffour.lms.repository.BookRepository;
import com.teamoffour.lms.repository.MemberRepository;
import com.teamoffour.lms.repository.TransactionRepository;
import com.teamoffour.lms.rest.NotificationServiceREST;
import com.teamoffour.lms.service.dto.NotificationEventDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TransactionService implements TransactionInterface {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationServiceREST notificationServiceREST;

    @Autowired
    public TransactionService(MemberRepository memberRepository,
                              BookRepository bookRepository,
                              TransactionRepository transactionRepository,
                              NotificationServiceREST notificationServiceREST) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.transactionRepository = transactionRepository;
        this.notificationServiceREST = notificationServiceREST;
    }

    /**
     * Borrow a book for a member.
     * <p>
     * Circuit breaker "bookService" guards the book repository interaction.
     * If the repository call fails (e.g. database unreachable in a future
     * persistent implementation), the circuit opens and the fallback is invoked
     * rather than letting errors cascade.
     * <p>
     * States: CLOSED → OPEN (after 50% failures in 5-call window)
     * OPEN   → HALF_OPEN (after 10 s wait)
     * HALF_OPEN → CLOSED (if 2 trial calls succeed)
     */
    @CircuitBreaker(name = "bookService", fallbackMethod = "borrowBookFallback")
    @Override
    public String borrowBook(Long bookId, Long memberId) throws ServerException {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        Book book = bookRepository.findBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

        if (!book.isAvailable() || book.hasAnyOngoingReservation()) {
            throw new ServerException(
                    "Book '" + book.getTitle() + "' is not available. " +
                            "Please reserve the book to join the reservation queue.");
        }

        if (!member.canBorrow()) {
            throw new ServerException(
                    "Borrowing limit exceeded. Current: " + member.getActiveBorrowCount() +
                            ", Limit: " + member.getMembershipPlan().getBorrowingPolicy().getBorrowingLimit());
        }

        book.decrementCopies();

        Transaction transaction = new Transaction(member, book);
        book.addTransaction(transaction);
        member.addTransaction(transaction);

        bookRepository.save(book);
        memberRepository.save(member);
        transactionRepository.save(transaction);

        sendNotification(member,
                "You have successfully borrowed '" + book.getTitle() + "'. " +
                        "Due date: " + transaction.calculateDueDate(),
                NotificationType.BORROWED);

        return "Book borrowed successfully. Transaction ID: " + transaction.getId() +
                "\nPlease note this ID — it is required when returning the book.";
    }

    /**
     * Fallback invoked when the bookService circuit breaker is OPEN,
     * or when borrowBook throws an unexpected runtime exception that
     * trips the breaker.
     * <p>
     * NOTE: The fallback signature must match borrowBook exactly
     * (same parameter types) with an additional Throwable/Exception parameter.
     */
    public String borrowBookFallback(Long bookId, Long memberId, Exception ex) {
        log.error("Circuit breaker OPEN for borrowBook — bookId={}, memberId={}, reason={}",
                bookId, memberId, ex.getMessage());
        return "The borrowing service is temporarily unavailable. " +
                "Please try again in a few moments. " +
                "(Circuit breaker is open — too many recent failures detected.)";
    }

    /**
     * Return a previously borrowed book.
     * <p>
     * Circuit breaker "bookService" also guards return operations since
     * they interact with the same book repository.
     */
    @CircuitBreaker(name = "bookService", fallbackMethod = "processReturnFallback")
    @Override
    public String processReturn(Long transactionId) {
        Transaction transaction = transactionRepository.findTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found with ID: " + transactionId));

        if (!transaction.isActive()) {
            throw new IllegalArgumentException(
                    "Transaction #" + transactionId + " is already closed. " +
                            "Status: " + transaction.getTransactionStatus());
        }

        Book book = transaction.getBook();
        book.incrementCopies();

        // Activate the next queued reservation if one exists
        List<Reservation> queuedReservations = book.getQueuedReservations();
        if (!queuedReservations.isEmpty()) {
            Reservation firstQueued = queuedReservations.get(0);
            Member reservingMember = firstQueued.getMember();
            firstQueued.activate();

            String reservationMessage = String.format(
                    "Great news! The book '%s' you reserved is now available for pickup. " +
                            "Please collect it within %d days (by %s) or your reservation will expire.",
                    book.getTitle(),
                    firstQueued.getDaysUntilExpiry(),
                    firstQueued.getExpiryDate());

            sendNotification(reservingMember, reservationMessage,
                    NotificationType.RESERVATION_AVAILABLE);
        }

        transaction.updateReturnedDate(LocalDateTime.now());
        LocalDateTime dueDate = transaction.calculateDueDate();
        transaction.markReturned();

        NotificationType notificationType;
        String message;

        if (transaction.isOverdue()) {
            Fine fine = transaction.calculateFine();
            message = String.format(
                    "Book '%s' returned LATE. Overdue fine: $%.2f (%.0f days late). " +
                            "Due date was: %s. Returned on: %s",
                    book.getTitle(),
                    fine.getAmount(),
                    (double) transaction.getDaysOverdue(),
                    dueDate,
                    transaction.getReturnedDate());
            notificationType = NotificationType.OVERDUE;
            log.warn("OVERDUE RETURN — fine: ${}", fine.getAmount());
        } else {
            message = String.format(
                    "Book '%s' returned successfully ON TIME. " +
                            "Thank you for returning by the due date (%s)!",
                    book.getTitle(), dueDate);
            notificationType = NotificationType.RETURNED;
            log.info("ON-TIME RETURN — no fine");
        }

        Member member = transaction.getMember();
        bookRepository.save(book);
        memberRepository.save(member);
        transactionRepository.save(transaction);

        sendNotification(member, message, notificationType);
        return message;
    }

    /**
     * Fallback for processReturn when the circuit is open.
     */
    public String processReturnFallback(Long transactionId, Exception ex) {
        log.error("Circuit breaker OPEN for processReturn — transactionId={}, reason={}",
                transactionId, ex.getMessage());
        return "The return service is temporarily unavailable. " +
                "Please try again in a few moments.";
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void sendNotification(Member member, String message, NotificationType type) {
        NotificationEventDTO notification = new NotificationEventDTO(
                member.getId(),
                member.getEmailId(),
                message,
                type.name()
        );

        notificationServiceREST.publish(notification);
    }
}
