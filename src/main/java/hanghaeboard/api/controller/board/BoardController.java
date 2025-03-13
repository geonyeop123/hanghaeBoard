package hanghaeboard.api.controller.board;

import hanghaeboard.annotation.AuthCheck;
import hanghaeboard.api.ApiResponse;
import hanghaeboard.api.controller.board.request.CreateBoardRequest;
import hanghaeboard.api.controller.board.request.UpdateBoardRequest;
import hanghaeboard.api.service.board.BoardService;
import hanghaeboard.api.service.board.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/api/v1/boards")
    public ApiResponse<List<FindBoardResponse>> getBoards(){
        return ApiResponse.ok(boardService.findAllBoard());
    }

    @AuthCheck
    @PostMapping("/api/v1/boards")
    public ApiResponse<CreateBoardResponse> createBoard(
            @Valid @RequestBody CreateBoardRequest request
            , @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String jwtToken = authorizationHeader.substring(7);

        return ApiResponse.ok(boardService.createBoard(request, jwtToken));
    }

    @GetMapping("/api/v1/boards/{id}")
    public ApiResponse<FindBoardWithCommentResponse> findBoardById(@PathVariable Long id){
        return ApiResponse.ok(boardService.findBoardByIdWithComments(id));
    }

    @AuthCheck
    @PutMapping("/api/v1/boards/{id}")
    public ApiResponse<UpdateBoardResponse> updateBoard(
            @PathVariable Long id
            , @Valid @RequestBody UpdateBoardRequest request
            , @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String jwtToken = authorizationHeader.substring(7);
        return ApiResponse.ok(boardService.updateBoard(request, id, jwtToken));
    }

    @AuthCheck
    @DeleteMapping("/api/v1/boards/{id}")
    public ApiResponse<DeleteBoardResponse> deleteBoard(
            @PathVariable Long id
            , @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String jwtToken = authorizationHeader.substring(7);
        return ApiResponse.ok(boardService.deleteBoard(id, jwtToken));
    }

}
