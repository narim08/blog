package blogpj.blog.controller;

import blogpj.blog.dto.BoardRequestDTO;
import blogpj.blog.dto.BoardResponseDTO;
import blogpj.blog.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /** Create: 게시글 작성 */
    @PostMapping
    public ResponseEntity<BoardResponseDTO> createBoard(@RequestHeader("Username") String username,
                                                        @RequestBody BoardRequestDTO requestDTO) {
        BoardResponseDTO responseDTO = boardService.createBoard(username, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    /** Read: 게시글 조회 */
    //게시글 전체 목록 조회
    @GetMapping
    public ResponseEntity<Page<BoardResponseDTO>> getAllBoards(@PageableDefault(size = 5, sort = "createTime") Pageable pageable) {
        Page<BoardResponseDTO> boards = boardService.getAllBoards(pageable);

        return ResponseEntity.ok(boards);
    }

    //게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getBoardById(@PathVariable Long id) {
        BoardResponseDTO board = boardService.getBoardById(id);

        return ResponseEntity.ok(board);
    }

    //게시글 검색 조회 (제목)
    @GetMapping("/search")
    public ResponseEntity<Page<BoardResponseDTO>> searchBoards(@RequestParam(value = "title", required = false) String title,
                                                               @PageableDefault(size = 5, sort = "createTime") Pageable pageable) {
        Page<BoardResponseDTO> boards = boardService.searchBoardsByTitle(title, pageable);

        return ResponseEntity.ok(boards);
    }


    /** Update: 게시글 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> updateBoard(@RequestHeader("Username") String username,
                                                        @PathVariable Long id,
                                                        @RequestBody BoardRequestDTO requestDTO) {
        BoardResponseDTO board = boardService.updateBoard(username, id, requestDTO);

        return ResponseEntity.ok(board);
    }


    /** Delete: 게시글 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@RequestHeader("Username") String username,
                                            @PathVariable Long id) {
        boardService.deleteBoard(username, id);

        return ResponseEntity.noContent().build(); //본문 없는 응답 자동 생성 (200 ok만)
    }


    //좋아요 기능
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, @RequestHeader("Username") String username) {
        boolean liked = boardService.toggleLike(id, username);
        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", boardService.getLikeCount(id)));
    }

    @GetMapping("/{id}/like/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getLikeCount(id));
    }

    @GetMapping("/tag")
    public ResponseEntity<Page<BoardResponseDTO>> getBoardsByTag(@RequestParam String tag,
                                                                 @PageableDefault(size = 5, sort = "createTime", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<BoardResponseDTO> boards = boardService.searchBoardsByTag(tag, pageable);
        return ResponseEntity.ok(boards);
    }
}
