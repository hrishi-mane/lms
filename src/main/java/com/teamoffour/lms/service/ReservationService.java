package com.teamoffour.lms.service;

import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.domain.Member;
import com.teamoffour.lms.domain.Reservation;
import com.teamoffour.lms.domain.Transaction;
import com.teamoffour.lms.domain.enums.NotificationType;
import com.teamoffour.lms.domain.enums.ReservationStatus;
import com.teamoffour.lms.exception.BusinessException;
import com.teamoffour.lms.repository.BookRepository;
import com.teamoffour.lms.repository.MemberRepository;
import com.teamoffour.lms.repository.ReservationRepository;
import com.teamoffour.lms.repository.TransactionRepository;
import com.teamoffour.lms.rest.NotificationServiceREST;
import com.teamoffour.lms.service.dto.NotificationEventDTO;
import com.teamoffour.lms.service.dto.ReservationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReservationService implements ReservationInterface {
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationServiceREST notificationServiceREST;
    private final TransactionRepository transactionRepository;

    public ReservationService(MemberRepository memberRepository, BookRepository bookRepository,
                              ReservationRepository reservationRepository, NotificationServiceREST notificationServiceREST, TransactionRepository transactionRepository) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
        this.notificationServiceREST = notificationServiceREST;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public String reserveBook(Long bookId, Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with ID: " + memberId));

        Book book = bookRepository.findBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Book not found with ID: " + bookId));

        if (book.isAvailable()) {
            throw new IllegalArgumentException(
                    "Book '" + book.getTitle() + "' is currently available. " +
                            "Please borrow it directly instead of reserving.");
        }

        if (member.hasReservationFor(book)) {
            throw new IllegalArgumentException(
                    "You already have an active reservation for '" + book.getTitle() + "'");
        }

        if (!member.canReserve()) {
            throw new IllegalArgumentException(
                    "Reservation limit exceeded. Current active reservations: " +
                            member.getActiveReservationCount() +
                            ", Limit: " + member.getMembershipPlan().getMaxReservationsAllowed());
        }

        Reservation reservation = new Reservation(member, book);
        reservationRepository.save(reservation);


        member.addReservation(reservation);
        memberRepository.save(member);

        book.addReservation(reservation);
        bookRepository.save(book);


        int queuePosition = book.getReservations().stream().filter(Reservation::isQueued).toList().size();


        String message = String.format(
                "You have successfully reserved '%s'. " +
                        "Queue position: %d. " +
                        "You will be notified when the book is available for pickup.",
                book.getTitle(),
                queuePosition
        );


        sendNotification(member, message, NotificationType.RESERVATION_CREATED);

        log.info(" Reservation confirmation sent");

        // 9. Return response
        return "Reservation created with id:"
                + reservation.getId()
                + "\n"
                + "Your reservation queue number is:"
                + queuePosition;
    }

    @Override
    public String processReservationPickup(Long reservationId) {
        // 1. Find the reservation
        Reservation reservation = reservationRepository.findReservationById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reservation not found with ID: " + reservationId));

        // 2. Validate reservation is ACTIVE (member was notified and came to pick up)
        if (!reservation.isActive()) {
            throw new IllegalArgumentException(
                    "Reservation #" + reservationId + " cannot be fulfilled. " +
                            "Expected status: ACTIVE, Current status: " + reservation.getStatus());
        }

        Member member = reservation.getMember();
        Book book = reservation.getBook();

        // 3. Check the book actually has copies available
        if (!book.isAvailable()) {
            throw new BusinessException(
                    "Book '" + book.getTitle() + "' is not available for pickup. " +
                            "Status: " + book.getCurrentState().getStateName());
        }

        // 4. Check member hasn't exceeded their borrowing limit
        if (!member.canBorrow()) {
            throw new BusinessException(
                    "Member has exceeded their borrowing limit. " +
                            "Current: " + member.getActiveBorrowCount() +
                            ", Limit: " + member.getMembershipPlan().getBorrowingPolicy().getBorrowingLimit());
        }

        // 5. Decrement book copies (transitions to Unavailable state if copies hit 0)
        book.decrementCopies();

        // 6. Create the transaction — this is an actual borrow
        Transaction transaction = new Transaction(member, book);
        book.addTransaction(transaction);
        member.addTransaction(transaction);

        // 7. Fulfill the reservation
        reservation.fulfill();

        // 8. Sync reservation state back to member and book
        member.updateReservation(reservation);
        book.updateReservation(reservation);

        // 9. Persist everything
        transactionRepository.save(transaction);
        bookRepository.save(book);
        memberRepository.save(member);
        reservationRepository.save(reservation);

        // 10. Notify the member
        sendNotification(member,
                "You have successfully picked up '" + book.getTitle() + "'. " +
                        "Due date: " + transaction.calculateDueDate(),
                NotificationType.BORROWED);

        return "Reservation fulfilled successfully. Transaction ID: " + transaction.getId() +
                "\nPlease note this ID — it is required when returning the book.";
    }

    @Override
    public void expireActiveReservations() {
        List<Reservation> reservationList = reservationRepository.findAll();

        reservationList.stream()
                .filter(r -> r.getStatus() == ReservationStatus.ACTIVE) // only active reservations matter
                .filter(Reservation::isExpired)                         // expired based on date logic
                .forEach(r -> {
                    r.expire();                                         // update domain object
                    Member member = r.getMember();
                    member.updateReservation(r);
                    Book book = r.getBook();
                    book.updateReservation(r);
                    reservationRepository.save(r);
                    memberRepository.save(member);
                    bookRepository.save(book);
                });
    }


    @Override
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::toReservationDTO)
                .toList();
    }

    private ReservationDTO toReservationDTO(Reservation r) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(r.getId());
        dto.setBookId(r.getBook().getId());
        dto.setBookTitle(r.getBook().getTitle());
        dto.setBookIsbn(r.getBook().getIsbn());
        dto.setMemberId(r.getMember().getId());
        dto.setMemberUserName(r.getMember().getUserName());
        dto.setReservationDate(r.getReservationDate());
        dto.setExpiryDate(r.getExpiryDate());
        dto.setStatus(r.getStatus());
        dto.setDaysUntilExpiry(r.getDaysUntilExpiry());
        return dto;
    }

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
