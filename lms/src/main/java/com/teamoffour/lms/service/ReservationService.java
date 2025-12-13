package com.teamoffour.lms.service;

import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.domain.Member;
import com.teamoffour.lms.domain.Notification;
import com.teamoffour.lms.domain.Reservation;
import com.teamoffour.lms.domain.enums.NotificationType;
import com.teamoffour.lms.domain.enums.ReservationStatus;
import com.teamoffour.lms.repository.BookRepository;
import com.teamoffour.lms.repository.MemberRepository;
import com.teamoffour.lms.repository.ReservationRepository;
import com.teamoffour.lms.service.observer.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.List;

@Service
@Slf4j
public class ReservationService implements ReservationInterface {
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationManager notificationManager;

    public ReservationService(MemberRepository memberRepository, BookRepository bookRepository,
                              ReservationRepository reservationRepository, NotificationManager notificationManager) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
        this.notificationManager = notificationManager;
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

        Notification notification = new Notification(
                member,
                message,
                NotificationType.RESERVATION_CREATED
        );

        notificationManager.notifyObservers(notification);
        log.info(" Reservation confirmation sent");

        // 9. Return response
        return "Reservation created with id:"
                + reservation.getId()
                + "\n"
                + "Your reservation queue number is:"
                + queuePosition;
    }

    @Override
    public String processReservationPickup(Long reservationId) throws ServerException {
        Reservation reservation = reservationRepository.findReservationById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reservation not found with ID: " + reservationId));


        reservation.fulfill();

        reservationRepository.save(reservation);
        Member member = reservation.getMember();
        Book book = reservation.getBook();

        if (!book.isAvailable()) {
            throw new ServerException(
                    "Book '" + book.getTitle() + "' is not available. Status: " + book.getCurrentState().getStateName());
        }

        member.updateReservation(reservation);
        book.updateReservation(reservation);

        bookRepository.save(book);
        memberRepository.save(member);

        return "Reservation fulfilled. Book borrowing successfull.";
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
}
