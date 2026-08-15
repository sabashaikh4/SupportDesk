package com.supportdesk.controller;


import com.supportdesk.request.CreateCommentRequest;
import com.supportdesk.response.CommentResponse;
import com.supportdesk.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")

public class CommentController {

    private final CommentService commentService;

    public ResponseEntity<CommentResponse> addComment(@Valid @RequestParam
                                                      CreateCommentRequest request ,@PathVariable Long TicketId ){
        CommentResponse create = commentService.addComment(TicketId , request , email);

    }




}
