package com.supportdesk.controller;

import com.supportdesk.entity.TicketStatus;
import com.supportdesk.request.CreateTicketRequest;
import com.supportdesk.request.UpdateStatusRequest;
import com.supportdesk.request.UpdateTicketRequest;
import com.supportdesk.response.TicketResponse;
import com.supportdesk.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request){

        TicketResponse create = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(create);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTicket(
            @RequestParam(required = false) TicketStatus status){
        if (status != null){
            return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
        }
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id){
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateTicketRequest request){
        return ResponseEntity.ok(ticketService.updateTicket(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(@PathVariable Long id,
                                                             @Valid @RequestBody UpdateStatusRequest request){
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id){
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}