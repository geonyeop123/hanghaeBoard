package hanghaeboard.api.controller.comment;

import hanghaeboard.annotation.AuthCheck;
import hanghaeboard.api.ApiResponse;
import hanghaeboard.api.controller.comment.request.CreateCommentRequest;
import hanghaeboard.api.controller.comment.request.UpdateCommentRequest;
import hanghaeboard.api.service.comment.CommentService;
import hanghaeboard.api.service.comment.response.CreateCommentResponse;
import hanghaeboard.api.service.comment.response.UpdateCommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @AuthCheck
    @PostMapping("/api/v1/boards/{boardId}/comments")
    public ApiResponse<CreateCommentResponse> createComment(
            @Valid @RequestBody CreateCommentRequest request
            , @PathVariable Long boardId
            , @RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        String jwtToken = authorizationHeader.substring(7);
        return ApiResponse.ok(commentService.createComment(request, jwtToken, boardId));
    }

    @AuthCheck
    @PutMapping("/api/v1/boards/{boardId}/comments/{commentId}")
    public ApiResponse<UpdateCommentResponse> updateComment(
            @Valid @RequestBody UpdateCommentRequest request
            , @PathVariable Long boardId
            , @PathVariable Long commentId
            , @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String jwtToken = authorizationHeader.substring(7);
        return ApiResponse.ok(commentService.updateComment(request, jwtToken, commentId));
    }
}
