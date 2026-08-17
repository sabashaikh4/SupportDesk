package com.supportdesk.controller;


import com.supportdesk.request.CreateCommentRequest;
import com.supportdesk.response.CommentResponse;
import com.supportdesk.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long ticketId ,@Valid @RequestBody
                                                      CreateCommentRequest request ){
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
       return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(ticketId ,email, request));

    }

    @GetMapping("/{ticketId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long ticketId){
        return ResponseEntity.ok(commentService.getCommentsByTicket(ticketId));
    }



}
