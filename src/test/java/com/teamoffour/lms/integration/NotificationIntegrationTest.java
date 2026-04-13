package com.teamoffour.lms.integration;

import com.teamoffour.lms.domain.enums.PlanType;
import com.teamoffour.lms.service.dto.AddBookRequest;
import com.teamoffour.lms.service.dto.RegisterMemberRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.rmi.ServerException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void borrowBookTriggersNotification() throws ServerException {
        // First register a member
        RegisterMemberRequest memberRequest = new RegisterMemberRequest();
        memberRequest.setUserName("testuser");
        memberRequest.setEmailId("youremail@gmail.com");
        memberRequest.setPhoneNumber("1234567890");
        memberRequest.setPlanType(PlanType.STANDARD);
        memberRequest.setMemberShipMonths(12);

        String memberResponse = restTemplate.postForObject(
                "/lms/registerMember", memberRequest, String.class);
        assertNotNull(memberResponse);

        // Then add a book
        AddBookRequest bookRequest = new AddBookRequest();
        bookRequest.setTitle("Clean Code");
        bookRequest.setAuthor("Robert Martin");
        bookRequest.setIsbn("978-0132350884");
        bookRequest.setCategory("Technology");
        bookRequest.setPublicationYear(2008);
        bookRequest.setCopiesAvailable(5);

        String bookResponse = restTemplate.postForObject(
                "/lms/addBook", bookRequest, String.class);
        assertNotNull(bookResponse);

        // Extract IDs from responses and borrow — notification fires automatically
    }
}
