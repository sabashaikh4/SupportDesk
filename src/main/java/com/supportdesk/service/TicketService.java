package com.supportdesk.service;

import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.TicketStatus;
import com.supportdesk.entity.User;
import com.supportdesk.exception.TicketNotFoundException;
import com.supportdesk.repository.TicketRepository;
import com.supportdesk.repository.UserRepository;
import com.supportdesk.request.CreateTicketRequest;
import com.supportdesk.request.UpdateTicketRequest;
import com.supportdesk.response.TicketResponse;
import com.supportdesk.response.UserSummaryResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request , String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCreatedBy(user);
        return mapToResponse(repository.save(ticket));
    }

    public List<TicketResponse> getAllTickets() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TicketResponse getTicketById(Long id) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        return mapToResponse(ticket);
    }

    @Transactional
    public TicketResponse updateTicketStatus(Long id, TicketStatus status) {
        Ticket ticket = findTicketOrThrow(id);
        if (status != null) ticket.setStatus(status);
        return mapToResponse(repository.save(ticket));
    }

    @Transactional
    public TicketResponse updateTicket(Long id, UpdateTicketRequest request) {
        Ticket ticket = findTicketOrThrow(id);
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        return mapToResponse(repository.save(ticket));
    }

    @Transactional
    public void deleteTicket(Long id) {
        Ticket ticket = findTicketOrThrow(id);
        repository.delete(ticket);
    }

    public List<TicketResponse> getTicketsByStatus(TicketStatus status) {
        return repository.findAllByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Ticket findTicketOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .createdBy(ticket.getCreatedBy() != null
                        ? UserSummaryResponse.builder()
                        .id(ticket.getCreatedBy().getId())
                        .name(ticket.getCreatedBy().getName())
                        .build()
                        : null)
                .assignedTo(ticket.getAssignedTo() != null
                        ? UserSummaryResponse.builder()
                        .id(ticket.getAssignedTo().getId())
                        .name(ticket.getAssignedTo().getName())
                        .build()
                        : null)
                .build();
    }

    private UserSummaryResponse mapToUserSummary(User user){
        if (user == null) return null;
        return UserSummaryResponse.builder()
                .id(user.getId())
                .name(user.getName())
                . build();
    }
}