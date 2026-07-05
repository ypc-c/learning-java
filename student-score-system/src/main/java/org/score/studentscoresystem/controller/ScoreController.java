package org.score.studentscoresystem.controller;

import org.score.studentscoresystem.common.Result;
import org.score.studentscoresystem.dto.ScoreStatisticsDTO;
import org.score.studentscoresystem.entity.Score;
import org.score.studentscoresystem.service.ScoreService;
import org.score.studentscoresystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
@CrossOrigin
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public Result<Score> getScoreById(@PathVariable Long id) {
        return Result.success(scoreService.getScoreById(id));
    }

    @GetMapping("/my-scores")
    public Result<Map<String, Object>> getMyScores(@RequestHeader("Authorization") String token,
                                                     @RequestParam(required = false) String sortBy,
                                                     @RequestParam(required = false) String order) {
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        List<Score> scores = scoreService.getScoresByStudent(userId);
        
        // 排序
        if ("totalScore".equals(sortBy)) {
            scores.sort((a, b) -> "asc".equals(order) ? 
                a.getTotalScore().compareTo(b.getTotalScore()) : 
                b.getTotalScore().compareTo(a.getTotalScore()));
        } else if ("credit".equals(sortBy)) {
            scores.sort((a, b) -> "asc".equals(order) ? 
                a.getCredit().compareTo(b.getCredit()) : 
                b.getCredit().compareTo(a.getCredit()));
        }
        
        BigDecimal totalGPA = scoreService.calculateTotalGPA(userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("scores", scores);
        data.put("totalGPA", totalGPA);
        
        return Result.success(data);
    }

    @GetMapping("/course/{courseId}")
    public Result<List<Score>> getScoresByCourse(@PathVariable Long courseId) {
        return Result.success(scoreService.getScoresByCourse(courseId));
    }

    @GetMapping("/search")
    public Result<List<Score>> searchScores(@RequestParam Long courseId,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) BigDecimal minScore,
                                            @RequestParam(required = false) BigDecimal maxScore) {
        return Result.success(scoreService.searchScores(courseId, keyword, minScore, maxScore));
    }

    @PostMapping("/save")
    public Result<?> saveScore(@RequestBody Score score) {
        boolean success = scoreService.saveScore(score);
        return success ? Result.success() : Result.error("保存失败");
    }

    @PutMapping("/update")
    public Result<?> updateScore(@RequestBody Score score) {
        boolean success = scoreService.updateScore(score);
        return success ? Result.success() : Result.error("更新失败");
    }

    @PostMapping("/import")
    public Result<?> importScores(@RequestParam Long courseId, @RequestParam("file") MultipartFile file) {
        try {
            List<String> errors = scoreService.importScoresFromExcel(courseId, file);
            if (errors.isEmpty()) {
                return Result.success();
            } else {
                return Result.error(500, "部分数据导入失败：" + String.join("; ", errors));
            }
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    @GetMapping("/statistics/{courseId}")
    public Result<ScoreStatisticsDTO> getStatistics(@PathVariable Long courseId) {
        ScoreStatisticsDTO stats = scoreService.getStatistics(courseId);
        return stats != null ? Result.success(stats) : Result.error("暂无数据");
    }
}
