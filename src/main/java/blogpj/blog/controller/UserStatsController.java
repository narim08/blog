package blogpj.blog.controller;

import blogpj.blog.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserStats(@RequestHeader("Username") String username) {
        Map<String, Object> stats = new HashMap<>();

        // 조회수 통계
        List<Map<String, Object>> viewStats = userService.getRecentViewStats(username);
        stats.put("viewStats", viewStats);

        // 게시글 작성 통계
        List<Map<String, Object>> postStats = userService.getRecentPostStats(username);
        stats.put("postStats", postStats);

        // 목표 진행률
        Map<String, Object> goalProgress = userService.getPostGoalProgress(username);
        stats.put("goalProgress", goalProgress);

        return ResponseEntity.ok(stats);
    }
    /**
     * 사용자의 월간/주간 목표 게시물 수를 업데이트합니다.
     * @param username 사용자 식별을 위한 이름
     * @param request 업데이트할 목표치가 담긴 DTO
     * @return 성공 메시지
     */
    @PatchMapping("/goals")
    public ResponseEntity<Map<String, String>> updateUserGoals(
            @RequestHeader("Username") String username,
            @RequestBody GoalUpdateRequest request) {

        userService.updateUserGoals(username, request.getMonthlyGoal(), request.getWeeklyGoal());

        Map<String, String> response = new HashMap<>();
        response.put("message", "목표가 성공적으로 업데이트되었습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 목표 업데이트 요청을 위한 DTO(Data Transfer Object)
     */
    @Data
    public static class GoalUpdateRequest {
        private Integer monthlyGoal;
        private Integer weeklyGoal;
    }
}
