package org.score.studentscoresystem.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.score.studentscoresystem.dto.ScoreStatisticsDTO;
import org.score.studentscoresystem.entity.Score;
import org.score.studentscoresystem.entity.User;
import org.score.studentscoresystem.mapper.ScoreMapper;
import org.score.studentscoresystem.mapper.UserMapper;
import org.score.studentscoresystem.util.ScoreCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private UserMapper userMapper;

    public Score getScoreById(Long id) {
        return scoreMapper.selectById(id);
    }

    public List<Score> getScoresByStudent(Long studentId) {
        return scoreMapper.selectByStudentId(studentId);
    }

    public List<Score> getScoresByCourse(Long courseId) {
        return scoreMapper.selectByCourseId(courseId);
    }

    public List<Score> searchScores(Long courseId, String keyword, BigDecimal minScore, BigDecimal maxScore) {
        return scoreMapper.selectByCondition(courseId, keyword, minScore, maxScore);
    }

    public boolean saveScore(Score score) {
        // 计算总成绩、等级和绩点
        BigDecimal totalScore = ScoreCalculator.calculateTotalScore(
                score.getUsualScore(), score.getMidtermScore(), score.getFinalScore());
        score.setTotalScore(totalScore);
        score.setGradeLevel(ScoreCalculator.calculateGradeLevel(totalScore));
        score.setGpa(ScoreCalculator.calculateGPA(totalScore));

        Score existing = scoreMapper.selectByCourseAndStudent(score.getCourseId(), score.getStudentId());
        if (existing != null) {
            score.setId(existing.getId());
            return scoreMapper.update(score) > 0;
        } else {
            return scoreMapper.insert(score) > 0;
        }
    }

    public boolean updateScore(Score score) {
        // 重新计算总成绩、等级和绩点
        BigDecimal totalScore = ScoreCalculator.calculateTotalScore(
                score.getUsualScore(), score.getMidtermScore(), score.getFinalScore());
        score.setTotalScore(totalScore);
        score.setGradeLevel(ScoreCalculator.calculateGradeLevel(totalScore));
        score.setGpa(ScoreCalculator.calculateGPA(totalScore));
        
        return scoreMapper.update(score) > 0;
    }

    public List<String> importScoresFromExcel(Long courseId, MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    String studentNo = getCellValue(row.getCell(0));
                    BigDecimal usualScore = new BigDecimal(getCellValue(row.getCell(1)));
                    BigDecimal midtermScore = new BigDecimal(getCellValue(row.getCell(2)));
                    BigDecimal finalScore = new BigDecimal(getCellValue(row.getCell(3)));
                    
                    User student = userMapper.selectByUsername(studentNo);
                    if (student == null) {
                        errors.add("第" + (i + 1) + "行：学号 " + studentNo + " 不存在");
                        continue;
                    }
                    
                    Score score = new Score();
                    score.setCourseId(courseId);
                    score.setStudentId(student.getId());
                    score.setUsualScore(usualScore);
                    score.setMidtermScore(midtermScore);
                    score.setFinalScore(finalScore);
                    
                    saveScore(score);
                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行：数据格式错误 - " + e.getMessage());
                }
            }
        }
        
        return errors;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    public ScoreStatisticsDTO getStatistics(Long courseId) {
        List<Score> scores = scoreMapper.selectByCourseId(courseId);
        if (scores.isEmpty()) {
            return null;
        }

        ScoreStatisticsDTO stats = new ScoreStatisticsDTO();
        stats.setCourseId(courseId);
        stats.setCourseName(scores.get(0).getCourseName());

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal max = scores.get(0).getTotalScore();
        BigDecimal min = scores.get(0).getTotalScore();
        int excellent = 0, good = 0, pass = 0, fail = 0;

        for (Score score : scores) {
            BigDecimal total = score.getTotalScore();
            sum = sum.add(total);
            if (total.compareTo(max) > 0) max = total;
            if (total.compareTo(min) < 0) min = total;

            if (total.compareTo(new BigDecimal("90")) >= 0) excellent++;
            else if (total.compareTo(new BigDecimal("80")) >= 0) good++;
            else if (total.compareTo(new BigDecimal("60")) >= 0) pass++;
            else fail++;
        }

        int total = scores.size();
        stats.setAvgScore(sum.divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));
        stats.setMaxScore(max);
        stats.setMinScore(min);
        stats.setExcellentCount(excellent);
        stats.setGoodCount(good);
        stats.setPassCount(pass);
        stats.setFailCount(fail);
        stats.setTotalCount(total);
        stats.setExcellentRate((double) excellent / total * 100);
        stats.setGoodRate((double) good / total * 100);
        stats.setPassRate((double) pass / total * 100);
        stats.setFailRate((double) fail / total * 100);

        return stats;
    }

    public BigDecimal calculateTotalGPA(Long studentId) {
        List<Score> scores = scoreMapper.selectByStudentId(studentId);
        if (scores.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalGPA = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (Score score : scores) {
            if (score.getGpa() != null && score.getCredit() != null) {
                totalGPA = totalGPA.add(score.getGpa().multiply(score.getCredit()));
                totalCredit = totalCredit.add(score.getCredit());
            }
        }

        if (totalCredit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalGPA.divide(totalCredit, 2, RoundingMode.HALF_UP);
    }
}
