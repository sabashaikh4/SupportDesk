package com.supportdesk.service;


import com.supportdesk.entity.Comment;
import com.supportdesk.entity.User;
import com.supportdesk.entity.Ticket;
import com.supportdesk.exception.TicketNotFoundException;
import com.supportdesk.repository.CommentRepository;
import com.supportdesk.repository.TicketRepository;
import com.supportdesk.repository.UserRepository;
import com.supportdesk.request.CreateCommentRequest;
import com.supportdesk.response.CommentResponse;
import com.supportdesk.response.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(Long ticketId, String userEmail , CreateCommentRequest request){

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new TicketNotFoundException(ticketId));


        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found: "+ userEmail));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTicket(ticket);
        comment.setAuthor(user);

        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }


    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTicket(Long ticketId){
       ticketRepository.findById(ticketId)
               .orElseThrow(() -> new TicketNotFoundException(ticketId));

       return commentRepository
               .findByTicketIdOrderByCreatedAtAsc(ticketId)
               .stream()
               .map(this::mapToResponse)
               .toList();
    }


    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .ticketId(comment.getTicket().getId())
                .author(UserSummaryResponse.builder()
                        .id(comment.getAuthor().getId())
                        .name(comment.getAuthor().getName())
                        .build())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
