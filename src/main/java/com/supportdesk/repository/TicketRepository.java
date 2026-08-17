package com.supportdesk.repository;

import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.TicketStatus;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByStatus (TicketStatus status);
    List<Ticket> findByTitleContainingIgnoreCase(String keyword);

    long countByStatus(TicketStatus status);
    List<Ticket> findAllByCreatedAtBetween(LocalDateTime start, Jsr310JpaConverters.LocalDateConverter end);


}