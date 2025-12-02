package com.teamoffour.lms.service;


import com.teamoffour.lms.domain.Book;
import com.teamoffour.lms.domain.Member;
import com.teamoffour.lms.repository.BookRepository;
import com.teamoffour.lms.repository.MemberRepository;
import com.teamoffour.lms.service.observer.NotificationManager;
import com.teamoffour.lms.service.strategy.IMembershipPlan;
import com.teamoffour.lms.service.strategy.PremiumPlan;
import com.teamoffour.lms.service.strategy.StandardPlan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.rmi.ServerException;
import java.time.LocalDate;
import java.util.Optional;


@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private NotificationManager notificationManager;


    @Test
    void borrowBookWhenBookIsAvailableAndMemberShipIsStandard() throws ServerException {
        Member member = new Member();
        member.setId(1L);
        member.setEmailId("abc@gmail.com");
        member.setPhoneNumber("0912381293123");
        member.setMembershipStartDate(LocalDate.now());
        member.setMembershipEndDate(LocalDate.of(2025,5,23));

        IMembershipPlan iMembershipPlan = new StandardPlan();
        member.setMembershipPlan(iMembershipPlan);

        Book book = new Book();
        book.setId(1L);
        book.setIsbn("123456789");
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setCategory("Category");
        book.setPublicationYear(2024);
        book.setCopiesAvailable(20);

        Mockito.when(memberRepository.findMemberById(Mockito.any())).thenReturn(Optional.of(member));
        Mockito.when(bookRepository.findBookById(Mockito.any())).thenReturn(Optional.of(book));

        Assertions.assertNotNull(transactionService.borrowBook(1L, 1L));

    }


    @Test
    void borrowBookWhenBookIsAvailableAndMemberShipIsPremium() throws ServerException {
        Member member = new Member();
        member.setId(1L);
        member.setEmailId("abc@gmail.com");
        member.setPhoneNumber("0912381293123");
        member.setMembershipStartDate(LocalDate.now());
        member.setMembershipEndDate(LocalDate.of(2025,5,23));

        IMembershipPlan iMembershipPlan = new PremiumPlan();
        member.setMembershipPlan(iMembershipPlan);

        Book book = new Book();
        book.setId(1L);
        book.setIsbn("123456789");
        book.setTitle("Title");
        book.setAuthor("Author");

        book.setCategory("Category");
        book.setPublicationYear(2024);
        book.setCopiesAvailable(20);

        Mockito.when(memberRepository.findMemberById(Mockito.any())).thenReturn(Optional.of(member));
        Mockito.when(bookRepository.findBookById(Mockito.any())).thenReturn(Optional.of(book));

        Assertions.assertNotNull(transactionService.borrowBook(1L, 1L));

    }




}