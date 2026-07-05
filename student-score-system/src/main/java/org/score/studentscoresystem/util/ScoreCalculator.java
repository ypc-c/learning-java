package org.score.studentscoresystem.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ScoreCalculator {

    // 计算总成绩：平时30% + 期中20% + 期末50%
    public static BigDecimal calculateTotalScore(BigDecimal usual, BigDecimal midterm, BigDecimal finalScore) {
        if (usual == null || midterm == null || finalScore == null) {
            return null;
        }
        BigDecimal total = usual.multiply(new BigDecimal("0.3"))
                .add(midterm.multiply(new BigDecimal("0.2")))
                .add(finalScore.multiply(new BigDecimal("0.5")));
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    // 根据总成绩计算等级
    public static String calculateGradeLevel(BigDecimal totalScore) {
        if (totalScore == null) {
            return null;
        }
        if (totalScore.compareTo(new BigDecimal("90")) >= 0) {
            return "优秀";
        } else if (totalScore.compareTo(new BigDecimal("80")) >= 0) {
            return "良好";
        } else if (totalScore.compareTo(new BigDecimal("60")) >= 0) {
            return "及格";
        } else {
            return "不及格";
        }
    }

    // 根据总成绩计算绩点
    public static BigDecimal calculateGPA(BigDecimal totalScore) {
        if (totalScore == null) {
            return null;
        }
        if (totalScore.compareTo(new BigDecimal("90")) >= 0) {
            return new BigDecimal("4.0");
        } else if (totalScore.compareTo(new BigDecimal("80")) >= 0) {
            return new BigDecimal("3.0");
        } else if (totalScore.compareTo(new BigDecimal("60")) >= 0) {
            return new BigDecimal("2.0");
        } else {
            return new BigDecimal("0");
        }
    }
}
