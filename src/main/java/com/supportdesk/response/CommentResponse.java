package com.supportdesk.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private UserSummaryResponse author;  // who wrote it
    private Long ticketId;               // which ticket
    private LocalDateTime createdAt;
}