package com.supportdesk.repository;

import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByStatus (TicketStatus status);
    List<Ticket> findAllByCreatedAtBetween();


}