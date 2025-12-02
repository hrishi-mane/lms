package com.teamoffour.lms.service;

import com.teamoffour.lms.domain.*;
import com.teamoffour.lms.domain.enums.NotificationType;
import com.teamoffour.lms.repository.BookRepository;
import com.teamoffour.lms.repository.MemberRepository;
import com.teamoffour.lms.repository.TransactionRepository;
import com.teamoffour.lms.service.observer.NotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService implements TransactionInterface {
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationManager notificationManager;

    @Autowired
    public TransactionService(MemberRepository memberRepository,
                              BookRepository bookRepository,
                              TransactionRepository transactionRepository,
                              NotificationManager notificationManager) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.transactionRepository = transactionRepository;
        this.notificationManager = notificationManager;
    }

    @Override
    public String borrowBook(Long bookId, Long memberId) throws ServerException {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Book book = bookRepository.findBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (!book.isAvailable() || book.hasAnyOngoingReservation()) {
            throw new ServerException(
                    "Book '" + book.getTitle() + "' is not available. Please reserve the book to get into reservation queue.");
        }

        if (!member.canBorrow()) {
            throw new ServerException(
                    "Borrowing limit exceeded. Current: " + member.getActiveBorrowCount() +
                            ", Limit: " + member.getMembershipPlan().getBorrowingPolicy().getBorrowingLimit());
        }

        book.decrementCopies();

        Transaction transaction = new Transaction(member, book);
        book.addTransaction(transaction);
        member.addTransaction(transaction); // optional, if Member tracks transactions


        bookRepository.save(book);
        memberRepository.save(member);
        transactionRepository.save(transaction);

        sendNotification(member, "You have successfully borrowed '" + book.getTitle() + "'. " +
                "Due date: " + transaction.calculateDueDate(), NotificationType.BORROWED);


        return "Book borrowed successfully with transactionId : "
                + transaction.getId()
                + "\n"
                + "Make sure to note the ID as it will be required when returning the book!";
    }

    @Override
    public String processReturn(Long transactionId) {
        Transaction transaction = transactionRepository.findTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (!transaction.isActive()) {
            throw new IllegalArgumentException(
                    "Transaction #" + transactionId + " is already closed. " +
                            "Status: " + transaction.getTransactionStatus());
        }

        Book book = transaction.getBook();
        book.incrementCopies();

        List<Reservation> queuedReservations = book.getQueuedReservations();

        if (!queuedReservations.isEmpty()) {
            //Get the first queued reservation;
            Reservation firstQueuedReservation = queuedReservations.get(0);
            Member reservedMember = firstQueuedReservation.getMember();

            firstQueuedReservation.activate();
            String reservationMessage = String.format(
                    "Good news! The book '%s' you reserved is now available for pickup. " +
                            "Please collect it within %d days (by %s) or your reservation will expire.",
                    book.getTitle(),
                    firstQueuedReservation.getDaysUntilExpiry(),
                    firstQueuedReservation.getExpiryDate()
            );

            sendNotification(reservedMember, reservationMessage, NotificationType.RESERVATION_AVAILABLE);


        }

        transaction.updateReturnedDate(LocalDateTime.now());
        LocalDateTime transactionDueDate = transaction.calculateDueDate();

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
                    transaction.getDaysOverdue(),
                    transactionDueDate,
                    transaction.getReturnedDate()
            );
            notificationType = NotificationType.OVERDUE;

            System.out.println("⚠️ OVERDUE RETURN - Fine calculated: $" + fine.getAmount());
        } else {
            message = String.format(
                    "Book '%s' returned successfully ON TIME. " +
                            "Thank you for returning by the due date (%s)!",
                    book.getTitle(),
                    transactionDueDate
            );
            notificationType = NotificationType.RETURNED;

            System.out.println("✅ ON-TIME RETURN - No fine");
        }
        Member member = transaction.getMember();

        bookRepository.save(book);
        memberRepository.save(member);
        transactionRepository.save(transaction);


        sendNotification(member, message, notificationType);

        return message;

    }

    private void sendNotification(Member member, String message, NotificationType notificationType) {
        Notification notification = new Notification(
                member,
                message,
                notificationType
        );
        notificationManager.notifyObservers(notification);
    }
}
