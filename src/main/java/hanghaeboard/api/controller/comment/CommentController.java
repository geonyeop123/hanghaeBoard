package hanghaeboard.api.controller.comment;

import hanghaeboard.api.ApiResponse;
import hanghaeboard.api.controller.comment.request.CreateCommentRequest;
import hanghaeboard.api.service.comment.CommentService;
import hanghaeboard.api.service.comment.response.CreateCommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/v1/boards/{boardId}/comments")
    public ApiResponse<CreateCommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request, @PathVariable Long boardId){
        return ApiResponse.ok(commentService.createComment(request, boardId));
    }

}
